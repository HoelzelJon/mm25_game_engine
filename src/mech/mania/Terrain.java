package mech.mania;

/*
 * Represents a tile which has terrain on it (either destructible or indestructible)
 */
public class Terrain extends Tile {
    private boolean destructable; // true iff this terrain is destructible
    private int hp; // number of hit points that this terrain has
}
