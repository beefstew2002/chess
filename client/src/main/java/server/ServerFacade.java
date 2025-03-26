package server;

import com.google.gson.Gson;
import model.AuthData;
import model.GameMetaData;
import exception.ResponseException;
import server.reqres.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = "http://localhost:"+ url;
    }
    public ServerFacade(int url) {
        serverUrl = "http://localhost:"+ Integer.toString(url);
    }

    //Methods for API
    //register
    public AuthData register(String username, String password, String email) throws ResponseException{
        var path = "/user";
        return this.makeRequest("POST", path, new RegisterRequest(username, password, email), AuthData.class);
    }

    //login
    public AuthData login(String username, String password) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, new LoginRequest(username, password), null);
    }

    //logout
    public void logout() throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null);
    }

    //create
    public int create(String name) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, name, Integer.class);
    }

    //list
    public GameMetaData[] list() throws ResponseException {
        var path = "/game";
        record listResponse(GameMetaData[] games) {}
        var response = this.makeRequest("GET", path, null, listResponse.class);
        return response.games();
    }

    //join
    public void join(int id) throws ResponseException {
        var path = "/game";
        this.makeRequest("PUT", path, id, null);
    }

    //clear
    public void clearData() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {

        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    try {
                        throw ResponseException.fromJson(respErr);
                    } catch (Exception e) {
                        throw new ResponseException(status, "other failure: " + status);
                    }
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}