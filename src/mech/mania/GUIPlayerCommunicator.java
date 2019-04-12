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
    public static void onGameEnd() {
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
    public UnitSetup[] getUnitsSetup() {
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
        int[][][] allPatterns = applicationInstance.getAttackPatterns();
        int[] allHps = applicationInstance.getHps();
        int[] allSpeeds = applicationInstance.getSpeeds();

        if (allPatterns == null || allHps == null || allSpeeds == null) {
            System.err.println("Unit(s) were not initialized correctly.");
            System.exit(0);
        }

        for (int i = 0; i < NUM_UNITS; i++) {
            int[][] transformedMap = transformMap(allPatterns[i]);
            allUnits[i] = new UnitSetup(transformedMap, allHps[i], allSpeeds[i]);
        }

        return allUnits;
    }

    // TODO: fix this function so attacking in a direction works
    private int[][] transformMap(int[][] map) {
        // for this method of input, the map needs to be transformed as follows:
        // 1 2 3       1 4 7
        // 4 5 6  -->  2 5 8
        // 7 8 9       3 6 9
        // or switch across x-y axis
        int[][] newMap = new int[map.length][map[0].length];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                newMap[i][j] = map[map.length - 1 - j][i];
            }
        }
        return newMap;
    }

    @Override
    public Decision getDecision(Game gameState) {

        // Print gameState and Unit's stats for user to see (copied from HumanPlayerCommunicator)
        System.out.println(gameState.getMapString());
        System.out.println(gameState.getUnitStatsString());

        System.out.println("**********Player " + playerNum + "**********");

        // don't show in GUI if the Unit is dead
        Unit[] units = gameState.getPlayerUnits(playerNum);

        Platform.runLater(() -> applicationInstance.launchDecisionGui(playerNum, units));

        // re-get the instance (not necessary to set to the static variable again,
        // but it's the same name variable so why Unither creating a new variable.
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