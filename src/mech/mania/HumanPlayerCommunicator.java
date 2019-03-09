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
        Direction[] attacks = new Direction[myUnits.length];

        // Iterate through bots
        for(int botId = 0; botId < myUnits.length; botId++){
            // Ask for priority
            System.out.println("Designate priority for bot " + botId + " (1 to " + myUnits.length + ", 1 is first):");
            priorities[botId] = sk.nextInt();
            sk.nextLine();
            while(priorities[botId] < 1 || priorities[botId] > myUnits.length){
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

        return new Decision(priorities, movements, attacks);
    }
}
