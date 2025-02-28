package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.RequestResult.FailureResult;
import service.RequestResult.LogoutRequest;
import service.RequestResult.LogoutResult;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Set;

import static service.UserService.logout;

public class LogoutHandler implements Route {
    public Object handle(Request req, Response res) {
        var serializer = new Gson();

        //Maybe I should write a function to extract the authToken from the headers
        LogoutRequest logoutRequest = new LogoutRequest(req.headers("authorization"));

        try {
            LogoutResult logoutResult = logout(logoutRequest);
            res.status(200);
            res.body("");
        } catch (DataAccessException e) {
            //Maybe I'll build a bunch of custom exceptions for this but I don't really feel like it rn, this should work for now
            res.status(401);
            res.body(serializer.toJson(new FailureResult("Logout failure. Exception: "+e.toString())));
            return res.body();
        } catch (Exception e) {
            res.status(500);
            res.body(serializer.toJson(new FailureResult("Logout failure. Exception: "+e.toString())));
            return res.body();
        }

        return res.body();

        /* */
    }
}
