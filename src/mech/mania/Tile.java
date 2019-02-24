package mech.mania;

/*
 * Represents a single square on the game board.
 */
public class Tile {
    private Position pos; // x,y coordinates of this tile
    private Unit unit;
    Type type;
    int health;

    enum Type {
        BLANK, DESTRUCTABLE, INDESTRUCTABLE
    }

    public Tile(Position pos) {
        this.pos = pos;
        unit = null;
        type = Type.BLANK;
        health = 0;
    }

    public Type getType() {
        return type;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String shortString() {
        if (type == Type.DESTRUCTABLE) {
            return " D ";
        } else if (type == Type.INDESTRUCTABLE) {
            return " I ";
        } else if (unit != null) {
            return " " + unit.getHp() + " ";
        } else return " . ";
    }

}
