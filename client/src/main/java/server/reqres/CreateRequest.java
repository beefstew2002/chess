package server.reqres;

public record CreateRequest(String gameName, String authToken) implements Authorized {
}
