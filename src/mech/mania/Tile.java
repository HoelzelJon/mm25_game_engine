package mech.mania;

/**
 * Represents a single square on the game board.
 */
public class Tile {
    private static long globalId;
    private long id;
    private Position pos; // x,y coordinates of this tile
    private Unit unit; // the Unit present on this tile (or null, if no unit is present)
    private Type type; // the type of tile this is (see Type enum below)
    private int hp; // health of this tile (only important for DESTRUCTIBLE type)
    public static final int COLLISION_DAMAGE = 1;

    enum Type {
        BLANK, // blank tile -- nothing is on it (except maybe a unit)
        DESTRUCTIBLE, // destructible terrain -- becomes BLANK after hp is reduced to or below 0
                      // units cannot be on DESTRUCTIBLE-type tiles
        INDESTRUCTIBLE // indestructible terrain -- becomes BLANK after hp is reduced to or below 0
                       // units cannot be on INDESTRUCTIBLE-type tiles
    }

    public Tile(Position pos) {
        id = globalId++;
        this.pos = pos;
        unit = null;
        type = Type.BLANK;
        hp = 5;
    }

    public long getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    /**
     * Used for printing out human-readable string of the map
     * @return a 3-character representation of this tile
     */
    public String shortString() {
        if (type == Type.DESTRUCTIBLE) {
            return " D ";
        } else if (type == Type.INDESTRUCTIBLE) {
            return " I ";
        } else if (unit != null) {
            return " " + unit.getHp() + " ";
        } else return " . ";
    }

    /**
     * should only be called on a terrain tile -- doesn't affect units present on this tile
     */
    public void collided() {
        hp -= COLLISION_DAMAGE;

        if (hp <= 0 && type == Type.DESTRUCTIBLE) {
            type = Type.BLANK;
        }
    }

    /**
     * deals damage to a tile
     * @param dmg
     */
    public void takeDamage(int dmg) {
        //TODO
        if (unit == null) {
            hp -= dmg;

            if (hp <= 0 && type == Type.DESTRUCTIBLE) {
                type = Type.BLANK;
            }
        } else {
            unit.takeDamage(dmg);
        }

    }
}
