package mech.mania;

import java.util.Arrays;

public class UnitSetup {
    public static final int ATTACK_PATTERN_SIZE = 7;
    static final int BASE_HEALTH = 1;
    static final int BASE_SPEED = 1;
    static final int MAX_POINTS = 24;
    static final int TERRAIN_COST = 2;
    private static final int[] DAMAGE_SCALING = {
            1, 3, 6, 10, 15, 21, 27
    };
    private static final int[] STAT_SCALING = {
            1, 2, 4, 6, 9, 12, 16, 20, 25
    };

    private boolean[][] terrainPattern;
    private int[][] attackPattern;
    private int health;
    private int speed;

    int getHealth() { return health; }
    int getSpeed() { return speed; }
    int[][] getAttackPattern() { return attackPattern; }
    boolean[][] getTerrainPattern() { return terrainPattern; }

    void setHealth(int setHealth) { health = setHealth; }
    void setSpeed(int setSpeed) { speed = setSpeed; }
    void setAttackPattern(int[][] setAttackPattern) { attackPattern = setAttackPattern; }

    UnitSetup() {
        health = BASE_HEALTH;
        speed = BASE_SPEED;
    }

    UnitSetup(int[][] setAttackPattern, boolean[][] setTerrainCreation, int setHealth, int setSpeed) {
        attackPattern = setAttackPattern;
        terrainPattern = setTerrainCreation;
        health = setHealth;
        speed = setSpeed;
    }

    static boolean hasValidStartingConditions(UnitSetup[] units) {
        return Arrays.stream(units).allMatch(u ->
                UnitSetup.hasValidStartingConditions(u.health, u.speed, u.attackPattern, u.terrainPattern));
    }

    static boolean hasValidStartingConditions(int setHealth, int setSpeed, int[][] setAttackPattern, boolean[][] setTerrainCreation) {
        if (setHealth < BASE_HEALTH || setSpeed < BASE_SPEED
                || setAttackPattern.length != ATTACK_PATTERN_SIZE
                || setTerrainCreation.length != ATTACK_PATTERN_SIZE) {
            return false;
        }

        for (int i = 0; i < ATTACK_PATTERN_SIZE; i++) {
            if (setAttackPattern[i].length != ATTACK_PATTERN_SIZE || setTerrainCreation[i].length != ATTACK_PATTERN_SIZE) {
                return false;
            }
        }

        int sum = 0;
        for (int[] row : setAttackPattern) {
            for (int cell : row) {
                if (cell > 1) {
                    // avoid ArrayIndexOutOfBounds
                    if (cell >= DAMAGE_SCALING.length) {
                        sum = MAX_POINTS + 1;
                        break;
                    } else {
                        sum += DAMAGE_SCALING[cell - 1];
                    }

                } else if (cell < 0) {
                    return false;
                }

                sum += cell;
            }
        }

        for (boolean[] row : setTerrainCreation) {
            for (boolean creatingTerrain : row) {
                if (creatingTerrain) {
                    sum += TERRAIN_COST;
                }
            }
        }

        if (sum > MAX_POINTS) {
            return false;

        // avoid ArrayIndexOutOfBounds
        } else if (setHealth >= STAT_SCALING.length || setSpeed >= STAT_SCALING.length) {
            return false;

        } else if (STAT_SCALING[setHealth - 1] + STAT_SCALING[setSpeed - 1] + sum > MAX_POINTS) {
            return false;
        }

        return true;
    }
}
