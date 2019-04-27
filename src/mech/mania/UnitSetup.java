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

    // GETTERS
    int getHealth() { return health; }
    int getSpeed() { return speed; }
    int[][] getAttackPattern() { return attackPattern; }

    // SETTERS (no validation for now)
    void setHealth(int setHealth) { health = setHealth; }
    void setSpeed(int setSpeed) { speed = setSpeed; }
    void setAttackPattern(int[][] setAttackPattern) { attackPattern = setAttackPattern; }

    // CONSTRUCTORS
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

    static boolean hasValidDecision(int[] priorities,
                                    Direction[][] movements,
                                    Direction[] attacks) {
        for (int priority : priorities) {
            if (priority != 1 && priority != 2 && priority != 3) {
                errorMessage = "Priorities must be First, Second, or Third.";
                return false;
            }
        }

        for (int i = 0; i < priorities.length - 1; i++) {
            if (priorities[i] == priorities[i + 1]) {
                errorMessage = "There may not be any duplicate priorities.";
                return false;
            }
        }

        // attacks and movements do not have to be validated since they are
        // already Direction objects
        return true;
    }
}
