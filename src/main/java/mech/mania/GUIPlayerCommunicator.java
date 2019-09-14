package mech.mania;

import javafx.application.Application;
import javafx.application.Platform;

/**
 * Container class that stores values from the GUI that will end up being used
 * for starting the game.
 */
public class GUIPlayerCommunicator extends PlayerCommunicator {

    private static final int NUM_UNITS = 3;

    // keep the JavaFX application running throughout
    private static GUIInitialUnitInput applicationInstance;


    GUIPlayerCommunicator(int playerNum) {
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
    public UnitSetup[] getUnitsSetup(Board map) {
        UnitSetup[] allUnits = new UnitSetup[NUM_UNITS];

        // pass in argument of the player number to display in title
        if (applicationInstance == null) {
            new Thread(() -> Application.launch(GUIInitialUnitInput.class, "" + playerNum))
                    .start();
        } else {
            Platform.runLater(() -> applicationInstance.launchInitializationGui(playerNum));
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

        for (int i = 0; i < NUM_UNITS; i++) {
            int[][] transformedMap = transformMap(allAttackPatterns[i]); //Map.toVisualCoords(allAttackPatterns[i]);
            allUnits[i] = new UnitSetup(transformedMap, allTerrainPatterns[i], allHps[i], allSpeeds[i]);
        }

        return allUnits;
    }

    private static int[][] transformMap(int[][] map) {
        int[][] transform = new int[map.length][map[0].length];
        for(int r = 0; r < transform.length; r++){
            for (int c = 0; c < transform[0].length; c++){
                transform[r][c] = map[r][map[0].length - c - 1];
            }
        }
        return transform;
    }

    @Override
    public Decision getDecision(Game gameState) throws InvalidDecisionException {

        // Print gameState and Unit's stats for user to see (copied from HumanPlayerCommunicator)
        System.out.println(gameState.getMapString());
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