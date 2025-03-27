package ui;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;

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
                //case "quit" -> quit();
                case "login" -> login(params);
                case "register" -> register(params);
                //case "logout" -> logout();
                //case "create" -> create(params);
                //case "list" -> list();
                //case "join" -> join(params);
                //case "observe" -> observe(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        //assertSignedIn();
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
            state = State.SIGNEDIN;
            AuthData user = server.login(params[0], params[1]);
            return String.format("You signed in as %s", user.username());
        }
        throw new ResponseException(400, "Expected: <username> <password>");
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
