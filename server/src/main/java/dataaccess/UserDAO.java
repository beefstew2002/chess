package dataaccess;
import model.UserData;

public class UserDAO implements DAInterface{
    //Methods this will require:
    //createUser
    //getUser

    public model.UserData getUser(String username) {
        return getUserData(username);
    }
    public void createUser(String username, String password, String email) throws DataAccessException {
        UserData newUser = new UserData(username, password, email);
        storeUserData(newUser);
    }
    public boolean isEmpty() {
        return isUserDataEmpty();
    }
}
