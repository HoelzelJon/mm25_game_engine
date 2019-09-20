package mech.mania;

import mech.mania.playerCommunication.UnitSetup;

import static mech.mania.playerCommunication.UnitSetup.ATTACK_PATTERN_SIZE;

/**
 * Represents a single mech.
 */
public class Unit {
    private static final int COLLISION_DAMAGE = 1;

    private int hp; // unit's current health
    private int speed; // unit's speed (number of tiles it can move per turn)
    private Position pos; // position of the unit
    private AttackTile[][] attackTiles; // 2-D grid of attack tiles
    private int id;
    private int playerNum;

    Unit(UninitializedUnit uninitializedUnit, UnitSetup setup) {
        id = uninitializedUnit.getUnitId();
        hp = setup.getHealth();
        speed = setup.getSpeed();
        pos = uninitializedUnit.getPos();
        playerNum = uninitializedUnit.getPlayerNum();

        attackTiles = new AttackTile[ATTACK_PATTERN_SIZE][];
        for (int x = 0; x < ATTACK_PATTERN_SIZE; x ++) {
            attackTiles[x] = new AttackTile[ATTACK_PATTERN_SIZE];
            for (int y = 0; y < ATTACK_PATTERN_SIZE; y ++) {
                attackTiles[x][y] = new AttackTile(setup.getAttackPattern()[x][y], setup.getTerrainPattern()[x][y]);
            }
        }
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

    public int getPlayerNum() {
        return playerNum;
    }

    /**
     * Does the death of the unit
     *
     * @return true if the unit just died, false otherwise
     */
    boolean shouldDie() {
        return hp <= 0;
    }

    void takeCollisionDamage() {
        hp -= COLLISION_DAMAGE;
    }

    public void takeDamage(int damage) {
        hp -= damage;
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
        int width = attackTiles.length;
        int height = attackTiles[0].length;

        if (dir == Direction.LEFT) {
            AttackTile[][] ret = new AttackTile[height][width];

            for (int y = 0; y < ret[0].length; y++) {
                for (int x = 0; x < ret.length; x++) {
                    ret[x][y] = attackTiles[y][width - x - 1];
                }
            }
            return ret;
        } else if (dir == Direction.DOWN) {
            AttackTile[][] ret = new AttackTile[width][height];

            for (int y = 0; y < ret[0].length; y++) {
                for (int x = 0; x < ret.length; x++) {
                    ret[x][y] = attackTiles[width - x - 1][height - y - 1];
                }
            }
            return ret;
        } else if (dir == Direction.RIGHT) {
            AttackTile[][] ret = new AttackTile[height][width];

            for (int y = 0; y < ret[0].length; y++) {
                for (int x = 0; x < ret.length; x++) {
                    ret[x][y] = attackTiles[height - y - 1][x];
                }
            }
            return ret;
        } else if (dir == Direction.UP) {
            AttackTile[][] ret = new AttackTile[height][width];

            for (int y = 0; y < ret[0].length; y++) {
                for (int x = 0; x < ret.length; x++) {
                    ret[x][y] = attackTiles[x][y];
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
