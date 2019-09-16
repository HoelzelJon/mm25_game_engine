package mech.mania.playerCommunication.gui;

import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

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
