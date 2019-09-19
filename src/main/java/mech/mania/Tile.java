package mech.mania;

/**
 * Represents a single square on the game board.
 */
public class Tile {
    static final int COLLISION_DAMAGE = 1;
    private static int ID_COUNTER = 100;

    private int id;
    private Unit unit; // the Unit present on this tile (or null, if no unit is present)
    private Type type; // the type of tile this is (see Type enum below)
    private int hp; // health of this tile (only important for DESTRUCTIBLE type)

    public static Tile createIndestructible() {
        return new Tile(Type.INDESTRUCTIBLE, 1);
    }

    public static Tile createBlank() {
        return new Tile(Type.BLANK, 0);
    }

    public static Tile createDestructible(int hp) {
        return new Tile(Type.DESTRUCTIBLE, hp);
    }

    public enum Type {
        BLANK, // blank tile -- nothing is on it (except maybe a unit)
        DESTRUCTIBLE, // destructible terrain -- becomes BLANK after hp is reduced to or below 0
                      // units cannot be on DESTRUCTIBLE-type tiles
        INDESTRUCTIBLE // indestructible terrain -- becomes BLANK after hp is reduced to or below 0
                       // units cannot be on INDESTRUCTIBLE-type tiles
    }

    private Tile(Type aType, int aHp) {
        id = ID_COUNTER++;
        unit = null;
        type = aType;
        hp = aHp;
    }

    public int getId() {
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

    public int getHp() {
        return hp;
    }

    /**
     * Used for printing out human-readable string of the board
     * @return a 3-character representation of this tile
     */
    public String shortString() {
//        if (unit != null) {
//            return " *" + unit.getId() + "* ";
//        }
//        return String.format(" %03d ", id);
        if (type == Type.DESTRUCTIBLE) {
            if (hp < 100) {
                return String.format(" %-2d", hp);
            } else {
                return String.format("%-3d",hp);
            }
        } else if (type == Type.INDESTRUCTIBLE) {
            return " I ";
        } else if (unit != null) {
            if (unit.getPlayerNum() == 1) {
                return String.format("*%-2d", unit.getId());
            } else {
                return String.format("*%-2d", unit.getId());
            }
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
        if (unit == null) {
            if (type == Type.BLANK) {
                // blank tiles cannot be damaged
                return;
            }

            hp -= dmg;

            if (hp <= 0 && type == Type.DESTRUCTIBLE) {
                type = Type.BLANK;
            }
        } else {
            unit.takeDamage(dmg);
        }

    }
}
