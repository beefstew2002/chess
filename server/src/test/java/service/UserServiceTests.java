package service;

//Imports

import dataaccess.*;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.UsernameAlreadyTaken;
import dataaccess.exceptions.WrongPasswordException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.reqres.*;

import static service.UserService.*;

public class UserServiceTests {

    /*
    Write a positive and a negative JUNIT test case for each public method on your Service classes,
    except for Clear which only needs a positive test case. A positive test case is on for which
    the action happens successfully (successfully claiming a spot in a game). A negative test case
    is one for which the operation fails (trying to claim an already claimed spot).
     */

    String username;
    String password;
    String email;
    RegisterRequest registerRequest;
    UserDAO udao;
    AuthDAO adao;
    GameDAO gdao;
    UserData userData;

    @BeforeEach
    public void setup() {
        username = "WorldFamousSpaghettore";
        password = "NyehHehHeh";
        email = "gr8papyrus@metta.ton";
        registerRequest = new RegisterRequest(username, password, email);
        userData = new UserData(username, BCrypt.hashpw(password, BCrypt.gensalt()), email);
        udao = new UserDAO();
        adao = new AuthDAO();
        gdao = new GameDAO();

        udao.clearData();
        adao.clearData();
        gdao.clearData();
    }

    //Register
    @Test
    @DisplayName("Register success")
    public void registerUser() throws DataAccessException {

        register(registerRequest);

        UserData result = udao.getUser(username);

        Assertions.assertTrue(userData.equals(result), "UserData not stored or accessed properly");

    }

    @Test
    @DisplayName("Name already taken")
    public void nameAlreadyTaken() throws DataAccessException {
        register(registerRequest);
        registerRequest = new RegisterRequest(username, "myBrotherIsTheCoolest", "sans@metta.ton");

        Assertions.assertThrows(UsernameAlreadyTaken.class, () -> register(registerRequest));
    }

    //Login
    @Test
    @DisplayName("Login success")
    public void succeedLogin() throws DataAccessException {
        register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResult result = login(loginRequest);

        Assertions.assertTrue(adao.verifyAuth(result.authToken()));
    }

    @Test
    @DisplayName("Login failure (wrong password)")
    public void failLogin() throws DataAccessException {
        register(registerRequest);

        Assertions.assertThrows(WrongPasswordException.class, () -> {
            LoginRequest loginRequest = new LoginRequest(username, "wrong password");
            login(loginRequest);
        });
    }

    //Logout

    //Successful logout
    @Test
    @DisplayName("Logout success")
    public void succeedLogout() throws DataAccessException {
        register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResult result = login(loginRequest);

        LogoutRequest logoutRequest = new LogoutRequest(result.authToken());
        logout(logoutRequest);

        Assertions.assertFalse(adao.verifyAuth(result.authToken()));
    }

    //Failed logout: user not logged in
    @Test
    @DisplayName("Logout failure")
    public void failLogout() throws DataAccessException {
        register(registerRequest);

        LogoutRequest logoutRequest = new LogoutRequest("a random string pretending to be an auth token");

        Assertions.assertThrows(DataAccessException.class, () -> {
            logout(logoutRequest);
        });
    }
}
