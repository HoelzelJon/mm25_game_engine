package mech.mania;

import java.util.Arrays;

public class UnitSetup {
    // EDIT THESE VALUES IF NECESSARY
    static final int BASE_HEALTH = 1;
    static final int BASE_SPEED = 1;
    static final int MAX_POINTS = 24;
    private static final int[] DAMAGE_SCALING = {
            1, 3, 6, 10, 15, 21, 27
    };
    private static final int[] STAT_SCALING = {
            1, 2, 4, 6, 9, 12, 16, 20, 25
    };

    private int[][] attackPattern;
    private int health;
    private int speed;

    // GETTERS
    int getHealth() { return health; }
    int getSpeed() { return speed; }
    int[][] getAttackPattern() { return attackPattern; }

    // SETTERS (no validation for now)
    void setHealth(int setHealth) { health = setHealth; }
    void setSpeed(int setSpeed) { speed = setSpeed; }
    void setAttackPattern(int[][] setAttackPattern) { attackPattern = setAttackPattern; }

    // CONSTRUCTORS
    UnitSetup() {
        health = BASE_HEALTH;
        speed = BASE_SPEED;
    }

    UnitSetup(int[][] setAttackPattern, int setHealth, int setSpeed) {
        attackPattern = setAttackPattern;
        health = setHealth;
        speed = setSpeed;
    }


    /** Error message in case something fails */
    private static String errorMessage = "";
    static String getErrorMessage() { return errorMessage; }

    public static boolean hasValidStartingConditions(UnitSetup[] units) {
        return Arrays.stream(units).allMatch(u ->
                UnitSetup.hasValidStartingConditions(u.health, u.speed, u.attackPattern));
    }

    // validate for EACH bot
    static boolean hasValidStartingConditions(int setHealth, int setSpeed, int[][] setAttackPattern) {
        if (setHealth < BASE_HEALTH || setSpeed < BASE_SPEED) {
            errorMessage = String.format("hp and speed must be >= %d and >= %d, respectively",
                    BASE_HEALTH, BASE_SPEED);
            return false;
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
                    errorMessage = "cannot assign negative points";
                    return false;
                }

                sum += cell;
            }
        }

        if (sum > MAX_POINTS) {
            errorMessage = "too many points allotted in grid";
            return false;

        // avoid ArrayIndexOutOfBounds
        } else if (setHealth >= STAT_SCALING.length || setSpeed >= STAT_SCALING.length) {
            errorMessage = "too many extra points in hp and speed";
            return false;

        } else if (STAT_SCALING[setHealth - 1] + STAT_SCALING[setSpeed - 1] + sum > MAX_POINTS) {
            errorMessage = "too many extra points in hp and speed";
            return false;
        }

        return true;
    }
}
