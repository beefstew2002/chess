package server.reqres;

public record LogoutRequest(String authToken) implements Authorized {

}
