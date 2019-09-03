package mech.mania;

/**
 * Represents a single mech.
 */
public class Unit implements Damageable {
    private int hp; // unit's current health
    private int speed; // unit's speed (number of tiles it can move per turn)
    private Position pos; // position of the unit
    private int[][] attack; // 2-D grid of attack damages
    private boolean isAlive;
    static final int COLLISION_DAMAGE = 1;
    private int id;

    Unit(Position setPosition, UnitSetup setup) {
        id = Game.GLOBAL_ID++;
        hp = setup.getHealth();
        speed = setup.getSpeed();
        pos = setPosition;
        attack = setup.getAttackPattern();
        isAlive = true;
    }

    int getId() {
        return id;
    }

    int getHp() {
        return hp;
    }

    int getSpeed() {
        return speed;
    }

    Position getPos() {
        return pos;
    }

    void setPos(Position pos) {
        this.pos = pos;
    }

    boolean isAlive() {
        return isAlive;
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

    @Override
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
    int[][] getAttack(Direction dir) {
        int width = attack.length;
        int height = attack[0].length;

        if (dir == Direction.LEFT) {
            int[][] ret = new int[height][width];

            for (int y = 0; y < ret[0].length; y ++) {
                for (int x = 0; x < ret.length; x ++) {
                    ret[x][y] = attack[y][width - x - 1];
                }
            }
            return ret;
        } else if (dir == Direction.DOWN) {
            int[][] ret = new int[width][height];

            for (int y = 0; y < ret[0].length; y ++) {
                for (int x = 0; x < ret.length; x ++) {
                    ret[x][y] = attack[width - x - 1][height - y - 1];
                }
            }
            return ret;
        } else if (dir == Direction.RIGHT) {
            int[][] ret = new int[height][width];

            for (int y = 0; y < ret[0].length; y ++) {
                for (int x = 0; x < ret.length; x ++) {
                    ret[x][y] = attack[height - y - 1][x];
                }
            }
            return ret;
        } else if (dir == Direction.STAY) {
            return new int[width][height];
        }

        return attack;
    }
}
