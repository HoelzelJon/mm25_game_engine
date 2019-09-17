package mech.mania.playerCommunication.gui;

import javafx.application.Application;
import javafx.application.Platform;
import mech.mania.*;
import mech.mania.playerCommunication.Decision;
import mech.mania.playerCommunication.InvalidDecisionException;
import mech.mania.playerCommunication.PlayerCommunicator;
import mech.mania.playerCommunication.UnitSetup;

import java.util.List;

import static mech.mania.Game.UNITS_PER_PLAYER;

/**
 * Container class that stores values from the GUI that will end up being used
 * for starting the game.
 */
public class GUIPlayerCommunicator extends PlayerCommunicator {
    // keep the JavaFX application running throughout
    private static GUIInitialUnitInput applicationInstance;


    public GUIPlayerCommunicator(int playerNum) {
        super(playerNum);
        // make sure that closing a JavaFX window does not exit the JavaFX thread
        Platform.setImplicitExit(false);
    }

    /**
     * Method that should be called when the game is over because we have no way
     * of properly exiting the JavaFX thread without the Main thread telling us
     * that the game is over.
     */
    static void onGameEnd() {
        if (applicationInstance == null) {
            return;
        }
        try {
            applicationInstance.stop();
            Platform.exit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendGameOver(String gameID) {
        onGameEnd();
    }

    @Override
    public UnitSetup[] getUnitsSetup(Board board) {
        UnitSetup[] allUnits = new UnitSetup[UNITS_PER_PLAYER];

        List<UninitializedUnit> nonSetupUnits = board.getInitialUnits(playerNum);

        // pass in argument of the player number to display in title
        if (applicationInstance == null) {
            new Thread(() -> Application.launch(GUIInitialUnitInput.class, "" + playerNum))
                    .start();
        } else {
            Platform.runLater(() -> applicationInstance.launchInitializationGui(playerNum, nonSetupUnits));
        }

        // wait until the GUI is finished and get the instance of the application
        applicationInstance = GUIInitialUnitInput.awaitAndGetInstance();
        int[][][] allAttackPatterns = applicationInstance.getAttackPatterns();
        boolean[][][] allTerrainPatterns = applicationInstance.getTerrainPatterns();
        int[] allHps = applicationInstance.getHps();
        int[] allSpeeds = applicationInstance.getSpeeds();

        if (allAttackPatterns == null || allHps == null || allSpeeds == null) {
            System.err.println("Unit(s) were not initialized correctly.");
            System.exit(0);
        }

        for (int i = 0; i < UNITS_PER_PLAYER; i++) {
            int[][] transformedBoard = transformBoard(allAttackPatterns[i]); //Board.toVisualCoords(allAttackPatterns[i]);
            allUnits[i] = new UnitSetup(transformedBoard, allTerrainPatterns[i], allHps[i], allSpeeds[i], nonSetupUnits.get(i).getUnitId());
        }

        return allUnits;
    }

    private static int[][] transformBoard(int[][] board) {
        int[][] transform = new int[board.length][board[0].length];
        for(int r = 0; r < transform.length; r++){
            for (int c = 0; c < transform[0].length; c++){
                transform[r][c] = board[r][board[0].length - c - 1];
            }
        }
        return transform;
    }

    @Override
    public Decision getDecision(Game gameState) throws InvalidDecisionException {

        // Print gameState and Unit's stats for user to see (copied from HumanPlayerCommunicator)
        System.out.println(gameState.getBoardString());
        System.out.println(gameState.getUnitStatsString());

        System.out.println("**********Player " + playerNum + "**********");

        Unit[] units = gameState.getPlayerUnits(playerNum);

        Platform.runLater(() -> applicationInstance.launchDecisionGui(playerNum, units));

        // re-get the instance (not necessary to set to the static variable again,
        // but it's the same name variable so why bother creating a new variable.
        applicationInstance = GUIInitialUnitInput.awaitAndGetInstance();
        int[] priorities = applicationInstance.getPriorities();
        Direction[][] movements = applicationInstance.getMovements();
        Direction[] attacks = applicationInstance.getAttacks();

        if (priorities == null || movements == null || attacks == null) {
            System.err.println("Decisions were not properly made.");
            System.exit(0);
        }

        return new Decision(priorities, movements, attacks);
    }
}