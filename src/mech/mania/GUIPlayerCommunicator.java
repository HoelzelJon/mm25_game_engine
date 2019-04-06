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

    public GUIPlayerCommunicator(int playerNum) {
        super(playerNum);
    }

    @Override
    public int[][][] getAttackPatterns() {
        Application.launch(GUIInitialBotInput.class);
        Platform.setImplicitExit(true);

        System.out.println(Arrays.deepToString(GUIInitialBotInput.getAttackPatterns()));

        return GUIInitialBotInput.getAttackPatterns();
    }

    @Override
    public Decision getDecision(Game gameState) {
        return null;
    }


    public static String errorMessage = "";

    // validate for EACH bot
    public static boolean hasValidConditions(int[][] setAttackPatterns,
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
}
