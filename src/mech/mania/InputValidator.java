package mech.mania;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that contains a bunch of static functions to be used when parsing/validating input
 */
public class InputValidator {
    public static int NUM_ROWS = 7;
    public static int NUM_COLS = 7;
    public static int MAX_POINTS = 14;
    public static int BOTS_PER_PLAYER = 3;
    //Maps rows of attack pattern array to valid indices in that row (since attack pattern is a diamond and array is a square)
    private static java.util.HashMap<Integer, ArrayList<Integer>> attackPatternRowIdxMap;

    static {
        attackPatternRowIdxMap = new HashMap<Integer, ArrayList<Integer>>();
        recurse(0, attackPatternRowIdxMap);
    }

    public static void printAttackPattern(int [][] attackPattern) {
        for (int r = 0; r < InputValidator.NUM_ROWS; r++) {
            for (int c = 0; c < InputValidator.NUM_COLS; c++) {
                System.out.print(attackPattern[r][c] + " ");
            }
            System.out.print("\n");
        }
    }

    /**
     * Sets up the hashmap, mapping rows of square attack pattern matrix to valid indices in each row
     * example: row 0 only has valid index 3
     * @param r row
     * @param map
     */
    private static void recurse(int r, HashMap<Integer, ArrayList<Integer>> map) {
        ArrayList<Integer> rowIndices = new ArrayList<>();
        int middleIdx = NUM_COLS/2;

        if (r != NUM_ROWS/2) rowIndices.add(middleIdx);
        for (int i = 1; i < r+1; i++) {
            rowIndices.add(middleIdx + i);
            rowIndices.add(middleIdx - i);
        }
        map.put(r, rowIndices);
        if (r == NUM_ROWS/2) return;
        recurse(r + 1, map);

        ArrayList<Integer> oppositeRowIndices = new ArrayList<>();
        oppositeRowIndices.add(middleIdx);
        for (int i = 1; i < r+1; i++) {
            oppositeRowIndices.add(middleIdx + i);
            oppositeRowIndices.add(middleIdx - i);
        }
        map.put(NUM_ROWS - 1 - r, oppositeRowIndices);
    }

    /**
     * Zeroes out mech position and unused positions in 7x7 attack pattern array to be used as a Diamond pattern.
     * @param attackPattern
     */
    public static void squareArrayToDiamond(int [][] attackPattern) {
        for (int r = 0; r < NUM_ROWS; r++) {
            ArrayList<Integer> rowIndices = attackPatternRowIdxMap.get(r);
            for (int c = 0; c < NUM_COLS; c++) {
                if (!rowIndices.contains(c)) {
                    attackPattern[r][c] = 0;
                }
            }
        }
    }

    /**
     * Helper function for calculating attack cost.
     * If attack value is 3 it costs (3 + 2 + 1) 6 points.
     * @return
     */
    public static int getAttackCost(int attack) {
        int cost = 0;
        for (int i = 1; i <= attack; i++) {
            cost += i;
        }
        return cost;
    }

    /**
     * Helper function to ensure players do not input more than limit points for attack pattern.
     */
    public static int getRowSum(int [] attackPatternRow, int rowIdx) {
        int sum = 0;
        ArrayList<Integer> rowIndices = attackPatternRowIdxMap.get(rowIdx);
        for (int c = 0; c < NUM_COLS; c++) {
            if (rowIndices.contains(c)) {
                int cost = getAttackCost(attackPatternRow[c]);
                sum += cost;
            }
        }
        return sum;
    }

}
