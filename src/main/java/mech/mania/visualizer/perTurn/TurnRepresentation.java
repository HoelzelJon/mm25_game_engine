package mech.mania.visualizer.perTurn;

import java.util.ArrayList;
import java.util.List;

public class TurnRepresentation {
    private int turn;
    private List<RoundRepresentation> rounds;

    public TurnRepresentation(int aTurn) {
        turn = aTurn;
        rounds = new ArrayList<>();
    }

    public void addRound(RoundRepresentation aRound) {
        rounds.add(aRound);
    }
}
