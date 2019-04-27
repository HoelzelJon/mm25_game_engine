package mech.mania;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import static java.util.Map.entry;

public class GUIInitialUnitInput extends Application {

    private int[][][] attackPatterns;
    private int[] hps;
    private int[] speeds;
    private int[] priorities;
    private Direction[][] movements;
    private Direction[] attacks;

    private int playerNum;
    private static final int DEFAULT_SCENE_WIDTH = 600;
    private static final int DEFAULT_SCENE_HEIGHT = 350;
    private static final int NUM_UNITS = 3;
    private static final int GRID_SIZE = 7;

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
    void launchInitializationGui(int setPlayerNum) {
        playerNum = setPlayerNum;
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
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Player " + playerNum + " : Mechmania 25 Unit Initialization");

        InitializationInputVBox[] unitInputs = new InitializationInputVBox[3];
        for (int i = 0; i < unitInputs.length; i++) {
            unitInputs[i] = new InitializationInputVBox();
        }

        HBox allComps = new HBox();
        allComps.setSpacing(10.0);
        for (int i = 0; i < unitInputs.length; i++) {
            allComps.getChildren().add(unitInputs[i].getUnitInputVBox("Unit " + (i + 1)));
        }

        // error message to show to user in case of an error
        Text errorMessage = new Text("");

        // submit button
        Button submit = new Button("Submit");
        submit.setOnMouseClicked(event -> {

            boolean allValid = true;
            int[][][] allAttackPatterns = new int[NUM_UNITS][GRID_SIZE][GRID_SIZE];
            int[] allHps = new int[NUM_UNITS];
            int[] allSpeeds = new int[NUM_UNITS];

            for (int i = 0; i < unitInputs.length; i++) {
                // check conditions using helper method below
                boolean valid = isValid(unitInputs[i].hpField,
                        unitInputs[i].speedField,
                        unitInputs[i].attackPatternGrid);

                // if valid, then set the actual values in the array above
                if (valid) {
                    allAttackPatterns[i] = unitInputs[i].attackPatternGrid.getAttackPattern();
                    allHps[i] = getNumFromTextField(unitInputs[i].hpField, UnitSetup.BASE_HEALTH);
                    allSpeeds[i] = getNumFromTextField(unitInputs[i].speedField, UnitSetup.BASE_SPEED);
                } else {
                    // print out an error message for the user to see
                    errorMessage.setText("invalid conditions\n" +
                            UnitSetup.getErrorMessage());
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
    private static final java.util.Map<String, Direction> DIRECTION_MAP = java.util.Map.ofEntries(
            entry("Stay", Direction.STAY),
            entry("Don't Attack", Direction.STAY),
            entry("Left", Direction.LEFT),
            entry("Right", Direction.RIGHT),
            entry("Up", Direction.UP),
            entry("Down", Direction.DOWN)
    );

    private static final java.util.Map<String, Integer> PRIORITY_MAP = java.util.Map.of(
            "First", 1,
            "Second", 2,
            "Third", 3,
            "", 0
    );

    /**
     * Launch the Decision input GUI. Requires a running instance of Application, but
     * will create its own Stage to display the GUI onto.
     * @param setPlayerNum player number to display as the title of the GUI
     * @param units an array of Unit objects to be accessed for speed and ID
     */
    void launchDecisionGui(final int setPlayerNum, final Unit[] units) {
        playerNum = setPlayerNum;
        Stage stage = new Stage();
        stage.setTitle("Player " + setPlayerNum + " Decision");

        Text directions = new Text("Type in a number (1, 2, 3) in the first box " +
                "for the priority, then choose three movement steps and a direction of attack.");

        // DecisionInputHBox is an object that holds the HBox and its internal
        // priority, movements, and attack fields that can be accessed later for
        // their values. Therefore storing both the object and the HBox created
        // by it is important.
        DecisionInputHBox[] allUnitDerivationObjs = new DecisionInputHBox[units.length];
        HBox[] allUnitHBoxes = new HBox[units.length];
        for (int i = 0; i < units.length; i++) {
            if (units[i].isAlive()) {
                allUnitDerivationObjs[i] = new DecisionInputHBox();
                allUnitHBoxes[i] = allUnitDerivationObjs[i].getDecisionInputHBox(
                        "Unit " + units[i].getId(), units[i].getSpeed());
            }
        }

        // default error message that can change if there is an error
        Text errorMessage = new Text("");

        // submit button
        Button submit = new Button("Submit");
        submit.setOnMouseClicked(event -> {
            int[] priorities = new int[units.length];
            Direction[][] movements = new Direction[units.length][];
            Direction[] attacks = new Direction[units.length];

            for (int i = 0; i < units.length; i++) {
                
                // if the unit is dead, then this index of the list wouldn't 
                // have been initialized, so to prevent NullPointerException
                // we need to account for it here.
                if (allUnitDerivationObjs[i] == null) {
                    priorities[i] = 0;
                    attacks[i] = Direction.STAY;
                    Direction[] myMovements = new Direction[units[i].getSpeed()];
                    for (int j = 0; j < units[i].getSpeed(); j++) {
                        myMovements[j] = Direction.STAY;
                    }
                    movements[i] = myMovements;
                    continue;
                }

                // if the unit is still alive, then get the corresponding values from
                // each of the TextFields and ChoiceBoxes that were displayed on screen
                priorities[i] = PRIORITY_MAP.get(allUnitDerivationObjs[i].priority.getValue());
                attacks[i] = DIRECTION_MAP.get(allUnitDerivationObjs[i].attack.getValue());

                Direction[] myMovements = new Direction[units[i].getSpeed()];
                for (int j = 0; j < units[i].getSpeed(); j++) {
                    String choice = allUnitDerivationObjs[i].movements[j].getValue();
                    myMovements[j] = DIRECTION_MAP.get(choice);
                }
                movements[i] = myMovements;
            }


            // get all the priorities for units that are alive to check validity
            int numAlive = (int) Arrays.stream(units).filter(Unit::isAlive).count();
            int[] filteredPriorities = new int[numAlive];
            for (int i = 0, j = 0; i < units.length; i++) {
                if (units[i].isAlive()) {
                    filteredPriorities[j++] = priorities[i];
                }
            }


            // if the Decision that the player made was valid (if the priorities were
            // set without any duplicates and only had a 1, 2, or a 3), then close
            // the window and countdown the latch (which will allow the next part of
            // the code to run (awaitAndGetInstance() in this file and 
            // GUIPlayerCommunicator.getDecision())
            if (Decision.hasValidDecision(filteredPriorities, movements, attacks)) {
                this.priorities = priorities;
                this.movements = movements;
                this.attacks = attacks;

                stage.close();
                latch.countDown();
            } else {
                // invalidate the input, don't close the window, and display an
                // error message for the user to see.
                errorMessage.setText(Decision.getErrorMessage());
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
        int maxSpeed = Arrays.stream(units).max(Comparator.comparingInt(Unit::getSpeed)).get().getSpeed();
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
    private static boolean isValid(TextField hpField, TextField speedField, AttackPatternGrid grid) {
        int hp = getNumFromTextField(hpField, 0);
        int speed = getNumFromTextField(speedField, 0);

        // get attack pattern from grid
        int[][] attackPattern = grid.getAttackPattern();

        return UnitSetup.hasValidStartingConditions(hp, speed, attackPattern);
    }

    int[][][] getAttackPatterns() {
        return attackPatterns;
    }

    int[] getHps() {
        return hps;
    }

    int[] getSpeeds() {
        return speeds;
    }

    int[] getPriorities() {
        return priorities;
    }

    Direction[][] getMovements() {
        return movements;
    }

    Direction[] getAttacks() {
        return attacks;
    }

}

/**
 * Wrapper class that contains information for creating the decision VBox used
 * for the decision GUI.
 */
class DecisionInputHBox {

    private static final String[] PRIORITY_CHOICES = {"", "First", "Second", "Third"};
    private static final String[] MOVEMENT_CHOICES = {"Stay", "Left", "Right", "Up", "Down"};
    private static final String[] ATTACK_CHOICES = {"Don't Attack", "Left", "Right", "Up", "Down"};

    ChoiceBox<String> priority;
    ChoiceBox<String>[] movements;
    ChoiceBox<String> attack;

    /** 
     * Get a HBox containing the Unit ID, unit speed number of ChoiceBox's
     * that have the choices MOVEMENT_CHOICES and one ChoiceBox containing
     * the choices ATTACK_CHOICES
     */
    HBox getDecisionInputHBox(String title, int unitSpeed) {
        Text titleText = new Text(title);

        // priority
        priority = new ChoiceBox<>();
        priority.getItems().addAll(PRIORITY_CHOICES);
        priority.getSelectionModel().selectFirst();

        // movements
        HBox allMovements = new HBox();
        movements = new ChoiceBox[unitSpeed];
        for (int i = 0; i < unitSpeed; i++) {
            movements[i] = new ChoiceBox<>();
            movements[i].getItems().addAll(MOVEMENT_CHOICES);
            movements[i].getSelectionModel().selectFirst();
        }
        allMovements.getChildren().addAll(movements);

        // attacks
        attack = new ChoiceBox<>();
        attack.getItems().addAll(ATTACK_CHOICES);
        attack.getSelectionModel().selectFirst();

        HBox root = new HBox();
        root.getChildren().addAll(titleText, priority, allMovements, attack);
        root.setSpacing(10);
        return root;
    }
}


/**
 * Wrapper class that contains information for one entire VBox of the initialization
 * screen. Contains the TextField for the grid, the hp input box and speed input box.
 */
class InitializationInputVBox {

    TextField hpField;
    TextField speedField;
    AttackPatternGrid attackPatternGrid;

    VBox getUnitInputVBox(String title) {

        Text titleText = new Text(title);

        attackPatternGrid = new AttackPatternGrid();
        GridPane gridPane = attackPatternGrid.createGrid();

        HBox hpBox = new HBox();
        Text hpText = new Text("hp: ");
        hpField = new TextField("" + UnitSetup.BASE_HEALTH);
        hpBox.getChildren().addAll(hpText, hpField);

        HBox speedBox = new HBox();
        Text speedText = new Text("speed: ");
        speedField = new TextField("" + UnitSetup.BASE_SPEED);
        speedBox.getChildren().addAll(speedText, speedField);

        // add to 1 VBox, then return
        VBox allComps = new VBox();
        allComps.getChildren().addAll(
                titleText, hpBox, speedBox, gridPane
        );
        return allComps;
    }
}

/**
 * Another wrapper class that has methods to create a grid of TextField objects
 * in the shape defined in the POSITIONS char[][] and the INVALID, VALID, and
 * MECH final char variables, which denote what should be placed when the program
 * encounters the character in the position grid.
 */
class AttackPatternGrid {

    private static final int SIZE = 7;

    /** array of the actual Nodes that will be on the grid */
    private Node[][] nodes = new Node[SIZE][SIZE];

    /** positions on the map where everything is supposed to be. */
    private static final char[][] POSITIONS = {
            {'x', 'x', 'x', '_', 'x', 'x', 'x'},
            {'x', 'x', '_', '_', '_', 'x', 'x'},
            {'x', '_', '_', '_', '_', '_', 'x'},
            {'_', '_', '_', 'M', '_', '_', '_'},
            {'x', '_', '_', '_', '_', '_', 'x'},
            {'x', 'x', '_', '_', '_', 'x', 'x'},
            {'x', 'x', 'x', '_', 'x', 'x', 'x'}
    };
    /** chars in the board array corresponding to each thing that is supposed
     * to be on it */
    private static final char INVALID = 'x';
    private static final char VALID = '_';
    private static final char MECH = 'M';

    int[][] getAttackPattern() {
        int[][] pattern = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                // if null then it wasn't a textfield, ignore
                if (nodes[i][j] == null) {
                    pattern[i][j] = 0;
                } else {
                    // must have been a textfield
                    String numStr = ((TextField) nodes[i][j]).getText();
                    int num = numStr.matches("^\\d+$") ?
                            Integer.parseInt(numStr) : 0;
                    pattern[i][j] = num;
                }
            }
        }
        return pattern;
    }

    GridPane createGrid() {
        GridPane grid = new GridPane();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                switch (POSITIONS[i][j]) {
                    case VALID:
                        TextField field = new TextField();
                        nodes[i][j] = field;
                        grid.add(field, i, j);
                        break;

                    case MECH:
                        Text text = new Text("M");
                        grid.add(text, i, j);
                        grid.setAlignment(Pos.CENTER);
                    case INVALID:
                        nodes[i][j] = null;
                        break;
                }
            }
        }
        return grid;
    }
}