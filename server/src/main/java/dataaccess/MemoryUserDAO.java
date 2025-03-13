package dataaccess;
import dataaccess.exceptions.DataAccessException;
import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements DAInterface{

    private static final ArrayList<UserData> USER_DATA = new ArrayList<>();

    public ArrayList<UserData> getUserData() {
        return USER_DATA;
    }

    public model.UserData getUser(String username) {
        for (int i = 0; i < USER_DATA.size(); i++) {
            if (USER_DATA.get(i).username().equals(username)) {
                return USER_DATA.get(i);
            }
        }
        return null;
    }
    public void createUser(String username, String password, String email) throws DataAccessException {
        UserData newUser = new UserData(username, password, email);
        USER_DATA.add(newUser);
    }
    public boolean isEmpty() {
        return USER_DATA.isEmpty();
    }

    public void clearData() {
        USER_DATA.clear();
    }

}
