package mech.mania;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GUIInitialBotInput extends Application {

    private static int[][][] attackPatterns;
    private static int SCENE_WIDTH = 900;
    private static int SCENE_HEIGHT = 350;

    /**
     * A GUI for easier player input. Contains input for HP, speed, and attack
     * pattern.
     * <p>
     * Get values set by this function through the get___() methods
     * <p>
     * Note: call GUIInitialBotInput.launch() from other code to create this
     * window.
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mechmania 25 Bot Initialization");

        IndividualInputBox[] botInputs = new IndividualInputBox[3];
        for (int i = 0; i < botInputs.length; i++) {
            botInputs[i] = new IndividualInputBox();
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
        submit.setOnMouseClicked(value -> {

            boolean allValid = true;
            int[][][] allAttackPatterns = new int[3][7][7];

            for (int i = 0; i < botInputs.length; i++) {
                // check conditions using method from GUIPlayerCommunicator
                boolean valid = isValid(botInputs[i].hpField, botInputs[i].speedField, botInputs[i].attackPatternGrid);

                if (valid) {
                    errorMessage.setText("success! you may now close the window");
                    allAttackPatterns[i] = botInputs[i].attackPatternGrid.getAttackPattern();
                } else {
                    errorMessage.setText("invalid conditions\n" + GUIPlayerCommunicator.errorMessage);
                    allValid = false;
                    break;
                }
            }

            attackPatterns = allValid ? allAttackPatterns : null;
        });

        // Show everything
        VBox root = new VBox();
        root.getChildren().addAll(allComps, errorMessage, submit);
        primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
        primaryStage.show(); // wait until program ends for next step to occur
    }

    private boolean isValid(TextField hpField, TextField speedField, AttackPatternGrid grid) {
        // get number from hp field
        String hpFieldText = hpField.getText();
        int hp = hpFieldText.matches("^\\d+$") ?
                Integer.parseInt(hpFieldText) : 0;

        // get number from speed field
        String speedFieldText = speedField.getText();
        int speed = speedFieldText.matches("^\\d+$") ?
                Integer.parseInt(speedFieldText) : 0;

        // get attack pattern from grid
        int[][] attackPatterns = grid.getAttackPattern();

        return GUIPlayerCommunicator.hasValidConditions(attackPatterns, hp, speed);
    }

    public static int[][][] getAttackPatterns() {
        return attackPatterns;
    }
}

class IndividualInputBox {

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