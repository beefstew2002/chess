package service.RequestResult;

import model.GameData;
import model.GameMetaData;

import java.util.ArrayList;

public record ListResult(ArrayList<GameMetaData> gameList) {
}
//ArrayList<String> is probably not the right data type for this, but all it needs is a list of game names right?