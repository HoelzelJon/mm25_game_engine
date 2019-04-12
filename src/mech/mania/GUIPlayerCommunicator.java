package mech.mania;

import javafx.application.Application;
import javafx.application.Platform;

import java.util.Arrays;

/**
 * Container class that stores values from the GUI that will end up being used
 * for starting the game.
 */
public class GUIPlayerCommunicator extends PlayerCommunicator {

    private static final int MAX_POINTS = 14;
    private static final int NUM_UNITS = 3;
    // keep the JavaFX application running throughout
    private static GUIInitialBotInput applicationInstance;

    public GUIPlayerCommunicator(int playerNum) {
        super(playerNum);
        Platform.setImplicitExit(false);
    }

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
            new Thread(() -> Application.launch(GUIInitialBotInput.class, "" + playerNum))
                    .start();
        } else {
            Platform.runLater(() -> applicationInstance.launchInitializationGui(playerNum));
        }

        applicationInstance = GUIInitialBotInput.awaitAndGetInstance();
        int[][][] allPatterns = applicationInstance.getAttackPatterns();
        int[] allHps = applicationInstance.getHps();
        int[] allSpeeds = applicationInstance.getSpeeds();

        if (allPatterns == null || allHps == null || allSpeeds == null) {
            System.err.println("Bot(s) were not initialized correctly.");
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

        // don't show in GUI if the bot is dead
        Unit[] units = gameState.getPlayerUnits(playerNum);

        Platform.runLater(() -> applicationInstance.launchDecisionGui(playerNum, units));

        // re-get the instance (not necessary to set to the static variable again,
        // but it's the same name variable so why bother creating a new variable.
        applicationInstance = GUIInitialBotInput.awaitAndGetInstance();
        int[] priorities = applicationInstance.getPriorities();
        Direction[][] movements = applicationInstance.getMovements();
        Direction[] attacks = applicationInstance.getAttacks();

        if (priorities == null || movements == null || attacks == null) {
            System.err.println("Decisions were not properly made.");
            System.exit(0);
        }

        return new Decision(priorities, movements, attacks);
    }


    private static String errorMessage = "";

    public static String getErrorMessage() {
        return errorMessage;
    }

    // validate for EACH bot
    public static boolean hasValidStartingConditions(int[][] setAttackPatterns,
                                                     int setHp,
                                                     int setSpeed) {
        if (setHp < 4 || setSpeed < 5) {
            errorMessage = "hp and speed must be >= 4 and >= 5, respectively";
            return false;
        }

        int sum = 0;
        for (int[] row : setAttackPatterns) {
            for (int col : row) {
                if (col > 1) {
                    sum += 2 * (col - 1);
                }
                sum += col;
            }
        }
        if (sum > MAX_POINTS) {
            errorMessage = "too many points allotted in grid";
            return false;
        } else if (setHp - 4 + setSpeed - 4 + sum > MAX_POINTS) {
            errorMessage = "too many extra points in hp and speed";
            return false;
        }

        return true;
    }

    public static boolean hasValidDecision(int[] priorities,
                                           Direction[][] movements,
                                           Direction[] attacks) {
        for (int priority : priorities) {
            if (priority != 1 && priority != 2 && priority != 3) {
                errorMessage = "priorities must be 1, 2, or 3";
                return false;
            }
        }

        for (int i = 0; i < priorities.length - 1; i++) {
            if (priorities[i] == priorities[i + 1]) {
                errorMessage = "there may not be any duplicate priorities";
                return false;
            }
        }

        // attacks and movements do not have to be validated since they are
        // already Direction objects
        return true;
    }
}