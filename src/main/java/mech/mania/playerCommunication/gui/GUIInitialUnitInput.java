package mech.mania.playerCommunication.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mech.mania.*;
import mech.mania.playerCommunication.InvalidDecisionException;
import mech.mania.playerCommunication.InvalidSetupException;
import mech.mania.playerCommunication.UnitDecision;
import mech.mania.playerCommunication.UnitSetup;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import static mech.mania.Game.UNITS_PER_PLAYER;
import static mech.mania.playerCommunication.UnitDecision.throwExceptionOnInvalidDecisionList;
import static mech.mania.playerCommunication.UnitSetup.ATTACK_PATTERN_SIZE;

public class GUIInitialUnitInput extends Application {

    private int[][][] attackPatterns;
    private boolean[][][] terrainPatterns;
    private int[] hps;
    private int[] speeds;
    private List<UnitDecision> decisions;
    private List<UninitializedUnit> nonSetupUnits;

    private int playerNum = -1;
    private static final int DEFAULT_SCENE_WIDTH = 600;
    private static final int DEFAULT_SCENE_HEIGHT = 550;

    private static GUIInitialUnitInput instance;
    private static CountDownLatch latch;

    // ---------------------- STUFF TO GET JAVAFX TO COOPERATE ----------------------

    public GUIInitialUnitInput() {
        instance = this;
    }

    static GUIInitialUnitInput awaitAndGetInstance() {
        latch = new CountDownLatch(1);
        try {
            // Will await until the player hits the submit button in one of the
            // GUIs and has a valid input. This way we can control when the
            // program should continue.
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return instance;
    }

    // ---------------------- INITIALIZATION GUI ----------------------

    /**
     * Alias function for start, since getting initialization values for
     * units happens at the start of the Application launching, while Decision
     * GUI showing will not occur until after Application has started (and don't
     * have to deal with any issues of Application not being launched already)
     */
    void launchInitializationGui(int setPlayerNum, List<UninitializedUnit> setNonSetupUnits) {
        playerNum = setPlayerNum;
        nonSetupUnits = setNonSetupUnits;
        try {
            start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A GUI for easier player input. Contains input for HP, speed, and attack
     * pattern.
     *
     * Get values set by this function through the getAll___() methods
     *
     * Note: call GUIInitialUnitInput.launch() from other code to create this
     * window.
     */
    @Override
    public void start(Stage primaryStage) {
        if (playerNum == -1) {
            // hasn't been initialized yet
            List<String> parameters = getParameters().getRaw();
            playerNum = Integer.parseInt(parameters.get(0));
        }

        primaryStage.setTitle("Player " + playerNum + " : Mechmania 25 Unit Initialization");

        InitializationInputVBox[] unitInputs = new InitializationInputVBox[3];
        for (int i = 0; i < unitInputs.length; i++) {
            unitInputs[i] = new InitializationInputVBox();
        }

        HBox allComps = new HBox();
        allComps.setSpacing(10.0);
        for (int i = 0; i < unitInputs.length; i++) {
            allComps.getChildren().add(unitInputs[i].getUnitInputVBox("Unit " + (i+1))); //TODO: nonSetupUnits.get(i).getUnitId()));
        }

        // error message to show to user in case of an error
        Text errorMessage = new Text("");

        // submit button
        Button submit = new Button("Submit");
        submit.setOnMouseClicked(event -> {

            boolean allValid = true;
            int[][][] allAttackPatterns = new int[UNITS_PER_PLAYER][ATTACK_PATTERN_SIZE][ATTACK_PATTERN_SIZE];
            boolean[][][] allTerrainPatterns = new boolean[UNITS_PER_PLAYER][ATTACK_PATTERN_SIZE][ATTACK_PATTERN_SIZE];
            int[] allHps = new int[UNITS_PER_PLAYER];
            int[] allSpeeds = new int[UNITS_PER_PLAYER];

            for (int i = 0; i < unitInputs.length; i++) {
                // check conditions using helper method below
                boolean valid = isValid(unitInputs[i].hpField,
                        unitInputs[i].speedField,
                        unitInputs[i].attackPatternGrid,
                        unitInputs[i].terrainPatternGrid);

                // if valid, then set the actual values in the array above
                if (valid) {
                    allAttackPatterns[i] = unitInputs[i].attackPatternGrid.getAttackPattern();
                    allTerrainPatterns[i] = unitInputs[i].terrainPatternGrid.getTerrainPattern();
                    allHps[i] = getNumFromTextField(unitInputs[i].hpField, UnitSetup.BASE_HEALTH);
                    allSpeeds[i] = getNumFromTextField(unitInputs[i].speedField, UnitSetup.BASE_SPEED);
                } else {
                    // print out an error message for the user to see
                    errorMessage.setText("invalid conditions\n");
                    allValid = false;
                    break;
                }
            }

            // set the local variables to the instance variables so that
            // the instance variables remain null / uninitialized if there
            // is an error in the input
            // close the window and countdown the latch (which will release
            // the latch.await() from awaitAndGetInstance() method in this
            // class), allowing GUIPlayerCommunicator.getUnitsSetup() to
            // continue.
            if (allValid) {
                attackPatterns = allAttackPatterns;
                terrainPatterns = allTerrainPatterns;
                hps = allHps;
                speeds = allSpeeds;

                primaryStage.close();
                latch.countDown();
            }
        });

        // if initialization is cancelled by closing the window, then
        // exit the game.
        primaryStage.setOnCloseRequest(event -> {
            latch.countDown();
            System.out.println("Game exited.");
            System.exit(0);
        });

        // Show everything
        VBox root = new VBox();
        root.getChildren().addAll(allComps, errorMessage, submit);
        primaryStage.setScene(new Scene(root, DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT));
        primaryStage.show();
    }


    // ---------------------- DECISION GUI ----------------------

    /**
     * A lookup table for conversion from String (since the ChoiceBox automatically
     * has a certain amount of variables already set) to Direction object that
     * needs to be returned later.
     */
    private static final java.util.Map<String, Direction> DIRECTION_MAP = new HashMap<>();
    static {
        DIRECTION_MAP.put("Stay", Direction.STAY);
        DIRECTION_MAP.put("Don't Attack", Direction.STAY);
        DIRECTION_MAP.put("Left", Direction.LEFT);
        DIRECTION_MAP.put("Right", Direction.RIGHT);
        DIRECTION_MAP.put("Up", Direction.UP);
        DIRECTION_MAP.put("Down", Direction.DOWN);
    }

    private static final java.util.Map<String, Integer> PRIORITY_MAP = new HashMap<>();
    static {
        PRIORITY_MAP.put("First", 1);
        PRIORITY_MAP.put("Second", 2);
        PRIORITY_MAP.put("Third", 3);
        PRIORITY_MAP.put("", 0);
    }

    /**
     * Launch the Decision input GUI. Requires a running instance of Application, but
     * will create its own Stage to display the GUI onto.
     * @param setPlayerNum player number to display as the title of the GUI
     * @param units an array of Unit objects to be accessed for speed and ID
     */
    void launchDecisionGui(final int setPlayerNum, final List<Unit> units) {
        playerNum = setPlayerNum;
        Stage stage = new Stage();
        stage.setTitle("Player " + setPlayerNum + " Decision");

        Text directions = new Text("Type in a number (1, 2, 3) in the first box " +
                "for the priority, then choose movement step(s) and a direction of attack.");

        // DecisionInputHBox is an object that holds the HBox and its internal
        // priority, movements, and attack fields that can be accessed later for
        // their values. Therefore storing both the object and the HBox created
        // by it is important.
        DecisionInputHBox[] allUnitDerivationObjs = new DecisionInputHBox[units.size()];
        HBox[] allUnitHBoxes = new HBox[units.size()];
        for (int i = 0; i < units.size(); i++) {
            allUnitDerivationObjs[i] = new DecisionInputHBox();
            allUnitHBoxes[i] = allUnitDerivationObjs[i].getDecisionInputHBox(
                    "Unit " + units.get(i).getId(), units.get(i).getSpeed());
        }

        // default error message that can change if there is an error
        Text errorMessage = new Text("");

        // submit button
        Button submit = new Button("Submit");
        submit.setOnMouseClicked(event -> {
            List<UnitDecision> myDecisions = new ArrayList<>();

            for (int i = 0; i < units.size(); i++) {
                List<Direction> myMovements = new ArrayList<>();
                for (int j = 0; j < units.get(i).getSpeed(); j++) {
                    String choice = allUnitDerivationObjs[i].movements[j].getValue();
                    myMovements.add(DIRECTION_MAP.get(choice));
                }

                myDecisions.add(
                        new UnitDecision(PRIORITY_MAP.get(allUnitDerivationObjs[i].priority.getValue()),
                                units.get(i).getId(),
                                DIRECTION_MAP.get(allUnitDerivationObjs[i].attack.getValue()),
                                myMovements));
            }

            // if the Decision that the player made was valid (if the priorities were
            // set without any duplicates and only had a 1, 2, or a 3), then close
            // the window and countdown the latch (which will allow the next part of
            // the code to run (awaitAndGetInstance() in this file and
            // GUIPlayerCommunicator.getDecision())
                try {
                    throwExceptionOnInvalidDecisionList(myDecisions, units);
                    decisions = myDecisions;

                    stage.close();
                    latch.countDown();
                } catch (InvalidDecisionException ex) {
                    errorMessage.setText("Invalid decision: " + ex.getMessage());
                }
        });

        // create a "root" node that we can store all of our stuff in
        VBox hBoxWrapper = new VBox();
        // filter out all of the dead units
        HBox[] nonNullHBoxes = Arrays.stream(allUnitHBoxes).filter(Objects::nonNull).toArray(HBox[]::new);
        hBoxWrapper.getChildren().add(directions);
        hBoxWrapper.getChildren().addAll(nonNullHBoxes);
        hBoxWrapper.getChildren().addAll(errorMessage, submit);

        // use the max speed to adjust how wide the screen should be
        int maxSpeed = units.stream().max(Comparator.comparingInt(Unit::getSpeed)).get().getSpeed();
        // if the screen is prematurely closed, then quit the game
        stage.setOnCloseRequest(event -> {
            latch.countDown();
            System.err.println("Unit decision was not made properly.");
            System.exit(0);
        });

        // create the Scene and show the stage
        Scene root = new Scene(hBoxWrapper, DEFAULT_SCENE_WIDTH * (1 + maxSpeed * 0.05), DEFAULT_SCENE_HEIGHT * 0.5);
        stage.setScene(root);
        stage.show();
    }


    // ---------------------- HELPER + GETTER METHODS ----------------------

    private static int getNumFromTextField(TextField field, int defaultNum) {
        if (field == null) {
            return defaultNum;
        }

        String fieldText = field.getText();
        return fieldText.matches("^\\d+$") ?
                Integer.parseInt(fieldText) : defaultNum;
    }

    /** Helper method for validating a condition given TextFields */
    private static boolean isValid(TextField hpField, TextField speedField, AttackPatternGrid attackGrid, TerrainPatternGrid terrainGrid) {
        int hp = getNumFromTextField(hpField, 0);
        int speed = getNumFromTextField(speedField, 0);

        // get attack pattern from grid
        int[][] attackPattern = attackGrid.getAttackPattern();

        boolean[][] terrainCreation = terrainGrid.getTerrainPattern();

        try {
            UnitSetup.throwExceptionOnInvalidSetup(new UnitSetup(attackPattern, terrainCreation, hp, speed, 0));
            return true;
        } catch (InvalidSetupException ex){
            return false;
        }
    }

    int[][][] getAttackPatterns() {
        return attackPatterns;
    }

    boolean[][][] getTerrainPatterns() {
        return terrainPatterns;
    }

    int[] getHps() {
        return hps;
    }

    int[] getSpeeds() {
        return speeds;
    }

    List<UnitDecision> getDecisions() {
        return decisions;
    }
}


