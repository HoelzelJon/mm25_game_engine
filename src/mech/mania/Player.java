package mech.mania;

import com.google.gson.Gson;


/**
 * This class will handle communication to and from the player script.
 */
public class Player {
    protected Game gameState;
    protected Decision decision;
    public Player(){
        // Do nothing
    }

    public void setGameState(Game gameObject){
        this.gameState = gameObject;
    }

    public Decision getDecision(){
        return decision;
    }

    private void setDecision(String decisionJson){
        Gson gson = new Gson();
        decision = gson.fromJson(decisionJson, Decision.class);
    }
}
