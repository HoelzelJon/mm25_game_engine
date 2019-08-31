package mech.mania;

public class VisualizerOutputter {
    public VisualizerOutputter() {}

    public void printInitialVisualizerJson(Game game) {
        System.out.println(game.getInitialVisualizerJson());
    }

    public void printRoundVisualizerJson(Game game) {
        System.out.println(game.getRoundVisualizerJson());
    }

    public void printWinnerJSON(int winner) {
        if (winner == Game.TIE) {
            System.out.println("{\"Winner\": 0}");
        } else if (winner == Game.P1_WINNER) {
            System.out.println("{\"Winner\": 1}");
        } else if (winner == Game.P2_WINNER) {
            System.out.println("{\"Winner\": 2}");
        }
    }
}
