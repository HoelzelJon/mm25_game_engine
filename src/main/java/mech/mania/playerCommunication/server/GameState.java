package mech.mania.playerCommunication.server;

import mech.mania.Board;
import mech.mania.Game;
import mech.mania.Position;
import mech.mania.Unit;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private int playerNum;
    private String gameId;
    private int turnsTaken;
    private String[] playerNames;
    private List<Unit> units;
    private TileRepresentation[][] tiles;

    public GameState(Game aGame, int aPlayerNum) {
        gameId = aGame.getGameId();
        turnsTaken = aGame.getTurnNumber();
        playerNames = new String[] {aGame.getPlayerName(1), aGame.getPlayerName(2)};
        units = new ArrayList<>();
        units.addAll(aGame.getPlayerUnits(1));
        units.addAll(aGame.getPlayerUnits(2));
        playerNum = aPlayerNum;

        Board gameBoard = aGame.getBoard();
        tiles = new TileRepresentation[gameBoard.width()][];
        for (int x = 0; x < tiles.length; x ++) {
            tiles[x] = new TileRepresentation[gameBoard.height()];

            for (int y = 0; y < tiles[x].length; y ++) {
                tiles[x][y] = new TileRepresentation(gameBoard.tileAt(new Position(x, y)));
            }
        }
    }
}
