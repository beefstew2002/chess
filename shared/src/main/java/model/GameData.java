package model;

import chess.ChessGame;
import java.util.Objects;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameMetaData getMetaData() {
        return new GameMetaData(gameID, whiteUsername, blackUsername, gameName);
    }

    @Override
    public boolean equals(Object other) {
        GameData otherGame = (GameData) other;
        return gameID == otherGame.gameID() &&
                Objects.equals(whiteUsername, otherGame.whiteUsername()) &&
                Objects.equals(blackUsername,otherGame.blackUsername()) &&
                Objects.equals(gameName,otherGame.gameName()) &&
                game.equals(otherGame.game());
    }
}
