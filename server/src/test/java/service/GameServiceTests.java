package service;


import dataaccess.*;
import dataaccess.exceptions.DataAccessException;
import model.GameMetaData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.reqres.*;

import java.util.ArrayList;

import static service.GameService.*;
import static service.UserService.*;

public class GameServiceTests {

    String username;
    String password;
    String email;
    String gameName;
    CreateRequest createRequest;
    UserDAO udao;
    GameDAO gdao;
    AuthDAO adao;
    String authToken;

    @BeforeEach
    public void setup() throws DataAccessException {
        username = "Cram";
        password = "JanetIsADifferentPersonIMeanTheSamePersonIMean";
        email = "thegobdoctor@spelljam.net";
        gameName = "The Goblin Campaign";
        udao = new UserDAO();
        gdao = new GameDAO();
        adao = new AuthDAO();

        udao.clearData();
        gdao.clearData();
        adao.clearData();

        //Log in the user
        RegisterResult registerResult = register(new RegisterRequest(username, password, email));
        authToken = registerResult.authToken();

        createRequest = new CreateRequest(gameName, authToken);
    }

    @Test
    @DisplayName("Create game success")
    public void createGameSuccess() throws DataAccessException{
        //Attempt to create the game
        create(createRequest);

        //Check to see if a game was created
        Assertions.assertFalse(gdao.isEmpty());
    }

    @Test
    @DisplayName("Duplicate name")
    public void createGameFailure() throws DataAccessException{
        //Create the first game
        create(createRequest);

        //For testing integrity, a different user with a different token will create the second game
        RegisterResult registerResult2 = register(new RegisterRequest("Janet", "theGoblather", "dndj@spelljam.net"));
        String authToken2 = registerResult2.authToken();

        //Attempting to create a second game with the same name should fail
        Assertions.assertThrows(DataAccessException.class, () ->
            create(new CreateRequest(gameName, authToken2))
        );
    }

    //List games success
    @Test
    @DisplayName("List game succeeds")
    public void listGamesSuccess() throws DataAccessException {
        //Create three games
        create(createRequest);
        create(new CreateRequest("Raan's Cult vs the Chosen of Ilvash", authToken));
        create(new CreateRequest("The Great Papyrus and sans.", authToken));

        //Get the list of games straight from GameDAO
        ArrayList<GameMetaData> gamesList = gdao.listGames();

        //Get the list of games from the service
        ListRequest listRequest = new ListRequest(authToken);
        ListResult listResult = list(listRequest);

        //Check to make sure they're the same
        Assertions.assertEquals(gamesList, listResult.games());

    }

    //List games failure
    @Test
    @DisplayName("List fails: bad auth")
    public void listGamesBadAuth() throws DataAccessException {
        //Create a game
        create(createRequest);

        //Attempt to run list with a bad auth token
        ListRequest listRequest = new ListRequest("this is not a good auth token");
        Assertions.assertThrows(DataAccessException.class, () -> {
            list(listRequest);
        });
    }

    //Join game success
    @Test
    @DisplayName("Join game succeeds")
    public void joinGameSucceeds() throws DataAccessException {
        //Create a game
        CreateResult createResult = create(createRequest);

        //Attempt to join a user to the list
        JoinRequest joinRequest = new JoinRequest(createResult.gameID(), "WHITE", authToken);
        join(joinRequest);

        //Check if the user is in the game now
        GameData game = gdao.getGame(createResult.gameID());

        Assertions.assertEquals(username, game.whiteUsername());
    }

    //Join game failure
    @Test
    @DisplayName("Game doesn't exist")
    public void gameDoesntExist() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            JoinRequest joinRequest = new JoinRequest(1, "WHITE", authToken);
            join(joinRequest);
        });
    }
}
