package mech.mania.visualizer.perTurn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TurnRepresentation {
    private int turn;
    private List<RoundRepresentation> rounds;
    private List<TerrainStatusRepresentation> outerWallsAdded;
    private List<Integer> unitsKilledByOuterWall;

    public TurnRepresentation(int aTurn) {
        turn = aTurn;
        rounds = new ArrayList<>();
        outerWallsAdded = new ArrayList<>();
        unitsKilledByOuterWall = new ArrayList<>();
    }

    public void addRound(RoundRepresentation aRound) {
        rounds.add(aRound);
    }

    public void addOuterWall(TerrainStatusRepresentation newWall) {
        outerWallsAdded.add(newWall);
    }

    public void addUnitKilledByOuterWalls(int unitId) {
        unitsKilledByOuterWall.add(unitId);
    }

    public List<Integer> getUnitsKilledByOuterWall() {
        return unitsKilledByOuterWall;
    }
}
