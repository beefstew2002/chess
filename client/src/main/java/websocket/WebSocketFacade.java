package websocket;

import com.google.gson.Gson;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class WebSocketFacade extends Endpoint {
    public Session session;
    public ServerMessageObserver observer;

    public WebSocketFacade(ServerMessageObserver observer) throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.observer = observer;

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                //THIS is where the magic happens
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                switch (serverMessage.getServerMessageType()) {
                    case NOTIFICATION -> {
                        NotificationMessage nm = new Gson().fromJson(message, NotificationMessage.class);
                        observer.notify(nm);
                    }
                    case ERROR -> {
                        ErrorMessage em = new Gson().fromJson(message, ErrorMessage.class);
                        observer.error(em);
                    }
                    case LOAD_GAME -> {
                        LoadGameMessage lgm = new Gson().fromJson(message, LoadGameMessage.class);
                        observer.loadGame(lgm);
                    }
                }

            }
        });
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
