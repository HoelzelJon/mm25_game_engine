package mech.mania;

/**
 * This class will handle communication to and from the player script.
 */
public abstract class PlayerCommunicator {
    int playerNum;

    PlayerCommunicator(int playerNum){
        this.playerNum = playerNum;
        // Do nothing
    }

    public abstract UnitSetup[] getUnitsSetup(Map map);

    public abstract Decision getDecision(Game gameState) throws InvalidDecisionException;

    public void sendGameOver(String gameID) {};
}
