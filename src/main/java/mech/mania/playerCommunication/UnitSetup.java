package mech.mania.playerCommunication;

import mech.mania.Game;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UnitSetup {
    public static final int ATTACK_PATTERN_SIZE = 7;
    public static final int BASE_HEALTH = 1;
    public static final int BASE_SPEED = 1;
    static final int MAX_POINTS = 24;
    static final int TERRAIN_COST = 2;
    private static final int[] DAMAGE_SCALING = {
            1, 3, 6, 10, 15, 21, 27
    };
    private static final int[] SPEED_SCALING = {
            1, 2, 4, 6, 9, 12, 16, 20, 25
    };
    private static final int[] HEALTH_SCALING = {
            1, 2, 4, 6, 9, 12, 16, 20, 25
    };


    private boolean[][] terrainPattern;
    private int[][] attackPattern;
    private int health;
    private int speed;
    private int unitId;

    public int getHealth() { return health; }
    public int getSpeed() { return speed; }
    public int getUnitId() { return unitId; }
    public int[][] getAttackPattern() { return attackPattern; }
    public boolean[][] getTerrainPattern() { return terrainPattern; }

    public UnitSetup(int[][] setAttackPattern, boolean[][] setTerrainCreation, int setHealth, int setSpeed, int setUnitId) {
        attackPattern = setAttackPattern;
        terrainPattern = setTerrainCreation;
        health = setHealth;
        speed = setSpeed;
        unitId = setUnitId;
    }

    public static void throwExceptionOnInvalidSetupList(List<UnitSetup> setups, List<Integer> ids) throws InvalidSetupException {
        if (setups == null) {
            throw new InvalidSetupException("Setup list is null");
        } else if (setups.size() != Game.UNITS_PER_PLAYER) {
            throw new InvalidSetupException("Setup list is incorrect size. Expected " + Game.UNITS_PER_PLAYER + ", received " + setups.size());
        } else if (setups.stream().anyMatch(Objects::isNull)) {
            throw new InvalidSetupException("Setup list contains one or more null elements");
        }

        List<Integer> unitSetupIds = setups.stream().map(s -> s.unitId).collect(Collectors.toList());
        if (!unitSetupIds.containsAll(ids) || !ids.containsAll(unitSetupIds)) {
            throw new InvalidSetupException("Setup list contains different unit Ids than this player's units");
        }

        for (UnitSetup setup : setups) {
            try {
                throwExceptionOnInvalidSetup(setup);
            } catch (InvalidSetupException ex) {
                throw new InvalidSetupException("Invalid setup for unitId " + setup.unitId + ": " + ex.getMessage());
            }
        }
    }

    public static void throwExceptionOnInvalidSetup(UnitSetup setup) throws InvalidSetupException {
        if (setup.attackPattern == null) {
            throw new InvalidSetupException("Setup contains null attack pattern");
        } else if (setup.terrainPattern == null) {
            throw new InvalidSetupException("Setup contains null terrain pattern");
        } else if (setup.health < BASE_HEALTH || setup.health >= HEALTH_SCALING.length) {
            throw new InvalidSetupException("Invalid health value: " + setup.health);
        } else if (setup.speed < BASE_SPEED || setup.speed >= SPEED_SCALING.length) {
            throw new InvalidSetupException("Invalid speed value: " + setup.speed);
        } else if (setup.attackPattern.length != ATTACK_PATTERN_SIZE) {
            throw new InvalidSetupException("Incorrect size of attack pattern in setup");
        } else if (setup.terrainPattern.length != ATTACK_PATTERN_SIZE) {
            throw new InvalidSetupException("Incorrect size of terrain pattern in setup");
        }

        for (int x = 0; x < ATTACK_PATTERN_SIZE; x++) {
            if (setup.attackPattern[x].length != ATTACK_PATTERN_SIZE) {
                throw new InvalidSetupException("Incorrect size of attack pattern");
            } else if (setup.terrainPattern[x].length != ATTACK_PATTERN_SIZE) {
                throw new InvalidSetupException("Incorrect size of terrain pattern");
            }

            for (int y = 0; y < ATTACK_PATTERN_SIZE; y ++) {
                if ((x == 3 && y == 3) || Math.abs(x-3) + Math.abs(y-3) > 3) {
                    if (setup.attackPattern[x][y] > 0){
                        throw new InvalidSetupException("Non-zero damage set outside of allowed attack bounds");
                    } else if (setup.terrainPattern[x][y]) {
                        throw new InvalidSetupException("Terrain creation set to true outside of allowed attack bounds");
                    } else if (setup.attackPattern[x][y] == 0 && setup.terrainPattern[x][y]) {
                        throw new InvalidSetupException("Terrain creation set to true on tile with 0 attack");
                    }
                }
            }
        }

        int sum = 0;
        for (int[] row : setup.attackPattern) {
            for (int cell : row) {
                if (cell < 0 || cell >= DAMAGE_SCALING.length) {
                    throw new InvalidSetupException("Invalid damage value set in attack pattern: " + cell);
                }  else if (cell > 1) {
                    sum += DAMAGE_SCALING[cell - 1];
                }
            }
        }

        for (boolean[] row : setup.terrainPattern) {
            for (boolean creatingTerrain : row) {
                if (creatingTerrain) {
                    sum += TERRAIN_COST;
                }
            }
        }

        if (setup.health > BASE_HEALTH) {
            sum += HEALTH_SCALING[setup.health - BASE_HEALTH - 1];
        }
        if (setup.speed > BASE_SPEED) {
            sum += SPEED_SCALING[setup.speed - BASE_SPEED - 1];
        }
        if (sum > MAX_POINTS) {
            throw new InvalidSetupException("Setup cost too high: " + sum);
        }
    }
}
