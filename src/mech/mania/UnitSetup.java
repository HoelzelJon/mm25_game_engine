package mech.mania;

public class UnitSetup {
    private static int BASE_HEALTH = 5;
    private static int BASE_SPEED = 4;
    public int[][] attackPattern;
    public int health;
    public int speed;

    public UnitSetup() {
        health = BASE_HEALTH;
        speed = BASE_SPEED;
    }
}
