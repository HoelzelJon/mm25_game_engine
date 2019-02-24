package mech.mania;

/*
 * Stores the 2-D array of tiles that makes up the game board.
 * May also be responsible for generating random boards, if that is what we end up doing.
 */
public class Map {
    private Tile[][] tiles; // 2-D array of all tiles on the board

    public Map(int size) {
        tiles = new Tile[size][size];

        for (int x = 0; x < size; x ++) {
            for (int y = 0; y < size; y ++) {
                tiles[x][y] = new Tile(new Position(x, y));
            }
        }
    }

    public Tile tileAt(Position pos) {
        return tiles[pos.x][pos.y];
    }

    public int width() {
        return tiles.length;
    }

    public int height() {
        return tiles[0].length;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();

        for (int y = 0; y < tiles[0].length; y ++) {
            for (int x = 0; x < tiles.length; x++) {
                ret.append(tiles[x][y].shortString());
            }
            ret.append("\n");
        }

        return ret.toString();
    }
}
