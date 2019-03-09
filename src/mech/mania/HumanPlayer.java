package mech.mania;
import java.util.Scanner;

/**
 * Created by prith on 3/2/2019.
 */
public class HumanPlayer extends Player {
    private Scanner sk;
    private Unit[] myUnits;
    public HumanPlayer(playerNum){
        super(playerNum);
        sk = new Scanner(System.in);
        // Do nothing
    }

    /**
     * Set game state and print it in human-readable format
     * @param state the state to store and print
     */
    public void setGameState(Game gameState){
        super.setGameState(gameState);
        myUnits = gameState.getPlayerUnits(playerNum);

        // Print out map and stats
        System.out.println(this.gameState.getMapString());
        System.out.println(this.gameState.getUnitStatsString());
    }

    /**
     * Prompt user for priorities, movements, and attacks
     */
    private void setDecision(){
        //TODO: build Decision object from user input and pass it as a Json

        // Variables to fill
        int[] priorities = new int[myUnits.length];
        Direction[][] movements = new Direction[myUnits.length][] // movements is a jagged array since speeds could be different
        Direction[] attacks = new Direction[myUnits.length];

        // Iterate through bots
        for(int botId = 0; botId < myUnits; botId++){
            // Ask for priority
            System.out.println("Designate priority for bot " + botId + " (1 to " + myUnits.length + ", 1 is first):");
            priorities[botId] = sk.nextInt();
            sk.nextLine();
            while(p < 1 || p > myUnits.length){
                System.out.println("Priority must be from 1 to 3. Entery priority for bot " + botId + ":");
                priorities[botId] = sk.nextInt();
                sk.nextLine();
            }

            // Ask for movement
            movements[botId] = new Direction[myUnits[botId].getSpeed()];
            System.out.println("Specify movement steps for bot " + botId + ". U = up, D = down, L = left, R = right");
            for(int step = 0; step < movements[botId].length; step++){

            }

        }

        Decision decision = new Decision(priorities, movements, attacks);

        Gson gson = new Gson();
        decisionJson = gson.toJson(decision);
        super.setDecision(decisionJson);
    }
}
