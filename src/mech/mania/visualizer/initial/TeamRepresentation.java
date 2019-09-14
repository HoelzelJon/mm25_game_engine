package mech.mania.visualizer.initial;

import mech.mania.Unit;

import java.util.List;
import java.util.stream.Collectors;

public class TeamRepresentation {
    String teamName;
    private List<BotRepresentation> botInfo;

    public TeamRepresentation(List<Unit> units, String name) {
        teamName = name;
        botInfo = units.stream().map(BotRepresentation::new).collect(Collectors.toList());
    }
}
