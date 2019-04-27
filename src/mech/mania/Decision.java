package mech.mania;

/**
 * Represents one turn's worth of decisions for a single player.
 */
public class Decision {
    private int[] priorities; // priorities for each of the player's bots [1,2,3]
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


    private static String errorMessage;
    static String getErrorMessage() { return errorMessage; }

    static boolean hasValidDecision(int[] priorities,
                                    Direction[][] movements, // not used (for now?)
                                    Direction[] attacks) {   // not used (for now?)
        for (int priority : priorities) {
            if (priority != 1 && priority != 2 && priority != 3) {
                errorMessage = "Priorities must be First, Second, or Third.";
                return false;
            }
        }

        for (int i = 0; i < priorities.length - 1; i++) {
            if (priorities[i] == priorities[i + 1]) {
                errorMessage = "There may not be any duplicate priorities.";
                return false;
            }
        }

        // attacks and movements do not have to be validated since they are
        // already Direction objects
        return true;
    }
}
