package mech.mania;

/*
 * Represents a single mech.
 */
public class Unit {
    private int hp; // unit's current health
    private int speed; // unit's speed (number of tiles it can move per turn)
    private Tile.Position pos; // position of the unit
    private int[][] attack; // 2-D grid of attack damages
}
