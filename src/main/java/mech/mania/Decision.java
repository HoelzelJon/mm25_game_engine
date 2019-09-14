package mech.mania;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents one turn's worth of decisions for a single player.
 */
class Decision {
    private int[] priorities; // priorities for each of the player's bots -- priorities[n] is the priority for bot n.
    // Since there are 3 bots, priorities should be one of [1,2,3]
    private Direction[][] movements; // movements[a] is the movement for bot a -- should always have length equal to that bot's speed
    private Direction[] attacks; // direction for each bot's attack

    Decision(int[] priorities, Direction[][] movements, Direction[] attacks) throws InvalidDecisionException {
        if (!validPriorities(priorities)) {
            throw new InvalidDecisionException("Invalid Priorities. Priorities must be 1, 2, and 3 in some order.");
        } else if (!validMovements(movements)) {
            throw new InvalidDecisionException("Invalid Movements. There must be one movement array per unit.");
        } else if (!validAttacks(attacks)) {
            throw new InvalidDecisionException("Invalid attacks. There must be one attack direction per unit.");
        }

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

    private static boolean validPriorities(int[] priorities) {
        if (priorities == null || priorities.length != Game.UNITS_PER_PLAYER) {
            return false;
        }

        Set<Integer> uniquePriorities = new HashSet<>();
        for (int priority : priorities) {
            if (uniquePriorities.contains(priority) || (priority != 1 && priority != 2 && priority != 3)) {
                return false;
            }

            uniquePriorities.add(priority);
        }

        return true;
    }

    private static boolean validMovements(Direction[][] movements) {
        return (movements != null && movements.length == Game.UNITS_PER_PLAYER);
    }

    private static boolean validAttacks(Direction[] attacks) {
        return (attacks != null && attacks.length == Game.UNITS_PER_PLAYER);
    }
}
