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

    public MovementRepresentation(int aBotId, Direction aDirection) {
        botId = aBotId;
        dir = aDirection;
        movementType = MovementType.Normal;
        collidedWith = Arrays.asList(-1, -1);
    }

    public void setCollision(Position collidedWithPosition) {
        movementType = MovementType.Collision;
        collidedWith = Arrays.asList(collidedWithPosition.x, collidedWithPosition.y);
    }

    public int getBotId() {
        return botId;
    }

    public MovementType getMovementType() {
        return movementType;
    }
}
