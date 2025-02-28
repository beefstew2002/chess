package service.RequestResult;

import java.util.ArrayList;

public record ListResult(String username, String authToken, ArrayList<String> gameList) {
}
//ArrayList<String> is probably not the right data type for this, but all it needs is a list of game names right?