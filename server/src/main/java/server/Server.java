package server;

import spark.*;
import websocket.WebSocketHandler;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        //Websocket
        Spark.webSocket("/ws", WebSocketHandler.class);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //Register
        Spark.post("/user", new RegisterHandler());
        //Login
        Spark.post("/session", new LoginHandler());
        //Logout
        Spark.delete("/session", new LogoutHandler());
        //Create game
        Spark.post("/game", new CreateHandler());
        //List games
        Spark.get("/game", new ListHandler());
        //Clear games
        Spark.delete("/db", new ClearHandler());
        //Join game
        Spark.put("/game", new JoinHandler());

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
