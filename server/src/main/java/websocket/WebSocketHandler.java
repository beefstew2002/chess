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

    private WebSocketSessions sessions;

    public WebSocketHandler() {
        sessions = new WebSocketSessions();
        udao = new SQLUserDAO();
        adao = new SQLAuthDAO();
        gdao = new SQLGameDAO();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            String username = command.getAuthToken(); //get the username from this, not just the auth token

            //Save the session in the WebSocketSessions: saveSession(command.getGameID(), session)
            sessions.addSessionToGame(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, username, (LeaveCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //It doesn't like the sendMessage line. Where is that supposed to be defined? Do I write it?
            //sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    public void connect(Session session, String username, ConnectCommand command) {
        System.out.println("the connect websocket endpoint got called!");
    }
    public void makeMove(Session session, String username, MakeMoveCommand command) {}
    public void leaveGame(Session session, String username, LeaveCommand command) {}
    public void resign(Session session, String username, ResignCommand command) {}
}
