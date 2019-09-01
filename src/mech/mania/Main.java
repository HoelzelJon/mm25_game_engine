package mech.mania;

import static mech.mania.UnitSetup.hasValidStartingConditions;

/**
 * Main class -- where the magic happens
 */
public class Main {
    private static String URL_FOR_HUMAN_PLAYER = "HUMAN";

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
        boolean hasHumanPlayer = p1URL.equals(URL_FOR_HUMAN_PLAYER) || p2URL.equals(URL_FOR_HUMAN_PLAYER);

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
            boolean p1MadeValidDecision = true;
            boolean p2MadeValidDecision = true;
            try {
                try {
                    p1Decision = player1.getDecision(game);
                } catch (InvalidDecisionException e) {
                    p1MadeValidDecision = false;
                }

                try {
                    p2Decision = player2.getDecision(game);
                } catch (InvalidDecisionException e) {
                    p2MadeValidDecision = false;
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                System.exit(1);
            }

            if (!p1MadeValidDecision && !p2MadeValidDecision) {
                visualizerOutput.printWinnerJSON(Game.TIE);
                return;
            } else if (!p1MadeValidDecision) {
                visualizerOutput.printWinnerJSON(Game.P2_WINNER);
                return;
            } else if (!p2MadeValidDecision) {
                visualizerOutput.printWinnerJSON(Game.P1_WINNER);
                return;
            }

            game.doTurn(p1Decision, p2Decision);

            visualizerOutput.printRoundVisualizerJson(game);
        }

        player1.sendGameOver(gameID);
        player2.sendGameOver(gameID);

        visualizerOutput.printWinnerJSON(game.getWinner());
    }

    private static PlayerCommunicator getPlayerForURL(String url, int playerNum) {
        if (url.equals(URL_FOR_HUMAN_PLAYER)) {
            return new GUIPlayerCommunicator(playerNum);
        } else {
            return new ServerPlayerCommunicator(playerNum, url);
        }
    }
}
