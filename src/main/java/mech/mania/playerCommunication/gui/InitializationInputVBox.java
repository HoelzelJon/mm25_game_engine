package mech.mania.playerCommunication.gui;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import mech.mania.playerCommunication.UnitSetup;

/**
 * Wrapper class that contains information for one entire VBox of the initialization
 * screen. Contains the TextField for the grid, the hp input box and speed input box.
 */
class InitializationInputVBox {

    TextField hpField;
    TextField speedField;
    AttackPatternGrid attackPatternGrid;
    TerrainPatternGrid terrainPatternGrid;

    VBox getUnitInputVBox(String title) {

        Text titleText = new Text(title);

        attackPatternGrid = new AttackPatternGrid();
        GridPane attackGridPane = attackPatternGrid.createGrid();

        terrainPatternGrid = new TerrainPatternGrid();
        GridPane terrainGridPane = terrainPatternGrid.createGrid();

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
                titleText,
                hpBox,
                speedBox,
                new Text("Attack Pattern:"),
                attackGridPane,
                new Text("Terrain Creation:"),
                terrainGridPane
        );
        return allComps;
    }
}
