package mech.mania;

/**
 * Represents one turn's worth of decisions for a single player.
 */
public class Decision {
    private int[] priorities; // priorities for each of the player's bots
    private Direction[][] movements; // movements[a] is the movement for bot a -- should always have length equal to that bot's speed
    private Direction[] attacks; // direction for each bot's attack

    public Decision(int[] priorities, Direction[][] movements, Direction[] attacks) {
        this.priorities = priorities;
        this.movements = movements;
        this.attacks = attacks;
    }

    public int[] getPriorities() {
        return priorities;
    }

    public Direction[][] getMovements() {
        return movements;
    }

    public Direction[] getAttacks() {
        return attacks;
    }
}
