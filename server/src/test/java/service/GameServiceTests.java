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

    @Test
    @DisplayName("Trying to create another game with the same name")
    public void createGameFailure() throws DataAccessException{
        //Create the first game
        CreateResult createResult1 = create(createRequest);

        //For testing integrity, a different user with a different token will create the second game
        RegisterResult registerResult2 = register(new RegisterRequest("Janet", "theGoblather", "dndj@spelljam.net"));
        String authToken2 = registerResult2.authToken();

        //Attempting to create a second game with the same name should fail
        Assertions.assertThrows(DataAccessException.class, () -> {
            create(new CreateRequest(gameName, authToken2));
        });
    }

}
