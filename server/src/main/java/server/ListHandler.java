package server;

import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import service.reqres.FailureResult;
import service.reqres.ListRequest;
import service.reqres.ListResult;
import spark.Request;
import spark.Response;
import spark.Route;

import static service.GameService.list;

public class ListHandler implements Route {
    public Object handle(Request req, Response res) {
        var serializer = new Gson();

        ListRequest listRequest = new ListRequest(req.headers("authorization"));

        try {
            ListResult listResult = list(listRequest);
            res.body(serializer.toJson(listResult));
        } catch (DataAccessException e) {
            res.status(401);
            res.body(serializer.toJson(new FailureResult("Error: unauthorized")));
            return res.body();
        } catch (Exception e) {
            res.status(500);
            res.body(serializer.toJson(new FailureResult("Error: " + e)));
        }

        return res.body();
    }
}
