package mech.mania.playerCommunication.gui;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import static mech.mania.playerCommunication.UnitSetup.ATTACK_PATTERN_SIZE;
import static mech.mania.playerCommunication.gui.AttackPatternGrid.*;

/**
 * Another wrapper class that has methods to create a grid of CheckBox objects
 * in the shape defined in the POSITIONS char[][] and the INVALID, VALID, and
 * MECH final char variables, which denote what should be placed when the program
 * encounters the character in the position grid.
 */
class TerrainPatternGrid {
    /** array of the actual Nodes that will be on the grid */
    private Node[][] nodes = new Node[ATTACK_PATTERN_SIZE][ATTACK_PATTERN_SIZE];

    boolean[][] getTerrainPattern() {
        boolean[][] pattern = new boolean[ATTACK_PATTERN_SIZE][ATTACK_PATTERN_SIZE];
        for (int i = 0; i < ATTACK_PATTERN_SIZE; i++) {
            for (int j = 0; j < ATTACK_PATTERN_SIZE; j++) {
                // if null then it wasn't a textfield, ignore
                if (nodes[i][j] == null) {
                    pattern[i][j] = false;
                } else {
                    pattern[i][j] = ((CheckBox) nodes[i][j]).isSelected();
                }
            }
        }
        return pattern;
    }

    GridPane createGrid() {
        GridPane grid = new GridPane();

        for (int i = 0; i < ATTACK_PATTERN_SIZE; i++) {
            for (int j = 0; j < ATTACK_PATTERN_SIZE; j++) {
                switch (POSITIONS[i][j]) {
                    case VALID:
                        CheckBox field = new CheckBox();
                        nodes[i][j] = field;
                        grid.add(field, i, j);
                        break;

                    case MECH:
                        Text text = new Text("M");
                        grid.add(text, i, j);
                        GridPane.setHalignment(text, HPos.CENTER);
                    case INVALID:
                        nodes[i][j] = null;
                        break;
                }
            }
        }
        return grid;
    }
}
