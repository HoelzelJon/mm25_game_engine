package mech.mania;

public class AttackTile {
    private int damage;
    private boolean isTerrainCreation;

    public AttackTile(int aDamage, boolean aTerrainCreation) {
        damage = aDamage;
        isTerrainCreation = aTerrainCreation;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isTerrainCreation() {
        return isTerrainCreation;
    }
}
