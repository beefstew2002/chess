package server.reqres;

public record JoinRequest(int gameID, String playerColor, String authToken) {
}
