package mech.mania;

import javafx.util.Pair;
import mech.mania.visualizer.perTurn.AttackRepresentation;
import mech.mania.visualizer.perTurn.UnitStatusRepresentation;
import mech.mania.visualizer.perTurn.TerrainStatusRepresentation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static mech.mania.playerCommunication.UnitSetup.ATTACK_PATTERN_SIZE;

/**
 * Stores the 2-D array of tiles that makes up the game board.
 * May also be responsible for generating random boards, if that is what we end up doing.
 */
public class Board {
    private String gameId;
    private Tile[][] tiles; // 2-D array of all tiles on the board
    private List<UninitializedUnit> initUnits;

    /**
     * Default board constructor: generates board based on a random file from DIRECTORY, in the following format:
     * - formatted as a .csv file (columns separated by commas, rows separated by newlines)
     * - Indestructible tiles are marked with 'I'
     * - Destructible tiles marked with an integer for their health
     * - Tiles which are initial spawns for units should be 'A#' or 'B#', where:
     *      - it is player 1's unit if the first character is 'A', or player 2's if the first character is 'B'
     *      - the # is replaced by the ID of the unit
     */
    public Board(String fileLocation, String gameId) throws IOException {
        this.gameId = gameId;
        File file = new File(fileLocation);

        List<String> fileStr = Files.readAllLines(file.toPath());

        int height = fileStr.size();

        List<String[]> stringGrid = new ArrayList<>();

        for (String s1 : fileStr) {
            stringGrid.add(s1.split(","));
        }

        int width = 0;
        for (String[] strings : stringGrid) {
            if (strings.length > width) {
                width = strings.length;
            }
        }

        tiles = new Tile[width][height];

        initUnits = new ArrayList<>();

        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y ++) {
                Tile t;

                if (stringGrid.get(height - y - 1).length > x) {
                    String s = stringGrid.get(height - y - 1)[x].trim();

                    if (s.equalsIgnoreCase("I")) {
                        t = Tile.createIndestructible();
                    } else if (s.length() >= 2 && (s.charAt(0) == 'A' || s.charAt(0) == 'B')) {
                        int playerNum = s.charAt(0) == 'A' ? 1 : 2;
                        int unitId = Integer.parseInt(s.substring(1));
                        initUnits.add(new UninitializedUnit(unitId, playerNum, new Position(x, y)));
                        t = Tile.createBlank();
                    } else if (s.length() > 0) {
                        // should be a Destructible tile, so entry should be a number
                        t = Tile.createDestructible(Integer.parseInt(s));
                    } else {
                        t = Tile.createBlank();
                    }
                } else {
                    t = Tile.createBlank();
                }

                tiles[x][y] = t;
            }
        }
        initUnits.sort(Comparator.comparingInt(UninitializedUnit::getUnitId));
    }

    public List<UninitializedUnit> getInitialUnits(int playerNum) {
        return initUnits.stream().filter(aUnit -> aUnit.getPlayerNum() == playerNum).collect(Collectors.toList());
    }

    public List<Integer> getUnitIds(int playerNum) {
        return getInitialUnits(playerNum).stream().map(UninitializedUnit::getUnitId).collect(Collectors.toList());
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
    void moveUnits(List<Unit> units, List<Position> destinations) {
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
     * @param units List of units doing the attacking (concurrently)
     * @param attackDirections List of directions for each unit's attack (parallel to units)
     * @return A representation of the attack that happened
     */
    List<AttackRepresentation> doAttacks(List<Unit> units, List<Direction> attackDirections) {
        Map<Position, List<Pair<AttackTile, Integer>>> attacksAndUnitIdsAtPosition = new HashMap<>();
        Map<Integer, AttackRepresentation> attackRepresentationByUnitId = new HashMap<>();

        for (Unit u : units) {
            attackRepresentationByUnitId.put(u.getId(), new AttackRepresentation(u.getId()));
        }

        // accumulate what is happening at each position on the board
        for (int unitNum = 0; unitNum < units.size(); unitNum ++) {
            AttackTile[][] attack = units.get(unitNum).getAttack(attackDirections.get(unitNum));
            int x0 = units.get(unitNum).getPos().x - (ATTACK_PATTERN_SIZE / 2); // x-coordinate of where attack[0][0] will go
            int y0 = units.get(unitNum).getPos().y - (ATTACK_PATTERN_SIZE / 2); // y-coordinate of where attack[0][0] will go

            for (int attackXIdx = 0; attackXIdx < ATTACK_PATTERN_SIZE; attackXIdx++) {
                if (x0 + attackXIdx >= 0 && x0 + attackXIdx < width()) {
                    for (int attackYIdx = 0; attackYIdx < ATTACK_PATTERN_SIZE; attackYIdx++) {
                        if (y0 + attackYIdx >= 0 && y0 + attackYIdx < height()) {
                            if (attack[attackXIdx][attackYIdx].getDamage() <= 0) {
                                continue;
                            }

                            Position boardPos = new Position(attackXIdx + x0, attackYIdx + y0);
                            attacksAndUnitIdsAtPosition
                                    .computeIfAbsent(boardPos, pos -> new ArrayList<>())
                                    .add(new Pair<>(attack[attackXIdx][attackYIdx], units.get(unitNum).getId()));
                        }
                    }
                }
            }
        }

        // use the accumulated information to change the board
        for (Map.Entry<Position, List<Pair<AttackTile, Integer>>> entry : attacksAndUnitIdsAtPosition.entrySet()) {
            Position pos = entry.getKey();

            if (entry.getValue().size() == 2) {
                int unitId1 = entry.getValue().get(0).getValue();
                AttackTile attackTile1 = entry.getValue().get(0).getKey();

                int unitId2 = entry.getValue().get(1).getValue();
                AttackTile attackTile2 = entry.getValue().get(1).getKey();

                if (tileAt(pos).getType() == Tile.Type.DESTRUCTIBLE) {
                    int totalDamage = attackTile1.getDamage() + attackTile2.getDamage();
                    TerrainStatusRepresentation terrainStatus = doDamageTerrain(pos, totalDamage);

                    attackRepresentationByUnitId.get(unitId1).addDamagedTerrain(terrainStatus);
                    attackRepresentationByUnitId.get(unitId2).addDamagedTerrain(terrainStatus);
                } else if (tileAt(pos).getUnit() != null) {
                    int totalDamage = attackTile1.getDamage() + attackTile2.getDamage();
                    UnitStatusRepresentation damagedUnitStatus = doDamageUnit(pos, totalDamage);

                    attackRepresentationByUnitId.get(unitId1).addDamagedUnit(damagedUnitStatus);
                    attackRepresentationByUnitId.get(unitId2).addDamagedUnit(damagedUnitStatus);
                } else if (tileAt(pos).getType() == Tile.Type.BLANK) {
                    if (attackTile1.isTerrainCreation() && attackTile2.isTerrainCreation()) {
                        int health = Math.max(attackTile1.getDamage(), attackTile2.getDamage());
                        TerrainStatusRepresentation createdTerrainStatus = doBuildTerrain(pos, health);

                        attackRepresentationByUnitId.get(unitId1).addCreatedTerrain(createdTerrainStatus);
                        attackRepresentationByUnitId.get(unitId2).addCreatedTerrain(createdTerrainStatus);
                    } else if (attackTile1.isTerrainCreation()) {
                        int health = attackTile1.getDamage() - attackTile2.getDamage();

                        TerrainStatusRepresentation terrainStatus = health > 0
                                ? doBuildTerrain(pos, health)
                                : new TerrainStatusRepresentation(pos, 0);
                        attackRepresentationByUnitId.get(unitId1).addCreatedTerrain(terrainStatus);
                        attackRepresentationByUnitId.get(unitId2).addDamagedTerrain(terrainStatus);
                    } else if (attackTile2.isTerrainCreation()) {
                        int health = attackTile2.getDamage() - attackTile1.getDamage();

                        TerrainStatusRepresentation terrainStatus = health > 0
                                ? doBuildTerrain(pos, health)
                                : new TerrainStatusRepresentation(pos, 0);
                        attackRepresentationByUnitId.get(unitId2).addCreatedTerrain(terrainStatus);
                        attackRepresentationByUnitId.get(unitId1).addDamagedTerrain(terrainStatus);
                    }
                }
            } else if (entry.getValue().size() == 1) {
                Pair<AttackTile, Integer> pair = entry.getValue().get(0);
                int unitId = pair.getValue();
                AttackTile attackTile = pair.getKey();

                if (tileAt(pos).getType() == Tile.Type.DESTRUCTIBLE) {
                    attackRepresentationByUnitId.get(unitId).addDamagedTerrain(doDamageTerrain(pos, attackTile.getDamage()));
                } else if (tileAt(pos).getUnit() != null) {
                    attackRepresentationByUnitId.get(unitId).addDamagedUnit(doDamageUnit(pos, attackTile.getDamage()));
                } else if (tileAt(pos).getType() == Tile.Type.BLANK && attackTile.isTerrainCreation()) {
                    attackRepresentationByUnitId.get(unitId).addCreatedTerrain(doBuildTerrain(pos, attackTile.getDamage()));
                }
            } else if (entry.getValue().size() == 0) {
                // do nothing
            } else {
                System.err.println("Error in attack logic -- more than 2 actions occurring on the same tile");
            }
        }

        return new ArrayList<>(attackRepresentationByUnitId.values());
    }

    private TerrainStatusRepresentation doDamageTerrain(Position pos, int damage) {
        Tile t = tileAt(pos);
        t.takeDamage(damage);
        return new TerrainStatusRepresentation(pos, t.getHp());
    }

    private UnitStatusRepresentation doDamageUnit(Position pos, int damage) {
        Unit damagedUnit = tileAt(pos).getUnit();
        damagedUnit.takeDamage(damage);
        return new UnitStatusRepresentation(damagedUnit.getId(), damagedUnit.getHp());
    }

    private TerrainStatusRepresentation doBuildTerrain(Position pos, int health) {
        Tile t = tileAt(pos);
        t.setType(Tile.Type.DESTRUCTIBLE);
        t.setHp(health);
        return new TerrainStatusRepresentation(pos, t.getHp());
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

    /**
     * @param pos a position that may or may not be on the board
     * @return true if the position lies within the board, false otherwise
     */
    boolean inBounds(Position pos) {
        return (pos.x >= 0 && pos.x < width() && pos.y >= 0 && pos.y < height());
    }

    public String getGameId(){ return gameId; }
}
