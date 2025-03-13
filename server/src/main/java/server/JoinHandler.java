package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UnauthorizedException;
import service.reqres.FailureResult;
import service.reqres.JoinRequest;
import service.reqres.JoinResult;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Properties;

import static service.GameService.join;

public class JoinHandler implements Route {
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        JoinRequest joinRequest;

        try {
            Properties data = serializer.fromJson(req.body(), Properties.class);
            String playerColor = data.getProperty("playerColor");
            int gameID = Integer.parseInt(data.getProperty("gameID"));
            joinRequest = new JoinRequest(gameID, playerColor, req.headers("authorization"));
        }
        catch (JsonSyntaxException | NumberFormatException e) {
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
            res.body(serializer.toJson(new FailureResult("Error: " + e)));
        }

        return res.body();
    }
}
