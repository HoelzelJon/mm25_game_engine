package mech.mania;

import java.util.Arrays;

public class UnitSetup {
    static final int BASE_HEALTH = 1;
    static final int BASE_SPEED = 1;
    static final int MAX_POINTS = 14;
    private static final double EXTRA_ATTACK_MULTIPLIER = 2;

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
            for (int col : row) {
                if (col > 1) {
                    sum += EXTRA_ATTACK_MULTIPLIER * (col - 1); // extra points to pattern cost 2 each
                }
                sum += col;
            }
        }

        if (sum > MAX_POINTS) {
            errorMessage = "too many points allotted in grid";
            return false;
        } else if (setHealth - BASE_HEALTH + setSpeed - BASE_SPEED + sum > MAX_POINTS) {
            errorMessage = "too many extra points in hp and speed";
            return false;
        }

        return true;
    }
}
