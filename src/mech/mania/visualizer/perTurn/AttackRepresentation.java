package mech.mania.visualizer.perTurn;

import java.util.ArrayList;
import java.util.List;

public class AttackRepresentation {
    private int botId;
    private List<TerrainStatusRepresentation> createdWalls;
    private List<TerrainStatusRepresentation> damagedWalls;
    private List<UnitStatusRepresentation> damagedUnits;

    public AttackRepresentation(int aBotId) {
        botId = aBotId;
        createdWalls = new ArrayList<>();
        damagedWalls = new ArrayList<>();
        damagedUnits = new ArrayList<>();
    }

    public void addCreatedTerrain(TerrainStatusRepresentation aTerrainStatus) {
        createdWalls.add(aTerrainStatus);
    }

    public void addDamagedTerrain(TerrainStatusRepresentation aTerrainStatus) {
        damagedWalls.add(aTerrainStatus);
    }

    public void addDamagedUnit(UnitStatusRepresentation aUnitStatus) {
        damagedUnits.add(aUnitStatus);
    }
}
