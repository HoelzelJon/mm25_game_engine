package mech.mania;

import mech.mania.playerCommunication.UnitSetup;

/**
 * Represents a single mech.
 */
public class Unit {
    private static final int COLLISION_DAMAGE = 1;

    private int hp; // unit's current health
    private int speed; // unit's speed (number of tiles it can move per turn)
    private Position pos; // position of the unit
    private int[][] attack; // 2-D grid of attack damages
    private boolean[][] terrain;
    private boolean isAlive;
    private int id;
    private int playerNum;

    Unit(UninitializedUnit uninitializedUnit, UnitSetup setup) {
        id = uninitializedUnit.getUnitId();
        hp = setup.getHealth();
        speed = setup.getSpeed();
        pos = uninitializedUnit.getPos();
        attack = setup.getAttackPattern();
        terrain = setup.getTerrainPattern();
        isAlive = true;
        playerNum = uninitializedUnit.getPlayerNum();
    }

    public int getId() {
        return id;
    }

    public int getHp() {
        return hp;
    }

    public int getSpeed() {
        return speed;
    }

    Position getPos() {
        return pos;
    }

    void setPos(Position pos) {
        this.pos = pos;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    /**
     * Does the death of the unit
     *
     * @return true if the unit just died, false otherwise
     */
    boolean doDeath() {
        if (hp <= 0) {
            isAlive = false;
            pos = new Position(-10, -10);
            return true;
        }
        return false;
    }

    void takeCollisionDamage() {
        if (isAlive) {
            hp -= COLLISION_DAMAGE;
        }
    }

    public void takeDamage(int damage) {
        if (isAlive) {
            hp -= damage;
        }
    }

    /**
     * Returns a rotated version of this unit's attack
     * if dir == UP or dir == STAY then returns unaltered attack
     * if dir == RIGHT then returns copy of attack, rotated 90 degrees clockwise
     * if dir == DOWN then returns copy of attack, rotated 180 degrees
     * if dir == LEFT then returns copy of attack, rotated 90 degrees counterclockwise
     *
     * @param dir direction the attack is facing
     * @return this unit's attack, rotated in direction dir
     */
    public AttackTile[][] getAttack(Direction dir) {
        int width = attack.length;
        int height = attack[0].length;

        if (dir == Direction.LEFT) {
            AttackTile[][] ret = new AttackTile[height][width];

            for (int y = 0; y < ret[0].length; y++) {
                for (int x = 0; x < ret.length; x++) {
                    ret[x][y] = new AttackTile(attack[y][width - x - 1], terrain[y][width - x - 1]);
                }
            }
            return ret;
        } else if (dir == Direction.DOWN) {
            AttackTile[][] ret = new AttackTile[width][height];

            for (int y = 0; y < ret[0].length; y++) {
                for (int x = 0; x < ret.length; x++) {
                    ret[x][y] = new AttackTile(attack[width - x - 1][height - y - 1], terrain[width - x - 1][height - y - 1]);
                }
            }
            return ret;
        } else if (dir == Direction.RIGHT) {
            AttackTile[][] ret = new AttackTile[height][width];

            for (int y = 0; y < ret[0].length; y++) {
                for (int x = 0; x < ret.length; x++) {
                    ret[x][y] = new AttackTile(attack[height - y - 1][x], terrain[height - y - 1][x]);
                }
            }
            return ret;
        } else if (dir == Direction.UP) {
            AttackTile[][] ret = new AttackTile[height][width];

            for (int y = 0; y < ret[0].length; y++) {
                for (int x = 0; x < ret.length; x++) {
                    ret[x][y] = new AttackTile(attack[y][x], terrain[y][x]);
                }
            }
            return ret;
        } else { // direction = STAY
            AttackTile[][] ret = new AttackTile[height][width];
            for (int y = 0; y < ret[0].length; y ++) {
                for (int x = 0; x < ret.length; x ++) {
                    ret[x][y] = new AttackTile(0, false);
                }
            }
            return ret;
        }
    }
}
