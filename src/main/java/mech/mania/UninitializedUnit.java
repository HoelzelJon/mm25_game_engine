package mech.mania;

public class UninitializedUnit {
    private int unitId;
    private Position pos;
    private int playerNum; // 1 or 2

    public UninitializedUnit(int aUnitId, int aPlayerNum, Position aPos) {
        unitId = aUnitId;
        playerNum = aPlayerNum;
        pos = aPos;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public Position getPos() {
        return pos;
    }

    public int getUnitId() {
        return unitId;
    }
}
