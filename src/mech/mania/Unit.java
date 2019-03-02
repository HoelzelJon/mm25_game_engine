package mech.mania;

/**
 * Represents a single mech.
 */
public class Unit {
    private int hp; // unit's current health
    private int speed; // unit's speed (number of tiles it can move per turn)
    private Position pos; // position of the unit
    private int[][] attack; // 2-D grid of attack damages

    public Unit(Position pos, int[][] attack) {
        hp = 2;
        speed = 4;
        this.pos = pos;
        this.attack = attack;
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

    //TODO: only mark units as dead once doDeaths() is called in Game
    public boolean isAlive() {
        return (hp > 0);
    }

    public void takeCollisionDamage() {
        hp --;
    }

    /**
     * Returns a rotated version of this unit's attack
     * if dir == UP or dir == STAY then returns unaltered attack //TODO: change STAY to return all zeros?
     * if dir == RIGHT then returns copy of attack, rotated 90 degrees clockwise
     * if dir == DOWN then returns copy of attack, rotated 180 degrees
     * if dir == LEFT then returns copy of attack, rotated 90 degrees counterclockwise
     *
     * @param dir direction the attack is facing
     * @return this unit's attack, rotated in direction dir
     */
    public int[][] getAttack(Direction dir) {
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
        }

        return attack;
    }
}
