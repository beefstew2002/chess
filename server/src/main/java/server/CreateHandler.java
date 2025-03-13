package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedException;
import spark.Request;
import spark.Response;
import service.reqres.FailureResult;
import service.reqres.CreateRequest;
import service.reqres.CreateResult;
import spark.Route;

import java.util.Properties;

import static service.GameService.create;

public class CreateHandler implements Route {
    public Object handle(Request req, Response res) {
        var serializer = new Gson();

        CreateRequest createRequest;


        try {
            Properties data = serializer.fromJson(req.body(), Properties.class);
            String gameName = data.getProperty("gameName");
            createRequest = new CreateRequest(gameName, req.headers("authorization"));
        }
        catch (JsonSyntaxException e) {
            res.status(400);
            res.body(serializer.toJson(new FailureResult("Error: bad request")));
            return res.body();
        }

        try {
            CreateResult createResult = create(createRequest);
            res.status(200);
            res.body(serializer.toJson(createResult));
        }
        catch (UnauthorizedException e) {
            res.status(401);
            res.body(serializer.toJson(new FailureResult("Error: You're not authorized")));
        }
        catch (DataAccessException e) {
            res.status(500);
            res.body(serializer.toJson(new FailureResult("Error: exception" + e)));
        }

        return res.body();
    }
}
