package dataaccess;

import model.AuthData;

import java.util.ArrayList;

public class MemoryAuthDAO implements DAInterface {

    static ArrayList<AuthData> AUTH_DATA = new ArrayList<>();

    public ArrayList<AuthData> getAuthData() {
        return AUTH_DATA;
    }

    public void createAuth(AuthData authData) throws DataAccessException {
        AUTH_DATA.add(authData);
    }

    public boolean verifyAuth(String authToken) {
        int i = 0;
        while (i < AUTH_DATA.size()) {
            if (AUTH_DATA.get(i).authToken().equals(authToken)) {
                return true;
            }
            i++;
        }
        return false;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData ad : AUTH_DATA) {
            if (ad.authToken().equals(authToken)) {
                return ad;
            }
        }
        throw new UnauthorizedException("Auth doesn't exist");
    }

    public void deleteAuth(String authToken) throws DataAccessException {
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

    public boolean isEmpty() {
        return AUTH_DATA.isEmpty();
    }

    public void clearData() {
        AUTH_DATA.clear();
    }
}
