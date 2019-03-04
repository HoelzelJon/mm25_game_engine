package mech.mania;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores the 2-D array of tiles that makes up the game board.
 * May also be responsible for generating random boards, if that is what we end up doing.
 */
public class Map {
    private static final String DIRECTORY = "./Maps/";

    private Tile[][] tiles; // 2-D array of all tiles on the board
    private Position[][] init_positions; // init_positions[0] = array of player 1's initial positions
                                        // init_positions[1] = array of player 2's initial positions

    /**
     * Default map constructor: generates map based on a random file from DIRECTORY, in the following format:
     * - formatted as a .csv file (columns separated by commas, rows separated by newlines)
     * - Indestructtile tiles are marked with 'I'
     * - Destructible tiles marked with an integer for their health
     * - Tiles which are initial spawns for units should be 'U##', where:
     *      - the first # is replaced by either 1 or 2, based on which player owns that unit
     *      - the second # is replaced by 0, 1, or 2, based on which unit it is
     */
    public Map() {
        File folder = new File(DIRECTORY);
        File[] files = folder.listFiles();

        int fileIndex = (int)(Math.random() * files.length);

        List<String> fileStr = new ArrayList<>();
        try {
            fileStr = Files.readAllLines(files[fileIndex].toPath());
        } catch (Exception ex) {
            System.out.println("Error reading file.");
        }

        int height = fileStr.size();

        List<String[]> stringGrid = new ArrayList<>();

        for (int y = 0; y < height; y ++) {
            stringGrid.add(fileStr.get(y).split(","));
        }

        int width = 0;
        for (int y = 0; y < stringGrid.size(); y ++) {
            if (stringGrid.get(y).length > width) {
                width = stringGrid.get(y).length;
            }
        }

        tiles = new Tile[width][height];

        init_positions = new Position[2][3];

        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y ++) {
                Tile t = new Tile(new Position(x, y));

                if (stringGrid.get(height - y - 1).length > x) {
                    String s = stringGrid.get(height - y - 1)[x].trim();

                    if (s.equalsIgnoreCase("I")) {
                        t.setType(Tile.Type.INDESTRUCTIBLE);
                    } else if (s.length() >= 3 && s.charAt(0) == 'U') {
                        int playerNum = s.charAt(1) - '0';
                        int unitNum = s.charAt(2) - '0';

                        init_positions[playerNum - 1][unitNum] = new Position(x, y);
                    } else if (s.length() > 0) {
                        // should be a Destructible tile, so entry should be a number
                        try {
                            t.setHp(Integer.parseInt(s));
                            t.setType(Tile.Type.DESTRUCTIBLE);
                        } catch (NumberFormatException e) {
                            System.out.println("Found weird string at position (" + x + "," + y + ") while parsing map: " + s);
                        }
                    }
                }

                tiles[x][y] = t;
            }
        }
    }

    public Position[] getP1InitialPositions() {
        return init_positions[0];
    }

    public Position[] getP2InitialPositions() {
        return init_positions[1];
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
