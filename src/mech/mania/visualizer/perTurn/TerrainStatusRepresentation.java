package mech.mania.visualizer.perTurn;

import mech.mania.Position;

import java.util.Arrays;
import java.util.List;

public class TerrainStatusRepresentation {
    private List<Integer> coordinates; // length-2 list representing (x,y) position of this terrain
    private int wallHP;

    public TerrainStatusRepresentation(Position aPos, int aTerrainHP) {
        coordinates = Arrays.asList(aPos.x, aPos.y);
        wallHP = aTerrainHP;
    }
}
