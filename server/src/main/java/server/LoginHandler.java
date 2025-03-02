package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.WrongPasswordException;
import service.reqres.FailureResult;
import service.reqres.LoginRequest;
import service.reqres.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    public Object handle(Request req, Response res) {
        var serializer = new Gson();

        LoginRequest loginRequest;

        try {
            loginRequest = serializer.fromJson(req.body(), LoginRequest.class);
        }
        catch (JsonSyntaxException e) {
            res.status(400);
            res.body(serializer.toJson(new FailureResult("Error: bad request")));
            return res.body();
        }

        try {
            LoginResult loginResult = UserService.login(loginRequest);
            res.status(200);
            res.body(serializer.toJson(loginResult));
        }
        catch (WrongPasswordException e) {
            res.status(401);
            res.body(serializer.toJson(new FailureResult("Error: unauthorized")));
        }
        catch (DataAccessException e) {
            res.status(401);
            res.body(serializer.toJson(new FailureResult("Error: username doesn't exist")));
        }
        catch (Exception e) {
            res.status(500);
            res.body(serializer.toJson(new FailureResult(e.toString())));
        }

        return res.body();
    }
}
