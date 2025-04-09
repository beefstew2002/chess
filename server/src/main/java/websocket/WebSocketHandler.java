package websocket;

import chess.ChessMove;
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

            String username = command.getAuthToken(); //get the username from this, not just the auth token

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
        //System.out.println("the connect websocket endpoint got called!");
        broadcastMessage(command.getGameID(), notification(username + " joined the game"), session);
        //For testing, for now it will also send a message back to itself
        //sendMessage(notification("connected to game"), session);
        //that worked!

        //Loading a game
        sendMessage(loadGameMessage(command.getGameID()), session);
    }
    public void makeMove(Session session, String username, MakeMoveCommand command) throws Exception {
        int gameId = command.getGameID();
        ChessMove move = command.getMove();
        GameData game = gdao.getGame(gameId);
        if (game.game().isMoveValid(move)) {
            game.game().makeMove(move);
            gdao.updateGame(game);
            broadcastMessage(gameId, notification(username + " made move " + move.toString()), session);
            broadcastMessage(gameId, loadGameMessage(game), null);
        } else {
            sendMessage(notification("That move is illegal"), session);
        }
    }
    public void leaveGame(Session session, String username, LeaveCommand command) throws Exception {
        sessions.removeSessionFromGame(command.getGameID(), session);
        broadcastMessage(command.getGameID(), username + " left the game", session);
        //For testing, just to show that this method is running
        sendMessage(notification("you left the game"), session);
    }
    public void resign(Session session, String username, ResignCommand command) throws Exception {

    }

    public String notification(String message) {
        return serializer.toJson(new NotificationMessage(message));
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
}
