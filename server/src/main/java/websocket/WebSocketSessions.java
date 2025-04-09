package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WebSocketSessions {
    private Map<Integer, Set<Session>> sessionMap;

    public WebSocketSessions() {
        sessionMap = new HashMap<Integer, Set<Session>>();
    }

    public void addSessionToGame(int gameId, Session session) {

        sessionMap.get(gameId).add(session);
    }

    public void removeSessionFromGame(int gameId, Session session) {
        sessionMap.get(gameId).remove(session);
    }

    public Set<Session> getSessionsFromGame(int gameId) {
        return sessionMap.get(gameId);
    }
}

