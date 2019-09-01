package mech.mania;

import static mech.mania.UnitSetup.hasValidStartingConditions;

/**
 * Main class -- where the magic happens
 */
public class Main {
    public static void main(String[] args) {
        if (args.length < 6) {
            System.out.println("Invalid arguments. Expected [gameId] [mapDirectory] [player1Name] [player2Name] [player1URL (or 'HUMAN')] [player2URL (or 'HUMAN)]");
        }

        String gameID = args[0];
        String mapDirectory = args[1];
        String p1Name = args[2];
        String p2Name = args[3];
        String p1URL = args[4];
        String p2URL = args[5];


        PlayerCommunicator player1 = getPlayerForURL(p1URL, 1);
        PlayerCommunicator player2 = getPlayerForURL(p2URL, 2);

        Map map = new Map(mapDirectory, gameID);

        UnitSetup[] p1setup = player1.getUnitsSetup(map);
        UnitSetup[] p2setup = player2.getUnitsSetup(map);

        VisualizerOutputter visualizerOutput = new VisualizerOutputter();

        if (!hasValidStartingConditions(p1setup)) {
            if (!hasValidStartingConditions(p2setup)) {
                visualizerOutput.printWinnerJSON(Game.TIE);
            } else {
                visualizerOutput.printWinnerJSON(Game.P2_WINNER);
            }
            return;
        } else if (!hasValidStartingConditions(p2setup)) {
            visualizerOutput.printWinnerJSON(Game.P1_WINNER);
            return;
        }

        Game game = new Game(gameID, p1Name, p2Name, p1setup, p2setup, map);


        visualizerOutput.printInitialVisualizerJson(game);

        while (game.getWinner() == Game.NO_WINNER) {

            Decision p1Decision = null, p2Decision = null;
            try {
                p1Decision = player1.getDecision(game);
                p2Decision = player2.getDecision(game);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

            game.doTurn(p1Decision, p2Decision);

            visualizerOutput.printRoundVisualizerJson(game);
        }

        player1.sendGameOver(gameID);
        player2.sendGameOver(gameID);

        visualizerOutput.printWinnerJSON(game.getWinner());
    }

    private static PlayerCommunicator getPlayerForURL(String url, int playerNum) {
        if (url.equals("HUMAN")) {
            return new GUIPlayerCommunicator(playerNum);
        } else {
            return new ServerPlayerCommunicator(playerNum, url);
        }
    }
}
