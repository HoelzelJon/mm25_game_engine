package mech.mania;

import java.util.Scanner;

/**
 * Created by prith on 3/2/2019.
 */
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
        //TODO: build Decision object from user input and pass it

        Unit[] myUnits = gameState.getPlayerUnits(playerNum);

        // Variables to fill
        int[] priorities = new int[myUnits.length];
        Direction[][] movements = new Direction[myUnits.length][]; // movements is a jagged array since speeds could be different
        Direction[] attackDirs = new Direction[myUnits.length];

        // Iterate through bots
        for(int botId = 0; botId < myUnits.length; botId++){
            // Ask for priority
            System.out.println("Specify priority for bot " + botId + " (1 to " + myUnits.length + ", 1 is first):");
            priorities[botId] = sk.nextInt();
            sk.nextLine();
            while(priorities[botId] < 1 || priorities[botId] > myUnits.length){
                System.out.println("Priority must be from 1 to 3. Entery priority for bot " + botId + ":");
                priorities[botId] = sk.nextInt();
                sk.nextLine();
            }

            // Ask for movement
            movements[botId] = new Direction[myUnits[botId].getSpeed()];
            System.out.println("Specify movement steps for bot " + botId + ". U = up, D = down, L = left, R = right, S = stay");
            for(int step = 0; step < movements[botId].length; step++){
                movements[botId][step] = dirFromChar(sk.nextChar());
            }
            sk.nextLine();

            // Ask for attack direction
            System.out.println("Specify attack direction for bot " + botId + ". U = up, D = down, L = left, R = right, S = stay");
            attackDirs[botId] = dirFromChar(sk.nextChar());
            sk.nextLine();
        }

        return new Decision(priorities, movements, attackDirs);
    }

    /**
     * Prompt user for initial attack patterns
     */
    public int[][][] getAttackPatterns(int numBots){
        int[][][] attackPatterns = new int[numBots][][];

        // Iterate over bots
        for(int botId = 0; botId < numBots; botId++){
            System.out.println("Configuring attack pattern for bot " + botId);
            // Ask for numRows
            System.out.println("How many rows (y-values) does the attack pattern span?");
            int numRows = sk.nextInt();
            sk.nextLine();
            while(numRows < 0 || numRows%2 == 0){
                System.out.println("Must have a positive, odd number of rows. Enter a new value:");
                numRows = sk.nextInt();
                sk.nextLine();
            }

            // Ask for columns
            System.out.println("How many columns (x-values) does the attack pattern span?");
            int numCols = sk.nextInt();
            sk.nextLine();
            while(numCols < 0 || numCols%2 == 0){
                System.out.println("Must have a positive, odd number of columns. Enter a new value:");
                numCols = sk.nextInt();
                sk.nextLine();
            }

            // Fill in attack pattern values
            attackPatterns[botId] = new int[numRows][numCols];
            System.out.println("Enter attack pattern with spaces between columns and newlines between rows like so:\n" +
                    "0 0 0\n" +
                    "0 0 0\n" +
                    "0 0 0");
            System.out.println("Your bot is in the center of the array.")
            for(int r = 0; r < numRows; r++){
                for(int c = 0; c < numCols; c++){
                    attackPatterns[botId][r][c] = sk.nextInt();
                }
                sk.nextLine();
            }

            // TODO: Transform attackPatterns matrix into correct coordinates
            // 1 2 3        3 6 9
            // 4 5 6    ->  2 5 8
            // 7 8 9        1 4 7
        }

        return attackPatterns;
    }

    /**
     * Helper method to transform from visual coordinates to game coordinates
     */
    public int[][] toGameCoords(int map[][]){
        return null;
    }

    /**
     * Helper method to convert a UDLRS character to a Direction
     */
    public Direction dirFromChar(char d){
        switch(d){
            case 'U':
                return Direction.UP;
                break;
            case 'D':
                return Direction.DOWN;
                break;
            case 'L':
                return Direction.LEFT;
                break;
            case 'R':
                return Direction.RIGHT;
                break;
            default:
                return Direction.STAY;
        }
    }
}
