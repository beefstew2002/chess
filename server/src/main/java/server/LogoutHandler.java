package server;

import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import service.reqres.FailureResult;
import service.reqres.LogoutRequest;
import service.reqres.LogoutResult;
import spark.Request;
import spark.Response;
import spark.Route;

import static service.UserService.logout;

public class LogoutHandler implements Route {
    public Object handle(Request req, Response res) {
        var serializer = new Gson();

        LogoutRequest logoutRequest = new LogoutRequest(req.headers("authorization"));

        try {
            LogoutResult logoutResult = logout(logoutRequest);
            res.status(200);
            res.body(serializer.toJson(logoutResult));
        } catch (DataAccessException e) {
            res.status(401);
            res.body(serializer.toJson(new FailureResult("Error: Logout failure. Exception: "+e)));
            return res.body();
        } catch (Exception e) {
            res.status(500);
            res.body(serializer.toJson(new FailureResult("Error: Logout failure. Exception: "+e)));
            return res.body();
        }

        return res.body();

        /* */
    }
}