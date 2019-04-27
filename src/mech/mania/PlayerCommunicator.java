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

    public abstract UnitSetup[] getUnitsSetup(String gameID, Map map);

    public abstract Decision getDecision(Game gameState);
}
