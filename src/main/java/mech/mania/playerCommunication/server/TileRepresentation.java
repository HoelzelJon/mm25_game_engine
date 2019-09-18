package mech.mania.playerCommunication.server;

import mech.mania.Tile;

public class TileRepresentation {
    private int id;
    private Tile.Type type;
    private int hp;

    public TileRepresentation(Tile aTile) {
        id = aTile.getId();
        type = aTile.getType();
        hp = aTile.getHp();
    }
}
