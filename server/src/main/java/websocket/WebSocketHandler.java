package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import dataaccess.SQLUserDAO;
import dataaccess.exceptions.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

@WebSocket
public class WebSocketHandler {

    private SQLUserDAO udao;
    private SQLAuthDAO adao;
    private SQLGameDAO gdao;
    private Gson serializer;
    private WebSocketSessions sessions;

    public WebSocketHandler() {
        sessions = new WebSocketSessions();
        udao = new SQLUserDAO();
        adao = new SQLAuthDAO();
        gdao = new SQLGameDAO();
        serializer = new Gson();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {

        try {
            UserGameCommand command = serializer.fromJson(message, UserGameCommand.class);

            //Verify auth
            if (!adao.verifyAuth(command.getAuthToken())) {
                sendMessage(error("Server: you're not authorized"), session);
                return;
            }

            String username = adao.getAuth(command.getAuthToken()).username();

            //Save the session in the WebSocketSessions: saveSession(command.getGameID(), session)
            sessions.addSessionToGame(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> {
                    command = serializer.fromJson(message, ConnectCommand.class);
                    connect(session, username, (ConnectCommand) command);
                }
                case MAKE_MOVE -> {
                    command = serializer.fromJson(message, MakeMoveCommand.class);
                    makeMove(session, username, (MakeMoveCommand) command);
                }
                case LEAVE -> {
                    command = serializer.fromJson(message, LeaveCommand.class);
                    leaveGame(session, username, (LeaveCommand) command);
                }
                case RESIGN -> {
                    command = serializer.fromJson(message, ResignCommand.class);
                    resign(session, username, (ResignCommand) command);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //It doesn't like the sendMessage line. Where is that supposed to be defined? Do I write it?
            //sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
        }
    }
    @OnWebSocketError
    public void onError(Throwable throwable) throws Throwable {
        //idk what this function is supposed to do, is this all?
        throw throwable;
    }

    public void connect(Session session, String username, ConnectCommand command) throws Exception {
        //Verify auth
        if (!adao.verifyAuth(command.getAuthToken())) {
            sendMessage(error("Server: you're not authorized"), session);
            return;
        }

        //Verify the game ID
        try {
            gdao.getGame(command.getGameID());
        } catch (DataAccessException e) {
            sendMessage(error("Server: That game doesn't exist!"), session);
            return;
        }

        String whoAreThey = "observer";
        if (username.equals(gdao.getGame(command.getGameID()).whiteUsername())) {
            whoAreThey = "white";
        }else if (username.equals(gdao.getGame(command.getGameID()).blackUsername())) {
            whoAreThey = "black";
        }

        broadcastMessage(command.getGameID(), notification(username + " joined the game as " + whoAreThey), session);

        //Loading a game
        sendMessage(loadGameMessage(command.getGameID()), session);
    }
    public void makeMove(Session session, String username, MakeMoveCommand command) throws Exception {
        int gameId = command.getGameID();
        ChessMove move = command.getMove();
        GameData game = gdao.getGame(gameId);
        String currentPlayerUsername = switch(game.game().getTeamTurn()) {
            case WHITE -> game.whiteUsername();
            case BLACK -> game.blackUsername();
        };
        if (game.game().isTurnValid(move) && username.equals(currentPlayerUsername)) {
            try {
                game.game().makeMove(move);
            } catch (InvalidMoveException e) {
                sendMessage(error(e.toString()), session);
                return;
            }
            gdao.updateGame(game);
            broadcastMessage(gameId, notification(username + " made move " + move.toString()), session);
            //Check checking
            String checkMessage = null;if (game.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
                checkMessage = "White is in checkmate. Black wins!";
                game.game().declareWinner(ChessGame.TeamColor.BLACK);
                gdao.updateGame(game);
            }else if (game.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
                checkMessage = "Black is in checkmate. White wins!";
                game.game().declareWinner(ChessGame.TeamColor.WHITE);
                gdao.updateGame(game);
            } else if (game.game().isInCheck(ChessGame.TeamColor.WHITE)) {
                checkMessage = "White is in check";
            }else if (game.game().isInCheck(ChessGame.TeamColor.BLACK)) {
                checkMessage = "Black is in check";
            }
            if (checkMessage != null) {
                broadcastMessage(gameId, notification(checkMessage), null);
            }

            broadcastMessage(gameId, loadGameMessage(game), null);
        } else {
            sendMessage(error("That move is illegal"), session);
        }
    }
    public void leaveGame(Session session, String username, LeaveCommand command) throws Exception {
        //Set the player name of that side to null
        int gameId = command.getGameID();
        GameData game = gdao.getGame(gameId);
        if (username.equals(game.whiteUsername())) {
            game = new GameData(gameId, null, game.blackUsername(), game.gameName(), game.game());
        }
        if (username.equals(game.blackUsername())) {
            game = new GameData(gameId, game.whiteUsername(), null, game.gameName(), game.game());
        }
        gdao.updateGame(game);

        //Remove the session from the map
        sessions.removeSessionFromGame(command.getGameID(), session);

        //Notify the other players
        broadcastMessage(command.getGameID(), notification(username + " left the game"), session);

        //For testing, just to show that this method is running
        //sendMessage(notification("you left the game"), session);
    }
    public void resign(Session session, String username, ResignCommand command) throws Exception {
        int gameId = command.getGameID();
        GameData game = gdao.getGame(gameId);

        //If the game is over, you can't resign
        if (game.game().gameOver()) {
            sendMessage(error("You can't resign, the game is over"), session);
            return;
        }

        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();
        ChessGame.TeamColor winner;
        if (username.equals(whiteUsername)) {
            winner = ChessGame.TeamColor.BLACK;
        } else if (username.equals(blackUsername)) {
            winner = ChessGame.TeamColor.WHITE;
        } else {
            sendMessage(error("You can't resign, you're not playing"), session);
            return;
        }
        game.game().declareWinner(winner);
        gdao.updateGame(game);
        sendMessage(notification("You resigned"), session);
        broadcastMessage(gameId, notification(username + " resigned"), session);
        //broadcastMessage(gameId, loadGameMessage(game), session);
    }

    public String notification(String message) {
        return serializer.toJson(new NotificationMessage(message));
    }
    public String error(String message) {
        return serializer.toJson(new ErrorMessage(message));
    }
    public String loadGameMessage(int gameId) {
        try {
            GameData game = gdao.getGame(gameId);
            return loadGameMessage(game);
        } catch (DataAccessException e) {
            return notification("Tried to load a game that doesn't exist");
        }
    }
    public String loadGameMessage(GameData game) {return serializer.toJson(new LoadGameMessage(game));}
    public void sendMessage(String message, Session session) throws Exception {
        //uhhh
        //This code is what the echo function looks like the sendMessage is supposed to be
        //Make sure the session is open, otherwise don't send a message
        if (session.isOpen()) {
            session.getRemote().sendString(message);
        }
    }
    public void broadcastMessage(int gameId, String message, Session exceptThisSession) throws Exception{
        //Send a message to every session at that gameId
        //Exclude whichever session that is
        for (Session session : sessions.getSessionsFromGame(gameId)) {
            //Keep track of which sessions are closed and should be removed
            if (!session.equals(exceptThisSession)) {
                sendMessage(message, session);
            }
        }
        //Add another loop to loop through the sessions and remove the ones that need to be removed
    }
    public void broadcastMessage(int gameId, String message) throws Exception{
        broadcastMessage(gameId, message, null);
    }
}
