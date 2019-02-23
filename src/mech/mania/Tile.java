package mech.mania;

/*
 * Represents a single square on the game board.
 */
public abstract class Tile {
    private Position pos; // x,y coordinates of this tile

    /*
     * 'Position' inner class for representing the position of things on the board
     */
    public class Position {
        public int x;
        public int y;
    }
}
