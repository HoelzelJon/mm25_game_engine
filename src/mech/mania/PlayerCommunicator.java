package mech.mania;

/**
 * This class will handle communication to and from the player script.
 */
public abstract class PlayerCommunicator {
    protected int playerNum;
    protected Map map;

    public PlayerCommunicator(int playerNum, Map map){
        this.playerNum = playerNum;
        this.map = map;
        // Do nothing
    }

    public abstract int[][][] getAttackPatterns();

    public abstract Decision getDecision(Game gameState);
}
