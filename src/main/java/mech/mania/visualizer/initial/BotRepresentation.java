package mech.mania.visualizer.initial;

import mech.mania.AttackTile;
import mech.mania.Direction;
import mech.mania.Unit;

import java.util.ArrayList;
import java.util.List;

import static mech.mania.playerCommunication.UnitSetup.ATTACK_PATTERN_SIZE;

public class BotRepresentation {
    private int botId;
    private int health;
    private List<AttackTileRepresentation> attackPattern;

    public BotRepresentation(Unit unit) {
        botId = unit.getId();
        health = unit.getHp();

        attackPattern = new ArrayList<>();

        AttackTile[][] tiles = unit.getAttack(Direction.UP);
        for (int xInd = 0; xInd < tiles.length; xInd ++) {
            for (int yInd = 0; yInd < tiles[xInd].length; yInd ++) {
                AttackTile tile = tiles[xInd][yInd];
                if (tile.getDamage() > 0) {
                    attackPattern.add(new AttackTileRepresentation(
                            xInd - (ATTACK_PATTERN_SIZE / 2),
                            yInd - (ATTACK_PATTERN_SIZE / 2),
                            tile));
                }
            }
        }
    }
}
