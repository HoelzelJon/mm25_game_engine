package mech.mania;

import java.util.Objects;

/**
 * 'Position' class for representing the position of things on the board
 */
public class Position {
    public int x;
    public int y;

    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x &&
                y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * @param dir direction in which you want to move
     * @return position that is 1 unit from this, in direction dir
     *         (NOTE: the position (0,0) is the bottom-left of the board)
     */
    public Position getNewPosition(Direction dir) {
        if (dir == Direction.DOWN) {
            return new Position(x, y-1);
        } else if (dir == Direction.UP) {
            return new Position(x, y+1);
        } else if (dir == Direction.LEFT) {
            return new Position(x-1, y);
        } else if (dir == Direction.RIGHT) {
            return new Position(x+1, y);
        }
        return new Position(x, y);
    }
}
