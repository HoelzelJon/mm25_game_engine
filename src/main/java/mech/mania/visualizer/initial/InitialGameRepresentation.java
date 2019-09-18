package mech.mania.visualizer.initial;

import mech.mania.Game;

import java.util.Arrays;
import java.util.List;

public class InitialGameRepresentation {
    private List<TeamRepresentation> teams;

    public InitialGameRepresentation(Game aGame) {
        teams = Arrays.asList(
                new TeamRepresentation(aGame.getPlayerUnits(1), aGame.getPlayerName(1)),
                new TeamRepresentation(aGame.getPlayerUnits(2), aGame.getPlayerName(2)));
    }
}
