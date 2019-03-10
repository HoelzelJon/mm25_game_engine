package mech.mania;

import java.util.ArrayList;
import java.util.List;

public class GameTurn {
    private List<GameRound> rounds = new ArrayList<>();

    public void add(GameRound gameRound) {
        rounds.add(gameRound);
    }

    public String toString() {
        StringBuilder result = new StringBuilder("{\"rounds\":[");
        for (GameRound round : rounds) {
            result.append(round.toString());
            result.append(",");
        }
        result.deleteCharAt(result.length() - 1);
        result.append("]}");

        return result.toString();
    }
}

class GameRound {
    private RoundMovement[] movements;
    private Attack[] attacks;

    public GameRound(RoundMovement[] movements, Attack[] attacks) {
        this.movements = movements;
        this.attacks = attacks;
    }

    public String toString() {
        StringBuilder result = new StringBuilder("{\"roundMovements\":[");
        for (RoundMovement movement : movements) {
            result.append(movement.toString());
            result.append(",");
        }
        if (result.substring(result.length() - 1).equals(",")) {
            result.deleteCharAt(result.length() - 1);
        }
        result.append("],\"attacks\":[");

        for (Attack attack : attacks) {
            result.append(attack.toString());
            result.append(",");
        }
        if (result.substring(result.length() - 1).equals(",")) {
            result.deleteCharAt(result.length() - 1);
        }
        result.append("]}");
        return result.toString();
    }
}

class RoundMovement {
    private Movement[] movements;
    private UnitDamaged[] unitsDamaged;
    private UnitCollision[] unitCollisions;
    private TileCollision[] tileCollisions;

    RoundMovement(Movement[] movements,
                  UnitDamaged[] unitsDamaged,
                  UnitCollision[] unitCollisions,
                  TileCollision[] tileCollisions) {
        this.movements = movements;
        this.unitsDamaged = unitsDamaged;
        this.unitCollisions = unitCollisions;
        this.tileCollisions = tileCollisions;
    }

    public String toString() {
        StringBuilder result = new StringBuilder("{\"movements\":[");
        for (Movement movement : movements) {
            result.append(movement.toString());
            result.append(",");
        }
        if (result.substring(result.length() - 1).equals(",")) {
            result.deleteCharAt(result.length() - 1);
        }
        result.append("],");

        result.append("\"unitsDamaged\":[");
        for (UnitDamaged unitDamaged : unitsDamaged) {
            result.append(unitDamaged.toString());
            result.append(",");
        }
        if (result.substring(result.length() - 1).equals(",")) {
            result.deleteCharAt(result.length() - 1);
        }
        result.append("],");

        result.append("\"unitCollisions\":[");
        for (UnitCollision unitCollision : unitCollisions) {
            result.append(unitCollision.toString());
            result.append(",");
        }
        if (result.substring(result.length() - 1).equals(",")) {
            result.deleteCharAt(result.length() - 1);
        }
        result.append("],");

        result.append("\"tileCollisions\":[");
        for (TileCollision tileCollision : tileCollisions) {
            result.append(tileCollision.toString());
            result.append(",");
        }
        if (result.substring(result.length() - 1).equals(",")) {
            result.deleteCharAt(result.length() - 1);
        }
        result.append("]}");
        return result.toString();
    }
}

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

    public String toString() {
        return "{\"unitId\":" + unit.getId() +
                ",\"direction\":\"" + direction.toString() + "\"" +
                "}";
    }
}

class Attack {
    private Unit unit;
    private Direction direction;

    public Attack(Unit unit, Direction direction) {
        this.unit = unit;
        this.direction = direction;
    }

    public String toString() {
        return "{\"unitId\":" + unit.getId() +
                ",\"direction\":\"" + direction.toString() + "\"" +
                "}";
    }
}

class UnitDamaged {
    private Unit unit;
    private int damage;

    public UnitDamaged(Unit unit, int damage) {
        this.unit = unit;
        this.damage = damage;
    }

    public String toString() {
        return "{\"unitId\":" + unit.getId() +
                ",\"damage\":" + damage +
                "}";
    }
}

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

    public String toString() {
        return "{\"unitId\":" + unit1.getId() +
                ",\"tileId\":" + unit2.getId() +
                ",\"unit1HealthLost\":" + unit1HealthLost +
                ",\"unit2HealthLost\":" + unit2HealthLost +
                "}";
    }
}

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

    public String toString() {
        return "{\"unitId\":" + unit.getId() +
                ",\"tileId\":" + (tile == null ? "-1" : tile.getId()) +
                ",\"unitHealthLost\":" + unitHealthLost +
                ",\"tileHealthLost\":" + tileHealthLost +
                "}";
    }
}


//class RoundMovement {
//    private Unit unit;
//    private Direction direction;
//    // private Position finalPosition;
//    private Unit unitCollidedWithMe;
//    private Tile tileCollidedWith;
//
//
//
//    public RoundMovement(Unit unit, Direction direction, Unit unitCollidedWithMe, Tile tileCollidedWith) {
//        this.unit = unit;
//        this.direction = direction;
//        this.unitCollidedWithMe = unitCollidedWithMe;
//        this.tileCollidedWith = tileCollidedWith;
//    }
//
//
//    public void setDirection(Direction direction) {
//        this.direction = direction;
//    }
//
//    public void setUnitCollidedWithMe(Unit unitCollidedWithMe) {
//        this.unitCollidedWithMe = unitCollidedWithMe;
//    }
//
//    public void setTileCollidedWith(Tile tileCollidedWith) {
//        this.tileCollidedWith = tileCollidedWith;
//    }
//
//    public void setUnit(Unit unit) {
//        this.unit = unit;
//    }
//
//    public Unit getUnit() {
//        return unit;
//    }
//
//    public Direction getDirection() {
//        return direction;
//    }
//
//    public Unit getUnitCollidedWithMe() {
//        return unitCollidedWithMe;
//    }
//
//    public Tile getTileCollidedWith() {
//        return tileCollidedWith;
//    }
//}


