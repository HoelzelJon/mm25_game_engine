package mech.mania.visualizer.perTurn;

import mech.mania.Direction;
import mech.mania.Position;

import java.util.Arrays;
import java.util.List;

public class MovementRepresentation {
    private int botId;
    private Direction dir;
    private MovementType movementType;
    private List<Integer> collidedWith; // length-2 list representing the (x,y) coordinate of what this bot collided with
                                        // or (-1, -1) if no collision happened
    private int newUnitHealth;
    private int collidedHealth;
    private int collidedId; // unitId if it collided with a unit, else -1

    public MovementRepresentation(int aBotId, Direction aDirection) {
        botId = aBotId;
        dir = aDirection;
        movementType = MovementType.Normal;
        collidedWith = Arrays.asList(-1, -1);
        collidedHealth = -1;
        collidedId = -1;
    }

    public void collidedWithTerrain(Position collidedWithPosition, int setCollidedHealth, int setThisHealth) {
        movementType = MovementType.Collision;
        collidedHealth = setCollidedHealth;
        collidedWith = Arrays.asList(collidedWithPosition.x, collidedWithPosition.y);
        newUnitHealth = setThisHealth;
    }

    public void collidedWithUnit(Position collidedWithPosition, int setCollidedHealth, int setThisHealth, int setCollidedId) {
        movementType = MovementType.Collision;
        collidedHealth = setCollidedHealth;
        collidedWith = Arrays.asList(collidedWithPosition.x, collidedWithPosition.y);
        collidedId = setCollidedId;
        newUnitHealth = setThisHealth;
    }

    public int getBotId() {
        return botId;
    }

    public MovementType getMovementType() {
        return movementType;
    }
}
