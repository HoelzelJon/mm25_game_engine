package mech.mania;

import java.util.Arrays;

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

    public String[] getActions() {
        String[] str = new String[3];
        for (int i = 0; i < 3; i++) {//priorities
            str[i] = "";
            for (int j = 0; j < 3; j++) { // index of bot
                str[i] += "bot" + j + ":\n";
                if (priorities[j] == i + 1) {
                    for (int k = 0; k < movements[j].length; k++) {
                        str[i] += "movements: " + movements[j][k].name() + "\n";
                    }
                    str[i] += "attack: " + attacks[j] + "\n";
                }
            }
        }
        return str;
    }
}
