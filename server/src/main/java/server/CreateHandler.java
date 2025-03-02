package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import service.RequestResult.LogoutRequest;
import spark.Request;
import spark.Response;
import service.RequestResult.FailureResult;
import service.RequestResult.CreateRequest;
import service.RequestResult.CreateResult;
import spark.Route;

import java.util.Properties;

import static service.GameService.create;

public class CreateHandler implements Route {
    public Object handle(Request req, Response res) {
        var serializer = new Gson();

        CreateRequest createRequest = null;
        String authToken = req.headers("authorization");


        try {
            //It got a little tricky here to extract the data I needed, since the authToken is
            //in the header and the gameName is in the body. I feel like there was supposed
            //to be a better way to do this. Does the createRequest even need the authToken?
            //Couldn't the handler be the one to check the authorization?
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
            res.body(serializer.toJson(new FailureResult("Error: exception" + e.toString())));
        }

        return res.body();
    }
}
