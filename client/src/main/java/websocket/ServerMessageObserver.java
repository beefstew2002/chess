package websocket;


import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void notify(NotificationMessage message);
    void error(ErrorMessage message);
    void loadGame(LoadGameMessage message);
}
