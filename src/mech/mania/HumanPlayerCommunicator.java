package mech.mania;

import java.util.Scanner;

public class HumanPlayerCommunicator extends PlayerCommunicator {
    private Scanner sk;

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
            if(myUnits[botId].isAlive()){
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
        }

        return new Decision(priorities, movements, attackDirs);
    }

    /**
     * Prompt user for initial attack patterns
     */
    @Override
    public int[][][] getAttackPatterns(){
        int numBots = 3;

        int[][][] attackPatterns = new int[numBots][][];
        int[][][] attackPatternsTransform = new int[numBots][][];

        // Iterate over bots
        for(int botId = 0; botId < numBots; botId++){
            System.out.println("Configuring attack pattern for bot " + botId);
            // Ask for numRows
            System.out.println("How many rows (y-values) does the attack pattern span?");
            int numRows = sk.hasNextInt()? sk.nextInt() : 0; // only get int if there is one -- avoids exceptions
            sk.nextLine();
            while(numRows < 0 || numRows%2 == 0){
                System.out.println("Must have a positive, odd number of rows. Enter a new value:");
                numRows = sk.hasNextInt()? sk.nextInt() : 0;
                sk.nextLine();
            }
            
            // ASSUMING SQUARE MATRIX
            System.out.println("Assuming square attack matrix");
            int numCols = numRows;
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
            attackPatterns[botId] = new int[numRows][numCols];
            System.out.println("Enter attack pattern with spaces between columns and newlines between rows like so:\n" +
                    "0 0 0\n" +
                    "0 0 0\n" +
                    "0 0 0");
            System.out.println("Your bot is in the center of the array.");
            for(int r = 0; r < numRows; r++){
                for(int c = 0; c < numCols; c++){
                    attackPatterns[botId][r][c] = sk.hasNextInt()? sk.nextInt() : 0;
                }
                sk.nextLine();
            }

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
