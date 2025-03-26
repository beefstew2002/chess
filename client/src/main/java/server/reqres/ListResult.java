package server.reqres;

import model.GameMetaData;

import java.util.ArrayList;

public record ListResult(ArrayList<GameMetaData> games) {
}
//ArrayList<String> is probably not the right data type for this, but all it needs is a list of game names right?