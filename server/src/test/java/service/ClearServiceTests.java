package service;

import dataaccess.AuthDAO;
import dataaccess.exceptions.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.reqres.ClearRequest;
import service.reqres.ClearResult;

import static service.GameService.clear;

public class ClearServiceTests {

    UserDAO udao;
    AuthDAO adao;
    GameDAO gdao;

    @BeforeEach
    public void setup() {
        udao = new UserDAO();
        adao = new AuthDAO();
        gdao = new GameDAO();
    }

    @Test
    @DisplayName("Clear")
    public void clearTest() throws DataAccessException {
        udao.createUser("dog", "bark bark bark", "tobyfox@metta.ton");
        adao.createAuth(new AuthData("scoobydoo", "reeheeheeheehee"));
        gdao.createGame("Welcome to the underground");


        ClearRequest clearRequest = new ClearRequest();
        ClearResult clearResult = clear(clearRequest);

        Assertions.assertTrue(udao.isEmpty(), "User data not empty");
        Assertions.assertTrue(adao.isEmpty(), "Auth data not empty");
        Assertions.assertTrue(gdao.isEmpty(), "Game data not empty");
    }
}
