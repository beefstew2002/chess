package server;

import com.google.gson.Gson;
import service.RequestResult.ClearRequest;
import service.RequestResult.ClearResult;
import service.RequestResult.FailureResult;
import spark.Request;
import spark.Response;
import spark.Route;

import static service.GameService.clear;

public class ClearHandler implements Route {
    public Object handle(Request req, Response res) {
        var serializer = new Gson();

        try {
            ClearRequest clearRequest = new ClearRequest();
            ClearResult clearResult = clear(clearRequest);
            res.status(200);
            res.body(serializer.toJson(clearResult));
        }

        catch (Exception e) {
            res.status(500);
            res.body(serializer.toJson(new FailureResult("Error: "+e)));
        }

        return res.body();
    }
}
