package mech.mania.playerCommunication;

import mech.mania.Direction;
import mech.mania.Unit;

import java.util.*;

public class UnitDecision {
    public static final Collection<Integer> validPriorities = Arrays.asList(1, 2, 3);

    private int priority;
    private List<Direction> movement;
    private Direction attack;
    private int unitId;

    public UnitDecision(int aPriority, int aUnitId, Direction aAttack, List<Direction> aMovement) {
        priority = aPriority;
        unitId = aUnitId;
        attack = aAttack;
        movement = aMovement;
    }

    public int getUnitId() {
        return unitId;
    }

    public int getPriority() {
        return priority;
    }

    public List<Direction> getMovement() {
        return movement;
    }

    public Direction getAttack() {
        return attack;
    }

    public static boolean isValidDecisionList(List<UnitDecision> decisions, List<Unit> units) {
        if (decisions == null || decisions.size() != units.size()) {
            return false;
        }

        Collection<Integer> usedPriorities = new HashSet<>();
        for (UnitDecision decision : decisions) {
            if (usedPriorities.contains(decision.priority) || !validPriorities.contains(decision.priority)) {
                return false;
            }
            usedPriorities.add(decision.priority);

            Optional<Unit> unit = units.stream().filter(u -> u.getId() == decision.getUnitId()).findAny();
            if (!unit.isPresent()) {
                return false;
            } else if (!isValidDecision(decision, unit.get())) {
                return false;
            }
        }

        return true;
    }

    private static boolean isValidDecision(UnitDecision decision, Unit unit) {
        return (decision != null
                && decision.movement != null
                && decision.attack != null
                && decision.movement.stream().noneMatch(Objects::isNull)
                && decision.movement.size() == unit.getSpeed());
    }
}
