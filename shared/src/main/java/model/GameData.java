package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameMetaData getMetaData() {
        return new GameMetaData(gameID, whiteUsername, blackUsername, gameName);
    }
}
