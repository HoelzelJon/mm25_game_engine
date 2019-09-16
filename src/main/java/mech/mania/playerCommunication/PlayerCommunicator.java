package mech.mania.playerCommunication;

import mech.mania.Board;
import mech.mania.Game;

/**
 * This class will handle communication to and from the player script.
 */
public abstract class PlayerCommunicator {
    protected int playerNum;

    public PlayerCommunicator(int playerNum){
        this.playerNum = playerNum;
    }

    public abstract UnitSetup[] getUnitsSetup(Board map);

    public abstract Decision getDecision(Game gameState) throws InvalidDecisionException;

    public void sendGameOver(String gameID) {
        // do nothing by default
    }
}
