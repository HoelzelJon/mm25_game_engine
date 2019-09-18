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

    public static void throwExceptionOnInvalidDecisionList(List<UnitDecision> decisions, List<Unit> units) throws InvalidDecisionException {
        if (decisions == null) {
            throw new InvalidDecisionException("Decision list is null");
        } else if (decisions.size() != units.size()) {
            throw new InvalidDecisionException("Size of decisions list is incorrect. Expected " + units.size() + ", got " + decisions.size());
        }

        Collection<Integer> usedPriorities = new HashSet<>();
        for (UnitDecision decision : decisions) {
            if (decision == null) {
                throw new InvalidDecisionException("Null decision found in decision list");
            } else if (usedPriorities.contains(decision.priority)) {
                throw new InvalidDecisionException("Used same priority for two different units: " + decision.priority);
            } else if (!validPriorities.contains(decision.priority)) {
                throw new InvalidDecisionException("Invalid priority chosen: unitId = " + decision.unitId + ", priority = " + decision.priority);
            }
            usedPriorities.add(decision.priority);

            Optional<Unit> unit = units.stream().filter(u -> u.getId() == decision.getUnitId()).findAny();
            if (!unit.isPresent()) {
                throw new InvalidDecisionException("Specified invalid unitId in decision: " + decision.unitId);
            } else {
                throwExceptionOnInvalidDecision(decision, unit.get()); // TODO: catch and re-throw to add unitId in exception message
            }
        }
    }

    private static void throwExceptionOnInvalidDecision(UnitDecision decision, Unit unit) throws InvalidDecisionException {
        if (decision == null) {
            throw new InvalidDecisionException("Null decision found");
        } else if (decision.movement == null) {
            throw new InvalidDecisionException("Decision contains null movement array");
        } else if (decision.attack == null) {
            throw new InvalidDecisionException("Decision contains null attack choice");
        } else if (decision.movement.stream().noneMatch(Objects::isNull)) {
            throw new InvalidDecisionException("Decision contains null element in movement array");
        } else if (decision.movement.size() != unit.getSpeed()) {
            throw new InvalidDecisionException("Decision movement array is incorrect length");
        }
    }
}
