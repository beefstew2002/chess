package websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import dataaccess.SQLUserDAO;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.*;
import websocket.messages.ErrorMessage;

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
        broadcastMessage(command.getGameID(), username + " joined the game", session);
        //For testing, for now it will also send a message back to itself
        //sendMessage("echo, echo, echo", session);
        //well, that worked! but the client was expecting json. That must be what the notification class is for.
    }
    public void makeMove(Session session, String username, MakeMoveCommand command) throws Exception {}
    public void leaveGame(Session session, String username, LeaveCommand command) throws Exception {}
    public void resign(Session session, String username, ResignCommand command) throws Exception {}

    public void sendMessage(String message, Session session) throws Exception {
        //uhhh
        //This code is what the echo function looks like the sendMessage is supposed to be
        session.getRemote().sendString(message);
    }
    public void broadcastMessage(int gameId, String message, Session exceptThisSession) throws Exception{
        //Send a message to every session at that gameId
        //Exclude whichever session that is
        for (Session session : sessions.getSessionsFromGame(gameId)) {
            if (!session.equals(exceptThisSession)) {
                sendMessage(message, session);
            }
        }
    }
}
