package mech.mania;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public class GUIInitialBotInput extends Application {

    private int[][][] attackPatterns;
    private int[] hps;
    private int[] speeds;
    private int[] priorities;
    private Direction[][] movements;
    private Direction[] attacks;

    private int playerNum = 1;
    private static final int DEFAULT_HP = 4;
    private static final int DEFAULT_SPEED = 5;
    private static final int SCENE_WIDTH = 600;
    private static final int SCENE_HEIGHT = 350;
    private static final int NUM_UNITS = 3;
    private static final int GRID_SIZE = 7;
    private static GUIInitialBotInput instance;
    private static CountDownLatch latch;

    // ---------------------- STUFF TO GET JAVAFX TO COOPERATE ----------------------

    public GUIInitialBotInput() {
        instance = this;
    }

    public static GUIInitialBotInput awaitAndGetInstance() {
        latch = new CountDownLatch(1);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return instance;
    }

    // ---------------------- INITIALIZATION GUI ----------------------

    /**
     * Alias function for start, since getting initialization values for
     * bots happens at the start of the Application launching, while Decision
     * GUI showing will not occur until after Application has started (and don't
     * have to deal with any issues of Application not being launched already)
     */
    public void launchInitializationGui(int setPlayerNum) {
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
     * <p>
     * Get values set by this function through the getAll___() methods
     * <p>
     * Note: call GUIInitialBotInput.launch() from other code to create this
     * window.
     *
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Player " + playerNum + " : Mechmania 25 Bot Initialization");

        InitializationInputVBox[] botInputs = new InitializationInputVBox[3];
        for (int i = 0; i < botInputs.length; i++) {
            botInputs[i] = new InitializationInputVBox();
        }

        HBox allComps = new HBox();
        allComps.setSpacing(10.0);
        for (int i = 0; i < botInputs.length; i++) {
            allComps.getChildren().add(
                    botInputs[i].getBotInputVBox("Bot " + (i + 1))
            );
        }

        Text errorMessage = new Text("");

        Button submit = new Button("Submit");
        submit.setOnMouseClicked(event -> {

            boolean allValid = true;
            int[][][] allAttackPatterns = new int[NUM_UNITS][GRID_SIZE][GRID_SIZE];
            int[] allHps = new int[NUM_UNITS];
            int[] allSpeeds = new int[NUM_UNITS];

            for (int i = 0; i < botInputs.length; i++) {
                // check conditions using method from GUIPlayerCommunicator
                boolean valid = isValid(botInputs[i].hpField,
                        botInputs[i].speedField,
                        botInputs[i].attackPatternGrid);

                if (valid) {
                    errorMessage.setText("success! you may now close the window");
                    allAttackPatterns[i] = botInputs[i].attackPatternGrid.getAttackPattern();
                    allHps[i] = getNumFromTextField(botInputs[i].hpField, DEFAULT_HP);
                    allSpeeds[i] = getNumFromTextField(botInputs[i].speedField, DEFAULT_SPEED);
                } else {
                    errorMessage.setText("invalid conditions\n" +
                            GUIPlayerCommunicator.getErrorMessage());
                    allValid = false;
                    break;
                }
            }

            if (allValid) {
                attackPatterns = allAttackPatterns;
                hps = allHps;
                speeds = allSpeeds;

                primaryStage.close();
                latch.countDown();
            }
        });

        primaryStage.setOnCloseRequest(event -> {
            latch.countDown();
            System.out.println("Game exited.");
            System.exit(0);
        });

        // Show everything
        VBox root = new VBox();
        root.getChildren().addAll(allComps, errorMessage, submit);
        primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
        primaryStage.show();
    }


    // ---------------------- DECISION GUI ----------------------

    private static final java.util.Map<String, Direction> DIRECTION_MAP = java.util.Map.ofEntries(
            entry("Stay", Direction.STAY),
            entry("Don't Attack", Direction.STAY),
            entry("Left", Direction.LEFT),
            entry("Right", Direction.RIGHT),
            entry("Up", Direction.UP),
            entry("Down", Direction.DOWN)
    );

    public void launchDecisionGui(int setPlayerNum, Unit[] units) {
        playerNum = setPlayerNum;
        Stage stage = new Stage();
        stage.setTitle("Player " + setPlayerNum + " Decision");

        Text directions = new Text("Type in a number (1, 2, 3) in the first box " +
                "for the priority,\n" +
                "then choose three movement steps and a direction of attack.");

        DecisionInputHBox[] allBotDerivationObjs = new DecisionInputHBox[units.length];
        HBox[] allBotHBoxes = new HBox[units.length];
        for (int i = 0; i < units.length; i++) {
            if (units[i].isAlive()) {
                allBotDerivationObjs[i] = new DecisionInputHBox();
                allBotHBoxes[i] = allBotDerivationObjs[i].getDecisionInputHBox(
                        "Bot " + units[i].getId(), units[i].getSpeed());
            }
        }

        Text errorMessage = new Text("");

        Button submit = new Button("Submit");
        submit.setOnMouseClicked(event -> {
            int[] priorities = new int[units.length];
            Direction[][] movements = new Direction[units.length][];
            Direction[] attacks = new Direction[units.length];

            for (int i = 0; i < units.length; i++) {
                if (allBotDerivationObjs[i] == null) {
                    priorities[i] = 0;
                    attacks[i] = Direction.STAY;
                    Direction[] myMovements = new Direction[units[i].getSpeed()];
                    for (int j = 0; j < units[i].getSpeed(); j++) {
                        myMovements[j] = Direction.STAY;
                    }
                    movements[i] = myMovements;
                    continue;
                }

                priorities[i] = getNumFromTextField(allBotDerivationObjs[i].priority, -1);
                attacks[i] = DIRECTION_MAP.get(allBotDerivationObjs[i].attack);

                Direction[] myMovements = new Direction[units[i].getSpeed()];
                for (int j = 0; j < units[i].getSpeed(); j++) {
                    String choice = allBotDerivationObjs[i].movements[j].getValue();
                    myMovements[j] = DIRECTION_MAP.get(choice);
                }
                movements[i] = myMovements;
            }

            if (GUIPlayerCommunicator.hasValidDecision(priorities, movements, attacks)) {
                this.priorities = priorities;
                this.movements = movements;
                this.attacks = attacks;

                stage.close();
                latch.countDown();
            } else {
                errorMessage.setText(GUIPlayerCommunicator.getErrorMessage());
            }
        });

        VBox hBoxWrapper = new VBox();
        HBox[] nonNullHBoxes = Arrays.stream(allBotHBoxes).filter(Objects::nonNull).toArray(HBox[]::new);
        hBoxWrapper.getChildren().add(directions);
        hBoxWrapper.getChildren().addAll(nonNullHBoxes);
        hBoxWrapper.getChildren().addAll(errorMessage, submit);

        int maxSpeed = Arrays.stream(units).max(Comparator.comparingInt(Unit::getSpeed)).get().getSpeed();
        stage.setOnCloseRequest(event -> {
            latch.countDown();
            System.err.println("Unit decision was not made properly.");
            System.exit(0);
        });
        Scene root = new Scene(hBoxWrapper, SCENE_WIDTH * (1 + maxSpeed * 0.05), SCENE_HEIGHT * 0.5);
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

    private static boolean isValid(TextField hpField, TextField speedField, AttackPatternGrid grid) {
        int hp = getNumFromTextField(hpField, 0);
        int speed = getNumFromTextField(speedField, 0);

        // get attack pattern from grid
        int[][] attackPatterns = grid.getAttackPattern();

        return GUIPlayerCommunicator.hasValidStartingConditions(attackPatterns, hp, speed);
    }

    public int[][][] getAttackPatterns() {
        return attackPatterns;
    }

    public int[] getHps() {
        return hps;
    }

    public int[] getSpeeds() {
        return speeds;
    }

    public int[] getPriorities() {
        return priorities;
    }

    public Direction[][] getMovements() {
        return movements;
    }

    public Direction[] getAttacks() {
        return attacks;
    }

}

/**
 * Wrapper class that contains information for creating the decision VBox used
 * for the decision GUI.
 */
class DecisionInputHBox {

    private static final String[] MOVEMENT_CHOICES = {"Stay", "Left", "Right", "Up", "Down"};
    private static final String[] ATTACK_CHOICES = {"Don't Attack", "Left", "Right", "Up", "Down"};

    TextField priority;
    ChoiceBox<String>[] movements;
    ChoiceBox<String> attack;

    public HBox getDecisionInputHBox(String title, int botSpeed) {
        Text titleText = new Text(title);

        // priority
        priority = new TextField();
        priority.setMaxWidth(30);

        // movements
        HBox allMovements = new HBox();
        movements = new ChoiceBox[botSpeed];
        for (int i = 0; i < botSpeed; i++) {
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

    public VBox getBotInputVBox(String title) {

        Text titleText = new Text(title);

        attackPatternGrid = new AttackPatternGrid();
        GridPane gridPane = attackPatternGrid.createGrid();

        HBox hpBox = new HBox();
        Text hpText = new Text("hp: ");
        hpField = new TextField();
        hpBox.getChildren().addAll(hpText, hpField);

        HBox speedBox = new HBox();
        Text speedText = new Text("speed: ");
        speedField = new TextField();
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

    public int[][] getAttackPattern() {
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

    public GridPane createGrid() {
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
                        text.setTextAlignment(TextAlignment.CENTER);
                    case INVALID:
                        nodes[i][j] = null;
                        break;
                }
            }
        }
        return grid;
    }
}