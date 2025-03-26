package server.reqres;

public record ListRequest(String authToken) implements Authorized {
}
