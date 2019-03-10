package mech.mania;

/**
 * One round (series of three movements according to player's decisions)
 * A container class that groups data that doesn't fit under each Movement
 */
class GameRound {
    private RoundMovement[] movements;
    private DamagedTile[] damagedTiles;
    private DamagedUnit[] damagedUnits;
    private Attack[] attacks;

    public GameRound(RoundMovement[] movements,
                     DamagedTile[] damagedTiles,
                     DamagedUnit[] damagedUnits,
                     Attack[] attacks) {
        this.movements = movements;
        this.damagedTiles = damagedTiles;
        this.damagedUnits = damagedUnits;
        this.attacks = attacks;
    }
}

/**
 * One Movement Turn (characters of the same priority move)
 */
class RoundMovement {
    private Movement[] movements;
    private UnitCollision[] unitCollisions;
    private TileCollision[] tileCollisions;

    RoundMovement(Movement[] movements,
                  UnitCollision[] unitCollisions,
                  TileCollision[] tileCollisions) {
        this.movements = movements;
        this.unitCollisions = unitCollisions;
        this.tileCollisions = tileCollisions;
    }
}

/**
 * The specific movement of one unit.
 */
class Movement {
    private Unit unit;
    private Direction direction;

    public Unit getUnit() {
        return unit;
    }

    public Movement(Unit unit, Direction direction) {
        this.unit = unit;
        this.direction = direction;
    }
}

/**
 * Specific attack that one unit *may* choose to do.
 * Will not be considered an attack if the attack movement is Direction.STAY
 */
class Attack {
    private Unit unit;
    private Direction direction;

    public Attack(Unit unit, Direction direction) {
        this.unit = unit;
        this.direction = direction;
    }
}

/**
 * Specific instance of a unit being damaged by another unit.
 */
class DamagedUnit {
    private Unit unitThatAttacked;
    private Unit damagedUnit;
    private int damage;

    public DamagedUnit(Unit unitThatAttacked, Unit damagedUnit, int damage) {
        this.unitThatAttacked = unitThatAttacked;
        this.damagedUnit = damagedUnit;
        this.damage = damage;
    }
}

/**
 * Specific instance of a Tile being damaged by a Player's attack.
 */
class DamagedTile {
    private Unit unit;
    private Tile affectedTile;
    private int damage;

    public DamagedTile(Unit unit, Tile affectedTile, int damage) {
        this.unit = unit;
        this.affectedTile = affectedTile;
        this.damage = damage;
    }
}

/**
 * Specific instance of two units colliding into each other and taking damage.
 */
class UnitCollision {
    private Unit unit1;
    private Unit unit2;
    private int unit1HealthLost;
    private int unit2HealthLost;

    public UnitCollision(Unit unit1, Unit unit2, int unit1HealthLost, int unit2HealthLost) {
        this.unit1 = unit1;
        this.unit2 = unit2;
        this.unit1HealthLost = unit1HealthLost;
        this.unit2HealthLost = unit2HealthLost;
    }
}

/**
 * Specific instance of unit colliding with board boundaries or a Tile
 */
class TileCollision {
    private Unit unit;
    private Tile tile;
    private int unitHealthLost;
    private int tileHealthLost;

    public TileCollision(Unit unit, Tile tile, int unitHealthLost, int tileHealthLost) {
        this.unit = unit;
        this.tile = tile;
        this.unitHealthLost = unitHealthLost;
        this.tileHealthLost = tileHealthLost;
    }
}
