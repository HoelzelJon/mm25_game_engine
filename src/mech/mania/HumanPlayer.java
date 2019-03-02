package mech.mania;

/**
 * Created by prith on 3/2/2019.
 */
public class HumanPlayer extends Player {
    public HumanPlayer(){
        // Do nothing
    }

    /**
     * Set game state and print it in human-readable format
     * @param state the state to store and print
     */
    public void setGameState(Game state){
        super.setGameState();

        // Print out map and stats
        System.out.println(this.gameState.getMapString());
        System.out.println(this.gameState.getUnitStatsString());
    }

    /**
     * Prompt user for priorities, movements, and attacks
     */
    private void setDecision(){
        //TODO: build json string from user input
        decisionJson = "":
        super.setDecision(decisionJson);
    }
}
