package mech.mania.visualizer.perTurn;

import java.util.ArrayList;
import java.util.List;

public class RoundRepresentation {
    private int round;
    private List<List<MovementRepresentation>> movements;
    private List<AttackRepresentation> attacks;

    public RoundRepresentation(int aRound) {
        round = aRound;
        movements = new ArrayList<>();
        attacks = new ArrayList<>();
    }

    public void addMovementStep(List<MovementRepresentation> aMovement) {
        movements.add(aMovement);
    }

    public void addAttacks(List<AttackRepresentation> anAttacks) {
        attacks.addAll(anAttacks);
    }
}
