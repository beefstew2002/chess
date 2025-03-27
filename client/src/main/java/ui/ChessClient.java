package ui;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Locale;

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
            ChessGame theGame = null;
            for (GameData game : allGames) {
                if (game.gameID() == id) {
                    theGame = game.game();
                }
            }

            //code to display the game will go here

            return theGame.getBoard().toString();

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
