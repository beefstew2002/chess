package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.UsernameAlreadyTaken;
import service.RequestResult.RegisterRequest;
import service.RequestResult.RegisterResult;
import service.UserService;
import service.RequestResult.FailureResult;

import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route{

    public Object handle(Request req, Response res) {

        var serializer = new Gson();
        RegisterRequest registerRequest;

        try {
            registerRequest = serializer.fromJson(req.body(), RegisterRequest.class);
        } catch (JsonSyntaxException e) {
            res.status(400);
            res.body(serializer.toJson(new FailureResult("Error: bad request")));
            return res.body();
        }

        try {
            res.status(200);
            RegisterResult regResult = UserService.register(registerRequest);
            res.body(serializer.toJson(regResult));
        }
        catch (UsernameAlreadyTaken e) {
            res.status(403);
            res.body(serializer.toJson(new FailureResult("Error: already taken")));
        }
        catch (DataAccessException e) {
            res.status(400);
            res.body(serializer.toJson(new FailureResult("Error: bad request")));
        }
        catch (Exception e) {
            res.status(500);
            res.body(serializer.toJson(new FailureResult(e.toString())));
        }

        return res.body();
    }

}
