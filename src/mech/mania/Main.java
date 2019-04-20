package mech.mania;

/**
 * Main class -- where the magic happens
 */
public class Main {
    public static void main(String[] args) {
        String gameID = args[0];
        String mapDirectory = args[1];
        String p1Name = args[2];
        String p2Name = args[3];
        String p1URL = args[4];
        String p2URL = args[5];

        Map map = new Map(mapDirectory);

        PlayerCommunicator player1 = new GUIPlayerCommunicator(1); //new HumanPlayerCommunicator(1); //ServerPlayerCommunicator(1, p1URL);
        PlayerCommunicator player2 = new GUIPlayerCommunicator(2); //new HumanPlayerCommunicator(2); //ServerPlayerCommunicator(2, p2URL);

        UnitSetup[] p1Units = player1.getUnitsSetup();
        UnitSetup[] p2Units = player2.getUnitsSetup();

        Game game = new Game(map, gameID, p1Units, p2Units);

        printInitialState(game);

        while (game.getWinner() == Game.NO_WINNER) {

            Decision p1Decision = null, p2Decision = null;
            try {
                p1Decision = player1.getDecision(game);
                p2Decision = player2.getDecision(game);
            } catch (Exception e) {
                GUIPlayerCommunicator.onGameEnd();
            }

            game.doTurn(p1Decision, p2Decision);

            printGameMap(game);
            printVisualizerJson(game);

            try {
                Thread.sleep(1000);
            } catch (Exception ex) {}
        }

        if (game.getWinner() == Game.TIE) {
            System.out.println("It's a tie!");
        } else if (game.getWinner() == Game.P1_WINNER) {
            System.out.println("Player 1 wins!");
        } else if (game.getWinner() == Game.P2_WINNER) {
            System.out.println("Player 2 wins!");
        }

        GUIPlayerCommunicator.onGameEnd();
    }

    static void printGameMap(Game game) {
        System.out.println(game.getMapString() + "\n");
    }

    static void printInitialState(Game game) {
        System.out.println(game.getInitialVisualizerJson() + "\n");
    }

    static void printVisualizerJson(Game game) {
        System.out.println(game.getRoundVisualizerJson() + "\n");
    }
}
