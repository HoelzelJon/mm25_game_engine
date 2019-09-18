package mech.mania.playerCommunication;

import mech.mania.Game;

import java.util.List;
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

    public static boolean validUnitSetups(List<UnitSetup> units, List<Integer> ids) {
        if (units == null || units.size() != Game.UNITS_PER_PLAYER) {
            return false;
        }

        List<Integer> unitSetupIds = units.stream().map(u -> u.unitId).collect(Collectors.toList());
        if (!unitSetupIds.containsAll(ids) || !ids.containsAll(unitSetupIds)) {
            return false; // IDs in unit setups don't match with the actual unit id's
        }

        return units.stream().allMatch(UnitSetup::validUnitSetup);
    }

    public static boolean validUnitSetup(UnitSetup setup) {
        if (setup == null
                || setup.attackPattern == null
                || setup.terrainPattern == null
                || setup.health < BASE_HEALTH
                || setup.speed < BASE_SPEED
                || setup.attackPattern.length != ATTACK_PATTERN_SIZE
                || setup.terrainPattern.length != ATTACK_PATTERN_SIZE) {
            return false;
        }

        for (int x = 0; x < ATTACK_PATTERN_SIZE; x++) {
            if (setup.attackPattern[x].length != ATTACK_PATTERN_SIZE || setup.terrainPattern[x].length != ATTACK_PATTERN_SIZE) {
                return false;
            }

            for (int y = 0; y < ATTACK_PATTERN_SIZE; y ++) {
                if ((x == 3 && y == 3) || Math.abs(x-3) + Math.abs(y-3) > 3) {
                    // this position should not have any attack or terrain creation set
                    if (setup.attackPattern[x][y] > 0 || setup.terrainPattern[x][y]) {
                        return false;
                    }
                }
            }
        }

        int sum = 0;
        for (int[] row : setup.attackPattern) {
            for (int cell : row) {
                if (cell < 0 || cell >= DAMAGE_SCALING.length) {
                    return false;
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

        if (setup.health >= HEALTH_SCALING.length || setup.speed >= SPEED_SCALING.length) {
            return false;
        } else if (HEALTH_SCALING[setup.health - 1] + SPEED_SCALING[setup.speed - 1] + sum > MAX_POINTS) {
            return false;
        }

        return true;
    }
}
