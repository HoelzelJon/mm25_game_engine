package mech.mania.visualizer.initial;

import mech.mania.AttackTile;

import java.util.Arrays;
import java.util.List;

public class AttackTileRepresentation {
    private List<Integer> coordinates; // length-2 list containing (x,y) position of the attack relative to the bot
    private int damage;
    private boolean createWall;

    public AttackTileRepresentation(int x, int y, AttackTile attackTile) {
        coordinates = Arrays.asList(x, y);
        damage = attackTile.getDamage();
        createWall = attackTile.isTerrainCreation();
    }
}
