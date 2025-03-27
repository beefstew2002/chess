package ui;

import chess.ChessGame;
import chess.ChessPiece;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import ui.EscapeSequences.*;

public class ChessClient {

    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private AuthData user;
    private Repl notificationHandler;

    public ChessClient(String serverUrl, Repl notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        user = new AuthData("", "");
        this.notificationHandler = notificationHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length >= 3) {
            state = State.SIGNEDIN;
            AuthData user = server.register(params[0], params[1], params[2]);
            return String.format("You signed in as %s", user.username());
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }
    public String login(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length >= 2) {
            user = server.login(params[0], params[1]);
            state = State.SIGNEDIN;
            return String.format("You signed in as %s", user.username());
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }
    public String logout() throws ResponseException {
        assertSignedIn();
        state = State.SIGNEDOUT;
        server.logout(user.authToken());
        return String.format("You signed out as %s", user.username());
    }
    public String create(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 1) {
            state = State.SIGNEDIN;
            int gameId = server.create(params[0], user.authToken()).gameID();
            return String.format("You created a new game %s with ID number %d", params[0], gameId);
        }
        throw new ResponseException(400, "Expected: <Name>");
    }
    public String list() throws ResponseException {
        assertSignedIn();
        var gameList = server.list(user.authToken()).games();
        String s = "";
        for (GameData game : gameList) {
            s += game.gameName() + "\n";

            s += "     White";
            s += (game.whiteUsername() == null) ? " empty" : ": " + game.whiteUsername();
            s += "\n";

            s += "     Black";
            s += (game.blackUsername() == null) ? " empty" : ": " + game.blackUsername();
            s += "\n";

            s += "     " + "ID: " + game.gameID() + "\n";
            s += "\n";
        }
        return s;
    }
    public String join(String... params) throws ResponseException {
        assertSignedIn();
        int gameId = Integer.parseInt(params[0]);
        if (params.length >= 2) {
            try {
                server.join(gameId, params[1], user.authToken());
                state = State.INGAME;
            } catch (NumberFormatException e) {
                return "You have to use the game's ID number, not its name";
            }
            return "You joined the game\n"+displayGame(gameId, params[1]);
        }
        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
    }
    public String observe(String... params) throws ResponseException {
        assertSignedIn();
        int gameId = Integer.parseInt(params[0]);

        var allGames = server.list(user.authToken()).games();
        GameData game = null;
        for (GameData g : allGames) {
            if (g.gameID() == gameId) {
                game = g;
            }
        }
        if (game.blackUsername().equals(user.username())) {
            return displayGame(gameId, 1);
        }
        return displayGame(gameId);
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to play chess
                    quit - playing chess
                    help - See your options
                    """;
        }else if (state == State.SIGNEDIN) {
            return """
                    create <NAME> - a game
                    list - games
                    join <ID> [WHITE|BLACK] - a game
                    observe <ID> - a game
                    logout - when you are done
                    quit - playing chess
                    help - with possible commands
                    """;
        }else if (state == State.INGAME) {
            return """
                    You can't play yet, silly.
                    We're not built for that
                    """;
        }
        return "You cheated. Someone is coming";
    }

    public String displayGame(int id, int pov) {
        //pov = 0 means white, pov = 1 means black
        try {
            var allGames = server.list(user.authToken()).games();
            GameData game = null;
            for (GameData g : allGames) {
                if (g.gameID() == id) {
                    game = g;
                }
            }

            String bpc = EscapeSequences.SET_TEXT_COLOR_BLUE;//Black piece color
            String bsc = EscapeSequences.SET_BG_COLOR_BLACK;//Black square color
            String wpc = EscapeSequences.SET_TEXT_COLOR_RED;//White piece color
            String wsc = EscapeSequences.SET_TEXT_COLOR_WHITE;//White square color
            String blackText = EscapeSequences.SET_TEXT_COLOR_BLACK;//Black text
            String whiteText = EscapeSequences.SET_TEXT_COLOR_WHITE;
            String grayBack = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
            String resetBack = EscapeSequences.RESET_BG_COLOR;
            String resetText = EscapeSequences.RESET_TEXT_COLOR;

            String blackUsername = game.blackUsername() == null ? "[no one yet]" : game.blackUsername();
            String whiteUsername = game.whiteUsername() == null ? "[no one yet]" : game.whiteUsername();

            //code to display the game will go here
            String s = "\n";
            //Reset colors
            s += EscapeSequences.RESET_TEXT_COLOR;

            //Add the top username: black's if it's white's POV, white's otherwise
            s += (pov == 0) ? bpc + blackUsername + "\n" : wpc + whiteUsername + "\n" ;

            //Board as an array of strings
            ArrayList<ArrayList<String>> boardArray = new ArrayList<ArrayList<String>>();
            //Initialize it as empty
            for (int y=0; y<10; y++) {
                boardArray.add(new ArrayList<String>());
                for (int x=0; x<10; x++) {
                    boardArray.get(y).add("   ");
                }
            }
            //Add borders
            String wideSpace = "\u2003";
            //Top border
            boardArray.get(0).set(0,grayBack+"   ");
            boardArray.get(0).set(9,grayBack+"   ");
            for (int x=1; x<9; x++) {
                char let = (char) (x + 96);
                String row = grayBack+blackText+" "+Character.toString(let)+" ";
                boardArray.get(0).set(x, row);
            }
            //Bottom border
            boardArray.get(9).set(0,grayBack+"   ");
            boardArray.get(9).set(9,grayBack+"   ");
            for (int x=1; x<9; x++) {
                char let = (char) (x + 96);
                String row = grayBack+blackText+" "+Character.toString(let)+" ";
                boardArray.get(9).set(x, row);
            }
            //Left border
            for (int y=1; y<9; y++) {
                String row = grayBack+blackText+" "+(9-y)+" ";
                boardArray.get(y).set(0, row);
            }
            //Right border
            for (int y=1; y<9; y++) {
                String row = grayBack+blackText+" "+(9-y)+" ";
                boardArray.get(y).set(9, row);
            }

            //Add pieces
            for (int y=1; y<=8; y++) {
                for (int x=1; x<=8; x++) {
                    ChessPiece piece = game.game().getBoard().getPiece(9-y, x);
                    if (piece != null) {
                        String color = switch (piece.getTeamColor()) {
                            case WHITE -> wpc;
                            case BLACK -> bpc;
                        };
                        String pieceChar = switch(piece.getPieceType()) {
                            case KING -> "K";
                            case QUEEN -> "Q";
                            case ROOK -> "R";
                            case KNIGHT -> "N";
                            case BISHOP -> "B";
                            case PAWN -> "P";
                        };
                        boardArray.get(y).set(x, resetText+color+" "+pieceChar+" ");
                    }else{
                        boardArray.get(y).set(x, "   ");
                    }
                }
            }

            //Add board colors
            for (int y=1; y<=8; y++) {
                for (int x=1; x<=8; x++) {
                    String color = (x % 2 == y % 2) ? wsc : bsc;
                    boardArray.get(y).set(x, resetBack+color+boardArray.get(y).get(x));
                }
            }

            //Flip the board if it's black's POV
            if (pov == 1) {
                ArrayList<ArrayList<String>> flippedBoard = new ArrayList<>();
                for (int y=9; y>=0; y--) {
                    flippedBoard.add(new ArrayList<String>());
                    for (int x=9; x>=0; x--) {
                        flippedBoard.get(9-y).add(boardArray.get(y).get(x));
                    }
                }
                boardArray = flippedBoard;
            }

            //Add the board to the string
            for (int y=0; y<10; y++) {
                for (int x=0; x<10; x++) {
                    s += boardArray.get(y).get(x);
                }
                s += resetBack + "\n";
            }

            //Add the bottom username: white's if it's white's POV, black's otherwise
            s += (pov == 1) ? bpc + blackUsername + "\n" : wpc + whiteUsername + "\n" ;

            return s;

        } catch (ResponseException e) {
            return "you must not be authorized to view this or smth";
        }
    }

    public String displayGame(int id, String color) {
        if (color.toUpperCase().equals("BLACK")) {
            return displayGame(id, 1);
        }
        return displayGame(id, 0);
    }
    public String displayGame(int id) {
        return displayGame(id, 0);
    } // default pov is 0

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        } else if (state == State.INGAME) {
            throw new ResponseException(400, "You must exit the game");
        }
    }
    private void assertSignedOut() throws ResponseException {
        if (state != State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign out");
        }
    }
    private void assertInGame() throws ResponseException {
        if (state != State.INGAME) {
            throw new ResponseException(400, "You must be in a game");
        }
    }
}
