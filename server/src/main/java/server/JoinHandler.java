package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import service.RequestResult.CreateRequest;
import service.RequestResult.FailureResult;
import service.RequestResult.JoinRequest;
import service.RequestResult.JoinResult;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Properties;

import static service.GameService.join;

public class JoinHandler implements Route {
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        JoinRequest joinRequest = null;

        try {
            //It got a little tricky here to extract the data I needed, since the authToken is
            //in the header and the gameName is in the body. I feel like there was supposed
            //to be a better way to do this. Does the createRequest even need the authToken?
            //Couldn't the handler be the one to check the authorization?
            Properties data = serializer.fromJson(req.body(), Properties.class);
            String gameName = data.getProperty("gameName");
            String playerColor = data.getProperty("playerColor");
            int gameID = Integer.parseInt(data.getProperty("gameID"));
            joinRequest = new JoinRequest(gameID, playerColor, req.headers("authorization"));
        }
        catch (JsonSyntaxException e) {
            res.status(400);
            res.body(serializer.toJson(new FailureResult("Error: bad request")));
            return res.body();
        }

        try {
            JoinResult joinResult = join(joinRequest);
            res.status(200);
            res.body(serializer.toJson(joinResult));
            return res.body();
        }
        catch (UnauthorizedException e) {
            res.status(401);
            res.body(serializer.toJson(new FailureResult("Error: unauthorized")));
        }
        catch (BadRequestException e) {
            res.status(400);
            res.body(serializer.toJson(new FailureResult("Error: bad request (wrong color)")));
        }
        catch (DataAccessException e) {
            res.status(403);
            res.body(serializer.toJson(new FailureResult("Error: already taken")));
        }
        catch (Exception e) {
            res.status(500);
            res.body(serializer.toJson(new FailureResult("Error: " + e.toString())));
        }

        return res.body();
    }
}
