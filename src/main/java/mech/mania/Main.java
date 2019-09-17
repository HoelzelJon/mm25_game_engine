package mech.mania;

import mech.mania.playerCommunication.*;
import mech.mania.playerCommunication.gui.GUIPlayerCommunicator;
import mech.mania.visualizer.initial.InitialGameRepresentation;
import mech.mania.visualizer.VisualizerOutputter;
import mech.mania.visualizer.perTurn.TurnRepresentation;

import java.io.IOException;

import static mech.mania.Winner.*;
import static mech.mania.playerCommunication.UnitSetup.hasValidStartingConditions;

/**
 * Main class -- where the magic happens
 */
public class Main {
    private static final String URL_FOR_HUMAN_PLAYER = "HUMAN";
    private static final String OUTPUT_FILE_FOR_STDOUT = "STDOUT";

    public static void main(String[] args) {
        if (args.length < 6) {
            System.out.println("Invalid arguments. Expected [gameId] [boardDirectory] [player1Name] [player2Name] [player1URL (or 'HUMAN')] [player2URL (or 'HUMAN')] [outputFile or 'STDOUT']");
        }

        String gameID = args[0];
        String boardDirectory = args[1];
        String p1Name = args[2];
        String p2Name = args[3];
        String p1URL = args[4];
        String p2URL = args[5];
        String outputFile = args[6];

        PlayerCommunicator player1 = getPlayerForURL(p1URL, 1);
        PlayerCommunicator player2 = getPlayerForURL(p2URL, 2);

        Board board = new Board(boardDirectory, gameID);

        UnitSetup[] p1setup = player1.getUnitsSetup(board);
        UnitSetup[] p2setup = player2.getUnitsSetup(board);

        VisualizerOutputter visualizerOutput;
        if (outputFile.equals(OUTPUT_FILE_FOR_STDOUT)) {
            visualizerOutput = new VisualizerOutputter();
        } else{
            visualizerOutput = new VisualizerOutputter(outputFile);
        }

        try {
            if (!hasValidStartingConditions(p1setup)) {
                if (!hasValidStartingConditions(p2setup)) {
                    visualizerOutput.printWinnerJSON(TIE);
                } else {
                    visualizerOutput.printWinnerJSON(P2_WINNER);
                }
                return;
            } else if (!hasValidStartingConditions(p2setup)) {
                visualizerOutput.printWinnerJSON(P1_WINNER);
                return;
            }
        } catch (IOException e){
            System.err.println(e.getMessage());
            return;
        }

        Game game = new Game(gameID, p1Name, p2Name, p1setup, p2setup, board);

        // Print initial visualizer Json
        try {
            visualizerOutput.printInitialVisualizerJson(new InitialGameRepresentation(game));
        } catch (IOException e){
            System.err.println(e.getMessage());
            return;
        }

        while (game.getWinner() == NO_WINNER) {

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
            try {
                if (!p1MadeValidDecision && !p2MadeValidDecision) {
                    visualizerOutput.printWinnerJSON(TIE);
                    return;
                } else if (!p1MadeValidDecision) {
                    visualizerOutput.printWinnerJSON(P2_WINNER);
                    return;
                } else if (!p2MadeValidDecision) {
                    visualizerOutput.printWinnerJSON(P1_WINNER);
                    return;
                }
            } catch (IOException e){
                System.err.println(e.getMessage());
                return;
            }

            // Print visualizer Json for this round
            TurnRepresentation turn = game.doTurn(p1Decision, p2Decision);
            try {
                visualizerOutput.printSingleTurnVisualizerJson(turn);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return;
            }
        }



        player1.sendGameOver(gameID);
        player2.sendGameOver(gameID);

        try {
            visualizerOutput.printWinnerJSON(game.getWinner());
            visualizerOutput.sendGameOver();
        } catch (IOException e){
            System.err.println(e.getMessage());
            return;
        }
    }

    private static PlayerCommunicator getPlayerForURL(String url, int playerNum) {
        if (url.equals(URL_FOR_HUMAN_PLAYER)) {
            return new GUIPlayerCommunicator(playerNum);
        } else {
            return new ServerPlayerCommunicator(playerNum, url);
        }
    }
}
