package mech.mania;

public class VisualizerOutputter {
    public VisualizerOutputter() {}

    public void printInitialVisualizerJson(Game game) {
        System.out.println(game.getInitialVisualizerJson());
    }

    public void printRoundVisualizerJson(Game game) {
        System.out.println(game.getRoundVisualizerJson());
    }
}
