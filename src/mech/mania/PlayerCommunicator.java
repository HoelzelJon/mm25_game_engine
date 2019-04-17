package mech.mania;

import java.util.ArrayList;

/**
 * This class will handle communication to and from the player script.
 */
public abstract class PlayerCommunicator {
    protected int playerNum;

    public PlayerCommunicator(int playerNum){
        this.playerNum = playerNum;
        // Do nothing
    }

    public abstract UnitSetup[] getUnitsSetup(String gameID, Map map);

    public abstract Decision getDecision(Game gameState);
}
