package service;

//Imports
import static service.UserService.login;
import static service.UserService.register;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.RequestResult.LoginRequest;
import service.RequestResult.LoginResult;
import service.RequestResult.RegisterRequest;

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
    UserData userData;

    @BeforeEach
    public void setup() {
        username = "WorldFamousSpaghettore";
        password = "NyehHehHeh";
        email = "gr8papyrus@metta.ton";
        registerRequest = new RegisterRequest(username, password, email);
        userData = new UserData(username, password, email);
        udao = new UserDAO();
        adao = new AuthDAO();
    }

    //Register
    @Test
    @DisplayName("Register success")
    public void registerUser() throws DataAccessException {

        register(registerRequest);

        UserData result = udao.getUser(username);

        Assertions.assertEquals(userData, result, "UserData not stored or accessed properly");

    }

    @Test
    @DisplayName("Name already taken")
    public void nameAlreadyTaken() throws DataAccessException {
        register(registerRequest);
        registerRequest = new RegisterRequest(username, "myBrotherIsTheCoolest", "sans@metta.ton");

        Assertions.assertThrows(UsernameAlreadyTaken.class, () -> {register(registerRequest);});
    }

    //Login
    @Test
    @DisplayName("Login success")
    public void succeedLogin() throws DataAccessException {
        register(registerRequest);

        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResult result = login(loginRequest);

        AuthData ad = new AuthData(result.username(), result.authToken());

        Assertions.assertTrue(adao.verifyAuth(ad));
    }

    @Test
    @DisplayName("Login failure (wrong password)")
    public void failLogin() throws DataAccessException {
        register(registerRequest);

        Assertions.assertThrows(WrongPasswordException.class, () -> {
            LoginRequest loginRequest = new LoginRequest(username, "wrong password");
            LoginResult result = login(loginRequest);
        });
    }

    //Logout
}
