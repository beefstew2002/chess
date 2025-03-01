package service;


import dataaccess.*;
import model.AuthData;
import model.UserData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.RequestResult.*;
import static service.GameService.*;
import static service.UserService.*;

public class GameServiceTests {

    String username;
    String password;
    String email;
    String gameName;
    CreateRequest createRequest;
    GameDAO gdao;
    String authToken;

    @BeforeEach
    public void setup() throws DataAccessException{
        username = "Cram";
        password = "JanetIsADifferentPersonIMeanTheSamePersonIMean";
        email = "thegobdoctor@spelljam.net";
        gameName = "The Goblin Campaign";
        gdao = new GameDAO();

        //Log in the user
        RegisterResult registerResult = register(new RegisterRequest(username, password, email));
        authToken = registerResult.authToken();

        createRequest = new CreateRequest(gameName, authToken);
    }

    @Test
    @DisplayName("Create game success")
    public void createGameSuccess() {
        //Attempt to create the game
        CreateResult createResult = create(createRequest);

        //Check to see if a game was created
        Assertions.assertFalse(gdao.isEmpty());
    }

}
