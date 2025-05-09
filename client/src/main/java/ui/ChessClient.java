package ui;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import server.ServerFacade;
import websocket.WebSocketFacade;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;

import java.util.*;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import static java.lang.Integer.parseInt;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private AuthData user;
    private final Repl notificationHandler;
    private GameData myGame;
    private int myPov;
    private WebSocketFacade ws;
    private Gson serializer = new Gson();
    private boolean tryingToResign = false;

    public ChessClient(String serverUrl, Repl notificationHandler) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(this.serverUrl);
        user = new AuthData("", "");
        this.notificationHandler = notificationHandler;
        myPov = 0;
        try {
            ws = new WebSocketFacade(notificationHandler);
        } catch (Exception e) {
            System.out.println("Couldn't initialize websocket");
            System.out.println(e);
        }
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
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "resign" -> attemptResign();
                case "no" -> cancelResign();
                case "move" -> makeMove(params);
                case "highlight" -> highlight(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length >= 3) {
            try {
                user = server.register(params[0], params[1], params[2]);
                state = State.SIGNEDIN;
            } catch (Exception e) {
                return "Try again, that username might be taken";
            }
            return String.format("You signed in as %s", user.username());
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }
    public String login(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length >= 2) {
            try {
                user = server.login(params[0], params[1]);
                state = State.SIGNEDIN;
                return String.format("You signed in as %s", user.username());
            } catch (Exception e) {
                return "Wrong password";
            }
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
            try {
                server.create(params[0], user.authToken());
                int index = server.list(user.authToken()).games().size()-1;
                return String.format("You created a new game %s with index number %d", params[0], index);
            } catch (Exception e) {
                return "Another game with that name already exists";
            }
        }
        throw new ResponseException(400, "Expected: <Name>");
    }
    public String list() throws ResponseException {
        assertSignedIn();
        var gameList = server.list(user.authToken()).games();
        String s = "";
        int index = 0;
        for (GameData game : gameList) {
            s += game.gameName() + "\n";
            s += "     White";
            s += (game.whiteUsername() == null) ? " empty" : ": " + game.whiteUsername();
            s += "\n";
            s += "     Black";
            s += (game.blackUsername() == null) ? " empty" : ": " + game.blackUsername();
            s += "\n";
            s += "     " + "Index: " + index + "\n";
            s += "\n";
            index++;
        }
        if (index == 0) {
            s += "No games have been created yet";
        }
        return s;
    }
    public int getGameId(int index) throws ResponseException{
        var gameList = server.list(user.authToken()).games();
        int i = 0;
        for (GameData game : gameList) {
            if (i == index) {
                return game.gameID();
            }
            i++;
        }
        throw new ResponseException(400, "That game doesn't exist");
    }
    public String join(String... params) throws ResponseException {
        assertSignedIn();
        int gameId;
        try {
            gameId = getGameId(parseInt(params[0]));
        } catch (NumberFormatException e) {
            return "That's not a number";
        }
        if (params.length >= 2) {
            try {
                if (!(params[1].equalsIgnoreCase("white") || params[1].equalsIgnoreCase("black"))) {
                    return "Expected: <ID> [WHITE|BLACK]";
                }
                server.join(gameId, params[1], user.authToken());
                ws.send(connectCommand(gameId));
                state = State.INGAME;
                //Load in the game
                var allGames = server.list(user.authToken()).games();
                for (GameData g : allGames) {
                    if (g.gameID() == gameId) {
                        myGame = g;
                    }
                }
                if (myGame == null) {return "That game doesn't exist";}
                if (params[1].equalsIgnoreCase("black")) {myPov = 1;}
            } catch (NumberFormatException e) {
                return "You have to use the game's ID number, not its name";
            } catch (Exception e) {
                return "That spot is taken, or something else went wrong";
            }
            return "You joined the game\n"/*+displayGame()*/;
        }
        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
    }
    public String observe(String... params) throws ResponseException {
        assertSignedIn();
        int gameId;
        try {
            gameId = getGameId(parseInt(params[0]));
        } catch (NumberFormatException e) {
            return "That's not a number";
        } catch (Exception e) {
            throw new ResponseException(400, "Expected: observe <ID>");
        }
        var allGames = server.list(user.authToken()).games();
        for (GameData g : allGames) {
            if (g.gameID() == gameId) {
                myGame = g;
            }
        }
        if (myGame == null) {
            return "That game doesn't exist";
        }
        try {
            ws.send(connectCommand(gameId));
        } catch (Exception e) {
            throw new ResponseException(999, "couldn't connect to websocket");
        }
        state = State.INGAME;
        if (user.username().equals(myGame.blackUsername())) {
            return displayGame();
        }
        return "Observe";
    }
    //Game commands
    public String redraw() throws ResponseException {
        assertInGame();
        return displayGame();
    }
    public String leave() throws ResponseException {
        assertInGame();
        try {
            ws.send(leaveCommand());
        } catch (Exception e) {
            throw new ResponseException(999, "couldn't connect to websocket to leave");
        }
        state = State.SIGNEDIN;
        myPov = 0;
        myGame = null;
        return "You left the game";
    }
    public String attemptResign() throws ResponseException{
        if (!tryingToResign) {
            tryingToResign = true;
            return "Are you sure you want to forfeit the game? If so, type 'resign' again";
        } else {
            return resign();
        }
    }
    public String cancelResign() {
        if (tryingToResign) {
            tryingToResign = false;
            return "Got it, you're *not* trying to resign";
        } else {
            return "? What do you mean, 'no'?";
        }
    }
    public String resign() throws ResponseException {
        assertInGame();
        try {
            ws.send(resignCommand());
        } catch (Exception e) {
            throw new ResponseException(999, "couldn't connect to websocket to resign");
        }
        state = State.SIGNEDIN;
        return "";
    }
    public String makeMove(String...params) throws ResponseException {
        assertInGame();
        ChessPosition startPos = posFromString(params[0]);
        ChessPosition endPos = posFromString(params[1]);
        ChessMove move = new ChessMove(startPos, endPos);
        try {
            ws.send(moveCommand(move));
        } catch (Exception e) {
            throw new ResponseException(999, "couldn't connect to websocket to move");
        }
        if (myGame.game().isTurnValid(move)) {
            return "You made move " + move.toString();
        } else {
            return "";
        }
    }
    public String highlight(String...params) throws ResponseException {
        ChessPosition target = posFromString(params[0]);
        //Get a list of all legal moves
        Collection<ChessMove> legalMoves = myGame.game().validMoves(target);
        //Print out the list with those highlighted squares
        return displayGame(target, legalMoves);
    }
    public String help() {
        if (tryingToResign) {
            cancelResign();
        }
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
                    redraw - redraw the chess board
                    leave - leave the game
                    move [ROW1] [COL1] [ROW2] [COL2] - make a move
                    resign - resign the game
                    highlight [ROW] [COL] - highlight the legal moves available to that piece
                    """;
        }
        return "You cheated. Someone is coming";
    }

    public String connectCommand(int gameId) {
        return serializer.toJson(new ConnectCommand(user.authToken(), gameId));
    }
    public String leaveCommand() {
        return serializer.toJson(new LeaveCommand(user.authToken(), myGame.gameID()));
    }
    public String resignCommand() {
        return serializer.toJson(new ResignCommand(user.authToken(), myGame.gameID()));
    }
    public String moveCommand(ChessMove move) {
        return serializer.toJson(new MakeMoveCommand(user.authToken(), myGame.gameID(), move));
    }

    public void loadGame(GameData game) {myGame = game;}
    private ChessPosition posFromString(String pos) throws ResponseException{
        if (pos.length() != 2 || !isLetter(pos.charAt(0)) || !isDigit(pos.charAt(1))) {throw new ResponseException(999, "Invalid input");}
        int row = pos.charAt(1) - '0';
        int col = switch(pos.toLowerCase().charAt(0)) {
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            default -> 0;
        };
        if (col == 0) {throw new ResponseException(999, "Invalid input");}
        return new ChessPosition(row,col);
    }
    private String getPieceChar(ChessPiece.PieceType type) {
        return switch(type) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case KNIGHT -> "N";
            case BISHOP -> "B";
            case PAWN -> "P";
        };
    }
    private ArrayList<ArrayList<String>> flipBoard(ArrayList<ArrayList<String>> oldBoard) {
        ArrayList<ArrayList<String>> flippedBoard = new ArrayList<>();
        for (int y=9; y>=0; y--) {
            flippedBoard.add(new ArrayList<>());
            for (int x=9; x>=0; x--) {
                flippedBoard.get(9-y).add(oldBoard.get(y).get(x));
            }
        }
        return flippedBoard;
    }
    private String displaySquare(GameData game, int x, int y, int highlighted) {
        String bpc = EscapeSequences.SET_TEXT_COLOR_BLUE;//Black piece color
        String bsc = EscapeSequences.SET_BG_COLOR_BLACK;//Black square color
        String wpc = EscapeSequences.SET_TEXT_COLOR_RED;//White piece color
        String wsc = EscapeSequences.SET_TEXT_COLOR_WHITE;//White square color
        String highlightDark = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
        String highlightLight = EscapeSequences.SET_BG_COLOR_GREEN;
        String highlightPiece = EscapeSequences.SET_BG_COLOR_YELLOW;
        String blackText = EscapeSequences.SET_TEXT_COLOR_BLACK;//Black text
        String grayBack = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        String resetBack = EscapeSequences.RESET_BG_COLOR;
        String resetText = EscapeSequences.RESET_TEXT_COLOR;

        String s;
        //Initialize it as empty
        s = "   ";
        //Corners
        if ((y == 0 || y == 9) && (x == 0 || x == 9)) {
            s = grayBack+"   ";
        }
        //Top and bottom border
        if ((y == 0 || y == 9) && (x >= 1 && x <= 8)) {
            char let = (char) (x + 96);
            String row = grayBack+blackText+" "+let+" ";
            s = row;
        }
        //Left and right borders
        if ((x == 0 || x == 9) && (y >= 1 && y <= 8)) {
            String row = grayBack+blackText+" "+(9-y)+" ";
            s = row;
        }
        //Add pieces
        if (x >= 1 && x <= 8 && y >= 1 && y <= 8) {
            ChessPiece piece = game.game().getBoard().getPiece(9-y, x);
            if (piece != null) {
                String color = switch (piece.getTeamColor()) {case WHITE -> wpc; case BLACK -> bpc;};
                String pieceChar = getPieceChar(piece.getPieceType());
                s = resetText+color+" "+pieceChar+" ";
            }else{
                s = "   ";
            }
            //Add checker colors
            String color = switch (highlighted) {
                //case 0 -> (x % 2 == y % 2) ? wsc : bsc;
                case 1 -> (x % 2 == y % 2) ? highlightLight : highlightDark;
                case 2 -> highlightPiece;
                default -> (x % 2 == y % 2) ? wsc : bsc;
            };
            s = resetBack + color + s;
        }
        return s;
    }
    private String displayGame(ChessPosition startPosition, Collection<ChessMove> legalMoves) {
        String bpc = EscapeSequences.SET_TEXT_COLOR_BLUE;//Black piece color
        String wpc = EscapeSequences.SET_TEXT_COLOR_RED;//White piece color
        String resetBack = EscapeSequences.RESET_BG_COLOR;

        GameData game = myGame;
        int pov = myPov;
        String blackUsername = game.blackUsername() == null ? "[no one yet]" : game.blackUsername();
        String whiteUsername = game.whiteUsername() == null ? "[no one yet]" : game.whiteUsername();

        //ChessPosition startPosition = null;
        Collection<ChessPosition> endPositions = null;
        if (legalMoves != null && legalMoves.isEmpty()) {legalMoves = null;}
        if (legalMoves != null) {
            //startPosition = legalMoves.iterator().next().getStartPosition();
            endPositions = new HashSet<>();
            for (ChessMove legalMove : legalMoves) {
                endPositions.add(legalMove.getEndPosition());
            }
        }

        //code to display the game will go here
        String s = "\n";
        //Reset colors
        s += EscapeSequences.RESET_TEXT_COLOR;
        //Add the top username: black's if it's white's POV, white's otherwise
        s += (pov == 0) ? bpc + blackUsername + "\n" : wpc + whiteUsername + "\n" ;
        //Board as an array of strings
        ArrayList<ArrayList<String>> boardArray = new ArrayList<>();
        //Initialize it as empty
        int highlighted;
        for (int y=0; y<10; y++) {
            boardArray.add(new ArrayList<>());
            for (int x=0; x<10; x++) {
                //Highlight if necessary
                highlighted = 0;
                ChessPosition pos = new ChessPosition(9-y, x);
                if (startPosition != null && pos.equals(startPosition)) {
                    highlighted = 2;
                }
                if (legalMoves != null) {
                    if (endPositions.contains(pos)) {
                        highlighted = 1;
                    }
                }
                boardArray.get(y).add(displaySquare(game,x,y,highlighted));
            }
        }
        //Flip the board if it's black's POV
        if (pov == 1) {boardArray = flipBoard(boardArray);}
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
    }
    public String displayGame() {
        return displayGame(null, null);
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        } else if (state == State.INGAME) {
            throw new ResponseException(400, "You must exit the game");
        }
    }
    private void assertSignedOut() throws ResponseException {
        if (state != State.SIGNEDOUT) {throw new ResponseException(400, "You must sign out");}
    }
    private void assertInGame() throws ResponseException {
        if (state != State.INGAME) {throw new ResponseException(400, "You're not in a game");}
    }
}
