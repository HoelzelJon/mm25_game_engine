package mech.mania.playerCommunication;

import mech.mania.Board;
import mech.mania.Game;

import java.util.List;

/**
 * This class will handle communication to and from the player script.
 */
public abstract class PlayerCommunicator {
    protected int playerNum;

    public PlayerCommunicator(int playerNum){
        this.playerNum = playerNum;
    }

    public abstract List<UnitSetup> getUnitsSetup(Board board) throws InvalidSetupException;

    public abstract List<UnitDecision> getDecision(Game gameState) throws InvalidDecisionException;

    public void sendGameOver(String gameID, int winner) {
        // do nothing by default
    }
}
