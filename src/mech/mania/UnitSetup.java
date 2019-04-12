package mech.mania;

public class UnitSetup {
    private static int BASE_HEALTH = 2;
    private static int BASE_SPEED = 4;
    public int[][] attackPattern;
    public int health;
    public int speed;

    public UnitSetup() {
        health = BASE_HEALTH;
        speed = BASE_SPEED;
    }

    public UnitSetup(int[][] setAttackPattern, int setHealth, int setSpeed) {
        attackPattern = setAttackPattern;
        health = setHealth;
        speed = setSpeed;
    }
}
