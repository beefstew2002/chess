package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DAInterface {

    ArrayList<UserData> USER_DATA = new ArrayList<>();
    ArrayList<GameData> GAME_DATA = new ArrayList<>();
    ArrayList<AuthData> AUTH_DATA = new ArrayList<>();

    default ArrayList<UserData> getUserData() {
        return USER_DATA;
    }

    default ArrayList<GameData> getGameData() {
        return GAME_DATA;
    }

    default ArrayList<AuthData> getAuthData() {
        return AUTH_DATA;
    }

    default void storeUserData(UserData ud) {
        USER_DATA.add(ud);
    }

    default UserData getUserData(String username) {
        for (int i = 0; i < USER_DATA.size(); i++) {
            if (USER_DATA.get(i).username().equals(username)) {
                return USER_DATA.get(i);
            }
        }
        return null;
    }

    default AuthData getAuthData(String authToken) throws DataAccessException {
        for (AuthData ad : AUTH_DATA) {
            if (ad.authToken().equals(authToken)) {
                return ad;
            }
        }
        throw new UnauthorizedException("Auth doesn't exist");
    }

    default GameData getGameData(int gameID) throws DataAccessException {
        for (GameData gd : GAME_DATA) {
            if (gd.gameID() == gameID) {
                return gd;
            }
        }
        throw new DataAccessException("Game doesn't exist");
    }

    default void storeAuthData(AuthData ad) {
        AUTH_DATA.add(ad);
    }

    default void deleteAuthData(String authToken) throws DataAccessException {
        int i = AUTH_DATA.size() - 1;
        while (i >= 0) {
            if (AUTH_DATA.get(i).authToken().equals(authToken)) {
                AUTH_DATA.remove(i);
                return;
            }
            i--;
        }
        throw new DataAccessException("Could not find auth data to delete");
    }

    default boolean checkAuthData(String authToken) {
        int i = 0;
        while (i < AUTH_DATA.size()) {
            if (AUTH_DATA.get(i).authToken().equals(authToken)) {
                return true;
            }
            i++;
        }
        return false;
    }

    default void storeGameData(GameData gd) {
        GAME_DATA.add(gd);
    }

    default void updateGameData(GameData gd) {
        boolean searching = true;
        int i = 0;
        while (searching) {
            if (GAME_DATA.get(i).gameName().equals(gd.gameName())) {
                GAME_DATA.set(i, gd);
                searching = false;
            }
            i++;
        }
    }

    default boolean hasGameData(String gameName) {
        for (int i = 0; i < GAME_DATA.size(); i++) {
            if (GAME_DATA.get(i).gameName().equals(gameName)) {
                return true;
            }
            i++;
        }
        return false;
    }

    default int getGameId() {
        return GAME_DATA.size() + 1;
    }

    default void clearData() {
        USER_DATA.clear();
        GAME_DATA.clear();
        AUTH_DATA.clear();
    }

    default boolean isUserDataEmpty() {
        return USER_DATA.isEmpty();
    }

    default boolean isAuthDataEmpty() {
        return AUTH_DATA.isEmpty();
    }

    default boolean isGameDataEmpty() {
        return GAME_DATA.isEmpty();
    }
}
