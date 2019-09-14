package mech.mania.visualizer.perTurn;

import java.util.ArrayList;
import java.util.List;

public class GameRepresentation {
    private List<TurnRepresentation> theTurns;

    public GameRepresentation() {
        theTurns = new ArrayList<>();
    }

    public void addTurn(TurnRepresentation aTurn) {
        theTurns.add(aTurn);
    }
}
