package mech.mania.playerCommunication.gui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

/**
 * Another wrapper class that has methods to create a grid of CheckBox objects
 * in the shape defined in the POSITIONS char[][] and the INVALID, VALID, and
 * MECH final char variables, which denote what should be placed when the program
 * encounters the character in the position grid.
 */
class TerrainPatternGrid {

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

    boolean[][] getTerrainPattern() {
        boolean[][] pattern = new boolean[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
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

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                switch (POSITIONS[i][j]) {
                    case VALID:
                        CheckBox field = new CheckBox();
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