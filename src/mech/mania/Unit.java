package mech.mania;

/*
 * Represents a single mech.
 */
public class Unit {
    private int hp; // unit's current health
    private int speed; // unit's speed (number of tiles it can move per turn)
    private Position pos; // position of the unit
    private int[][] attack; // 2-D grid of attack damages

    public Unit(Position pos) {
        hp = 2;
        speed = 4;
        this.pos = pos;
        attack = new int[5][5];
    }

    public int getHp() {
        return hp;
    }

    public int getSpeed() {
        return speed;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    public int[][] getAttack() {
        return attack;
    }

    public boolean isAlive() {
        return (hp > 0);
    }

    public void takeCollisionDamage() {
        hp --;
    }
}
