package mech.mania;

import java.util.List;

/**
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

    /**
     * Concurrently moves units from one location to another
     *  (concurrency is required because if we do them one at a time, then some
     *   units will overwrite others on some tiles)
     */
    public void moveUnits(List<Unit> units, List<Position> destinations) {
        // remove units from the tiles they're on right now
        for (Unit u : units) {
            tileAt(u.getPos()).setUnit(null);
        }

        // add units to the new tiles and set their new positions
        for (int i = 0; i < units.size(); i ++) {
            units.get(i).setPos(destinations.get(i));
            tileAt(destinations.get(i)).setUnit(units.get(i));
        }
    }

    /**
     * Deals attack damage as indicated by attack to tiles, centered at center
     *
     * @param attack 2-D int array of attack damages (should be odd width and height to correctly handle center)
     * @param center Position of the center of the attack
     */
    public void doAttackDamage(int[][] attack, Position center) {
        int attackWidth = attack.length;
        int attackHeight = attack[0].length;

        int x0 = center.x - (attackWidth / 2); // x-coordinate of where attack[0][0] will go
        int y0 = center.y - (attackHeight / 2); // y-coordinate of where attack[0][0] will go

        for (int x = 0; x < attackWidth; x ++) {
            if (x0 + x >= 0 && x0 + x < width()){
                for (int y = 0; y < attackHeight; y++) {
                    if (y0 + y >= 0 && y0 + y < height()) {
                        tiles[x0 + x][y0 + y].takeDamage(attack[x][y]);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();

        for (int y = tiles[0].length - 1; y >= 0; y --) {
            for (int x = 0; x < tiles.length; x++) {
                ret.append(tiles[x][y].shortString());
            }
            ret.append("\n");
        }

        return ret.toString();
    }
}
