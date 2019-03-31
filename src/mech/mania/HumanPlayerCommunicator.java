package mech.mania;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class HumanPlayerCommunicator extends PlayerCommunicator {
    private Scanner sk;
    //TODO: check that input priorities are different
    private static int NUM_ROWS = 7;
    private static int NUM_COLS = 7;
    private static int MAX_POINTS = 14;
    //Maps rows of attack pattern array to valid indices in that row (since attack pattern is a diamond and array is a square)
    private static java.util.HashMap<Integer, ArrayList<Integer>> attackPatternRowIdxMap;

    private void initializeAttackPatternMap() {
        attackPatternRowIdxMap = new HashMap<Integer, ArrayList<Integer>>();
        recurse(0, attackPatternRowIdxMap);
    }

    private void recurse(int r, HashMap<Integer, ArrayList<Integer>> map) {
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
    private void squareArrayToDiamond(int [][] attackPattern) {
        for (int r = 0; r < NUM_ROWS; r++) {
            ArrayList<Integer> rowIndices = attackPatternRowIdxMap.get(r);
            for (int c = 0; c < NUM_COLS; c++) {
                if (!rowIndices.contains(c)) {
                    attackPattern[r][c] = 0;
                }
            }
        }
    }

    private void printAttackPattern(int [][] attackPattern) {
        for (int r = 0; r < NUM_ROWS; r++) {
            for (int c = 0; c < NUM_COLS; c++) {
                System.out.print(attackPattern[r][c] + " ");
            }
            System.out.print("\n");
        }
    }

    private void printAttackPatternInfo() {
        System.out.println(" X X X 0 X X X");
        System.out.println(" X X 0 0 0 X X");
        System.out.println(" X 0 0 0 0 0 X");
        System.out.println(" 0 0 0 M 0 0 0");
        System.out.println(" X 0 0 0 0 0 X");
        System.out.println(" X X 0 0 0 X X");
        System.out.println(" X X X 0 X X X");
    }

    /**
     * Helper function to ensure players do not input more than limit points for attack pattern.
     */
    private int getRowSum(int [] attackPatternRow, int rowIdx) {
        int sum = 0;
        ArrayList<Integer> rowIndices = attackPatternRowIdxMap.get(rowIdx);
        for (int c = 0; c < NUM_COLS; c++) {
            if (rowIndices.contains(c)) {
                sum += attackPatternRow[c];
            }
        }
        return sum;
    }

    public HumanPlayerCommunicator(int playerNum){
        super(playerNum);
        sk = new Scanner(System.in);
        // Do nothing
    }

    /**
     * Prompt user for priorities, movements, and attacks
     */
    @Override
    public Decision getDecision(Game gameState){
        // Print gameState and Unit's stats for user to see
        System.out.println(gameState.getMapString());
        System.out.println(gameState.getUnitStatsString());

        System.out.println("**********Player " + playerNum + "**********");

        Unit[] myUnits = gameState.getPlayerUnits(playerNum);

        // Variables to fill
        int[] priorities = new int[myUnits.length];
        Direction[][] movements = new Direction[myUnits.length][]; // movements is a jagged array since speeds could be different
        Direction[] attackDirs = new Direction[myUnits.length];

        // Iterate through bots
        for(int botId = 0; botId < myUnits.length; botId++){
            // Ask for priority
            System.out.println("Specify priority for bot " + botId + " (board ID " + myUnits[botId].getId() + ") (1 to " + myUnits.length + ", 1 is first):");
            priorities[botId] = sk.hasNextInt()? sk.nextInt() : 0;
            sk.nextLine();
            while(priorities[botId] < 1 || priorities[botId] > myUnits.length){
                System.out.println("Priority must be a number from 1 to 3. Entery priority for bot " + botId + ":");
                priorities[botId] = sk.hasNextInt()? sk.nextInt() : 0;
                sk.nextLine();
            }

            // Ask for movement
            movements[botId] = new Direction[myUnits[botId].getSpeed()];
            System.out.println("Specify movement steps for bot " + botId + ". U = up, D = down, L = left, R = right, S = stay");
            String moveSteps = sk.nextLine();
            for(int step = 0; step < movements[botId].length; step++){
                try{
                    movements[botId][step] = dirFromChar(moveSteps.charAt(step));
                }
                catch(Exception e){
                    // Triggers if user didn't enter all 4 movement steps
                    movements[botId][step] = Direction.STAY;
                }
            }

            // Ask for attack direction
            System.out.println("Specify attack direction for bot " + botId + ". U = up, D = down, L = left, R = right, S = stay");
            try{
                attackDirs[botId] = dirFromChar(sk.nextLine().charAt(0));
            }
            catch(Exception e){
                // Triggers if user doesn't enter anything
                attackDirs[botId] = Direction.STAY;
            }
        }

        return new Decision(priorities, movements, attackDirs);
    }

    /**
     * Prompt user for initial attack patterns
     */
    @Override
    public int[][][] getAttackPatterns(){
        initializeAttackPatternMap();
        int numBots = 3;

        int[][][] attackPatterns = new int[numBots][][];
        int[][][] attackPatternsTransform = new int[numBots][][];

        // Iterate over bots
        for(int botId = 0; botId < numBots; botId++){
            System.out.println("Configuring attack pattern for bot " + botId);
            // Ask for numRows
//            System.out.println("How many rows (y-values) does the attack pattern span?");
//            int numRows = sk.hasNextInt()? sk.nextInt() : 0; // only get int if there is one -- avoids exceptions
//            sk.nextLine();
//            while(numRows < 0 || numRows%2 == 0){
//                System.out.println("Must have a positive, odd number of rows. Enter a new value:");
//                numRows = sk.hasNextInt()? sk.nextInt() : 0;
//                sk.nextLine();
//            }
            
//           ASSUMING SQUARE MATRIX
            //int numCols = numRows;
            /*
            // Ask for columns
            System.out.println("How many columns (x-values) does the attack pattern span?");
            int numCols = sk.nextInt();
            sk.nextLine();
            while(numCols < 0 || numCols%2 == 0){
                System.out.println("Must have a positive, odd number of columns. Enter a new value:");
                numCols = sk.nextInt();
                sk.nextLine();
            }
            */

            // Fill in attack pattern values

            attackPatterns[botId] = new int[NUM_ROWS][NUM_COLS];
            System.out.println("Attack pattern looks like so:");
            printAttackPatternInfo();
            System.out.println("Numbers in X and M positions are not counted.");
            System.out.println("Your bot is at the center of the array.");
            int totalSum = 0;
            for(int r = 0; r < NUM_ROWS; r++) {
                while (true) {
                    System.out.println("Enter row " + Integer.toString(r) + " of attack pattern\n");
                    for (int c = 0; c < NUM_COLS; c++) {
                        attackPatterns[botId][r][c] = sk.hasNextInt() ? sk.nextInt() : 0;
                    }
                    int rowSum = getRowSum(attackPatterns[botId][r], r);
                    System.out.println("You used " + Integer.toString(rowSum) + " points on this row");
                    if (totalSum + rowSum > MAX_POINTS) {
                        System.out.println("Not enough points for this! Try again.");
                    } else {
                        totalSum += rowSum;
                        break;
                    }
                }
                sk.nextLine();
            }
            System.out.println("Selected Pattern:");
            squareArrayToDiamond(attackPatterns[botId]);
            printAttackPattern(attackPatterns[botId]);
            // Transform attackPatterns matrix into correct coordinates
            // 1 2 3      7 8 9
            // 4 5 6  ->  4 5 6
            // 7 8 9      1 2 3
            attackPatternsTransform[botId] = Map.toGameCoords(attackPatterns[botId]);
        }

        return attackPatternsTransform;
    }

    /**
     * Helper method to convert a UDLRS character to a Direction
     */
    public Direction dirFromChar(char d){
        d = Character.toUpperCase(d);
        if(!Character.isUpperCase(d)){
            // d was not a letter
            return Direction.STAY;
        }
        switch(d){
            case 'U':
                return Direction.UP;
            case 'D':
                return Direction.DOWN;
            case 'L':
                return Direction.LEFT;
            case 'R':
                return Direction.RIGHT;
            default:
                return Direction.STAY;
        }
    }
}
