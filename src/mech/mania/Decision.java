package mech.mania;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents one turn's worth of decisions for a single player.
 */
class Decision {
    private int[] priorities; // priorities for each of the player's bots [1,2,3]
    private Direction[][] movements; // movements[a] is the movement for bot a -- should always have length equal to that bot's speed
    private Direction[] attacks; // direction for each bot's attack

    Decision(int[] priorities, Direction[][] movements, Direction[] attacks) {
        this.priorities = priorities;
        this.movements = movements;
        this.attacks = attacks;
    }

    int[] getPriorities() {
        return priorities;
    }

    Direction[][] getMovements() {
        return movements;
    }

    Direction[] getAttacks() {
        return attacks;
    }


    private static String errorMessage;
    static String getErrorMessage() { return errorMessage; }

    static boolean hasValidDecision(int[] priorities,
                                    Direction[][] movements, // not used (for now?)
                                    Direction[] attacks) {   // not used (for now?)

        Set<Integer> uniquePriorities = new HashSet<>();
        for (int priority : priorities) {
            uniquePriorities.add(priority);
            if (priority != 1 && priority != 2 && priority != 3) {
                errorMessage = "Priorities must be First(1), Second(2), or Third(3).";
                return false;
            }
        }

        if (uniquePriorities.size() != priorities.length) {
            errorMessage = "Cannot have duplicate priorities.";
            return false;
        }

        // attacks and movements do not have to be validated since they are
        // already Direction objects
        return true;
    }
}
