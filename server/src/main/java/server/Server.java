package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

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

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
