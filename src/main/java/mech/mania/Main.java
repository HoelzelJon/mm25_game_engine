package mech.mania;

import mech.mania.playerCommunication.*;
import mech.mania.playerCommunication.gui.GUIPlayerCommunicator;
import mech.mania.playerCommunication.server.ServerPlayerCommunicator;
import mech.mania.visualizer.initial.InitialGameRepresentation;
import mech.mania.visualizer.VisualizerOutputter;
import mech.mania.visualizer.perTurn.TurnRepresentation;

import java.io.IOException;
import java.util.List;

import static mech.mania.Winner.*;
import static mech.mania.playerCommunication.UnitDecision.throwExceptionOnInvalidDecisionList;
import static mech.mania.playerCommunication.UnitSetup.throwExceptionOnInvalidSetupList;

/**
 * Main class -- where the magic happens
 */
public class Main {
    private static final String URL_FOR_HUMAN_PLAYER = "HUMAN";
    private static final String OUTPUT_FILE_FOR_STDOUT = "STDOUT";

    public static void main(String[] args) {
        if (args.length < 6) {
            System.err.println("Invalid arguments. Expected [gameId] [boardDirectory] [player1Name] [player2Name] [player1URL (or 'HUMAN')] [player2URL (or 'HUMAN')] [outputFile or 'STDOUT']");
        }

        String gameID = args[0];
        String boardDirectory = args[1];
        String p1Name = args[2];
        String p2Name = args[3];
        String p1URL = args[4];
        String p2URL = args[5];
        String outputFile = args[6];

        String argumentsString = String.format("gameID: %s, boardDirectory: %s, p1Name: %s, p2Name: %s, p1URL: %s, p2URL: %s, outputFile: %s",
                gameID, boardDirectory, p1Name, p2Name, p1URL, p2URL, outputFile);
        System.err.println(argumentsString);

        PlayerCommunicator player1 = getPlayerForURL(p1URL, 1);
        PlayerCommunicator player2 = getPlayerForURL(p2URL, 2);

        Board board;
        try {
            board = new Board(boardDirectory, gameID);
        } catch (IOException ex) {
            System.err.println("IOException encountered when initializing board");
            ex.printStackTrace();
            System.exit(1);
            return;
        }

        VisualizerOutputter visualizerOutput;
        if (outputFile.equals(OUTPUT_FILE_FOR_STDOUT)) {
            visualizerOutput = new VisualizerOutputter();
        } else{
            visualizerOutput = new VisualizerOutputter(outputFile);
        }

        boolean p1SetupValid;
        boolean p2SetupValid;
        List<UnitSetup> p1Setup = null;
        List<UnitSetup> p2Setup = null;
        try {
            p1Setup = player1.getUnitsSetup(board);
            throwExceptionOnInvalidSetupList(p1Setup, board.getUnitIds(1));
            p1SetupValid = true;
        } catch (InvalidSetupException ex) {
            p1SetupValid = false;
            System.err.println("Player 1 made invalid setup: " + ex.getMessage());
        }

        try {
            p2Setup = player2.getUnitsSetup(board);
            throwExceptionOnInvalidSetupList(p2Setup, board.getUnitIds(2));
            p2SetupValid = true;
        } catch (InvalidSetupException ex) {
            p2SetupValid = false;
            System.err.println("Player 2 made invalid setup: " + ex.getMessage());
        }

        try {
            if (printWinnerIfInvalid(p1SetupValid, p2SetupValid, player1, player2, gameID, visualizerOutput)) {
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Game game = new Game(gameID, p1Name, p2Name, p1Setup, p2Setup, board);

        // Print initial visualizer Json
        try {
            visualizerOutput.printInitialVisualizerJson(new InitialGameRepresentation(game));
        } catch (IOException e){
            e.printStackTrace();
            return;
        }

        while (game.getWinner() == NO_WINNER) {

            List<UnitDecision> p1Decision = null, p2Decision = null;
            boolean p1MadeValidDecision;
            boolean p2MadeValidDecision;
            try {
                p1Decision = player1.getDecision(game);
                throwExceptionOnInvalidDecisionList(p1Decision, game.getPlayerUnits(1));
                p1MadeValidDecision = true;
            } catch (InvalidDecisionException e) {
                p1MadeValidDecision = false;
                System.err.println("Error in player 1 decision: " + e.getMessage());
            }

            try {
                p2Decision = player2.getDecision(game);
                throwExceptionOnInvalidDecisionList(p2Decision, game.getPlayerUnits(2));
                p2MadeValidDecision = true;
            } catch (InvalidDecisionException e) {
                p2MadeValidDecision = false;
                System.err.println("Error in player 2 decision: " + e.getMessage());
            }

            try {
                if (printWinnerIfInvalid(p1MadeValidDecision, p2MadeValidDecision, player1, player2, game.getGameId(), visualizerOutput)) {
                    System.exit(0);
                }
            } catch (IOException e){
                e.printStackTrace();
                System.exit(1);
            }

            // Print visualizer Json for this round
            TurnRepresentation turn = game.doTurn(p1Decision, p2Decision);
            try {
                visualizerOutput.printSingleTurnVisualizerJson(turn);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        int winner = game.getWinner() == P1_WINNER ? 1 : game.getWinner() == P2_WINNER ? 2 : 0;
        player1.sendGameOver(gameID, winner == 0 ? "TIE" : winner == 1 ? "WIN" : "LOSE");
        player2.sendGameOver(gameID, winner == 0 ? "TIE" : winner == 2 ? "WIN" : "LOSE");

        try {
            visualizerOutput.printWinnerJSON(game.getWinner());
            visualizerOutput.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * @return true iff one of the two inputs is false
     */
    private static boolean printWinnerIfInvalid(boolean p1Valid, boolean p2Valid, PlayerCommunicator player1, PlayerCommunicator player2, String gameId, VisualizerOutputter outputter) throws IOException {
        if (!p1Valid && !p2Valid) {
            outputter.printWinnerJSON(TIE);
            player1.sendGameOver(gameId, "TIE");
            player2.sendGameOver(gameId, "TIE");
            return true;
        } else if (!p1Valid) {
            outputter.printWinnerJSON(P2_WINNER);
            player1.sendGameOver(gameId, "LOSE");
            player2.sendGameOver(gameId, "WIN");
            return true;
        } else if (!p2Valid) {
            outputter.printWinnerJSON(P1_WINNER);
            player1.sendGameOver(gameId, "WIN");
            player2.sendGameOver(gameId, "LOSE");
            return true;
        }

        return false;
    }

    private static PlayerCommunicator getPlayerForURL(String url, int playerNum) {
        if (url.equals(URL_FOR_HUMAN_PLAYER)) {
            return new GUIPlayerCommunicator(playerNum);
        } else {
            return new ServerPlayerCommunicator(playerNum, url);
        }
    }
}
