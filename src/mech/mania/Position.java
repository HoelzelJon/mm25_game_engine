package mech.mania;

/*
 * 'Position' class for representing the position of things on the board
 */
public class Position {
    public final int x;
    public final int y;

    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position other = (Position) o;
            return (this.x == other.x) && (this.y == other.y);
        }
        else return false;
    }

    // (0,0) is bottom-left
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
