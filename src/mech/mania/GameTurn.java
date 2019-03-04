package mech.mania;

import java.util.ArrayList;
import java.util.List;

public class GameTurn {
    private List<GameRound> rounds = new ArrayList<>();

    public void addRound(GameRound round) {
        rounds.add(round);
    }

    public List<GameRound> getRounds() {
        return rounds;
    }

    public String toJson() {
        return "";
    }
}

class GameRound {
    private List<RoundMovement> movements = new ArrayList<>();

    public void addMovement(Unit unit,
                            Direction direction,
                            Unit unitCollidedWithMe,
                            Tile tileCollidedWith) {
        movements.add(new RoundMovement(unit, direction, unitCollidedWithMe, tileCollidedWith));
    }

    public List<RoundMovement> getMovements() {
        return movements;
    }
}

class RoundMovement {
    private Unit unit;
    private Direction direction;
    // private Position finalPosition;
    private Unit unitCollidedWithMe;
    private Tile tileCollidedWith;



    public RoundMovement(Unit unit, Direction direction, Unit unitCollidedWithMe, Tile tileCollidedWith) {
        this.unit = unit;
        this.direction = direction;
        this.unitCollidedWithMe = unitCollidedWithMe;
        this.tileCollidedWith = tileCollidedWith;
    }



    public Unit getUnit() {
        return unit;
    }

    public Direction getDirection() {
        return direction;
    }

    public Unit getUnitCollidedWithMe() {
        return unitCollidedWithMe;
    }

    public Tile getTileCollidedWith() {
        return tileCollidedWith;
    }
}
