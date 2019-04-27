package mech.mania;

import java.util.Arrays;

public class UnitSetup {
    private static final int BASE_HEALTH = 4;
    private static final int BASE_SPEED = 5;
    private static final int MAX_POINTS = 14;
    private static final double EXTRA_ATTACK_MULTIPLIER = 2;

    private int[][] attackPattern;
    private int health;
    private int speed;

    int getHealth() { return health; }
    int getSpeed() { return speed; }
    int[][] getAttackPattern() { return attackPattern; }

    public UnitSetup() {
        health = BASE_HEALTH;
        speed = BASE_SPEED;
    }

    public UnitSetup(int[][] setAttackPattern, int setHealth, int setSpeed) {
        attackPattern = setAttackPattern;
        health = setHealth;
        speed = setSpeed;
    }


    /** Error message in case something fails */
    private static String errorMessage = "";
    public static String getErrorMessage() { return errorMessage; }

    public static boolean hasValidStartingConditions(UnitSetup[] units) {
        return Arrays.stream(units).allMatch(UnitSetup::hasValidStartingConditions);
    }

    // validate for EACH bot
    public static boolean hasValidStartingConditions(UnitSetup unit) {
        if (unit.getHealth() < BASE_HEALTH || unit.getSpeed() < BASE_SPEED) {
            errorMessage = String.format("hp and speed must be >= %d and >= %d, respectively",
                    BASE_HEALTH, BASE_SPEED);
            return false;
        }

        int sum = 0;
        for (int[] row : unit.getAttackPattern()) {
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
        } else if (unit.getHealth() - BASE_HEALTH + unit.getSpeed() - BASE_SPEED + sum > MAX_POINTS) {
            errorMessage = "too many extra points in hp and speed";
            return false;
        }

        return true;
    }
}
