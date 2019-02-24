package mech.mania;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Stores the state of the game, as well as handling much of the logic during each turn
 */
public class Game {
    private Map map; // current map
    private Unit[] p1Units; // array of Player 1's units
    private Unit[] p2Units; // array of Player 2's units

    private static Unit[] initUnitList(Position[] positions) {
        Unit[] ret = new Unit[positions.length];
        for (int i = 0; i < positions.length; i ++) {
            ret[i] = new Unit(positions[i]);
        }
        return ret;
    }

    public Game(int boardSize, Position[] p1Positions, Position[] p2Positions/*TODO: add parameters (attack patterns?)*/) {
        p1Units = initUnitList(p1Positions);
        p2Units = initUnitList(p2Positions);

        map = new Map(boardSize);
    }

    private static boolean hasLiveUnit(Unit[] units){
        for (Unit u : units) {
            if (u.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public int getWinner() {
        if (hasLiveUnit(p1Units)) {
            if (hasLiveUnit(p2Units)) {
                return NO_WINNER;
            } else {
                return P1_WINNER;
            }
        } else { // Player 1 has no live units
            if (hasLiveUnit(p2Units)) {
                return P2_WINNER;
            } else {
                return TIE;
            }
        }
    }

    public void doTurn(Decision p1Decision, Decision p2Decision) {
        for (int priority = 1; priority <= 3; priority ++) {
            ArrayList<Unit> unitsToMove = new ArrayList<>();
            ArrayList<Direction[]> movements = new ArrayList<>();
            ArrayList<Direction> attackDirections = new ArrayList<>();


            for (int unitNum = 0; unitNum < 3; unitNum ++) {
                if (p1Decision.getPriorities()[unitNum] == priority) {
                    unitsToMove.add(p1Units[unitNum]);
                    movements.add(p1Decision.getMovements()[unitNum]);
                    attackDirections.add(p1Decision.getAttacks()[unitNum]);
                }

                if (p2Decision.getPriorities()[unitNum] == priority) {
                    unitsToMove.add(p2Units[unitNum]);
                    movements.add(p2Decision.getMovements()[unitNum]);
                    attackDirections.add(p2Decision.getAttacks()[unitNum]);
                }
            }

            doRound(unitsToMove, movements, attackDirections);
        }
    }

    private void doRound(List<Unit> units, List<Direction[]> movements, List<Direction> attackDirections) {
        int numSteps = 0;
        for (int i = 0; i < movements.size(); i ++) {
            if (movements.get(i).length > numSteps) {
                numSteps = movements.get(i).length;
            }
        }

        // TODO: check that movement arrays are equal to each bot's speed

        // TODO: have bots stop moving after a collision
        for (int stepNum = 0; stepNum < numSteps; stepNum ++) {
            List<Direction> stepDirections = new ArrayList<>(units.size());

            for (int unitNum = 0; unitNum < units.size(); unitNum ++) {
                if (movements.get(unitNum).length < stepNum) {
                    stepDirections.add(unitNum, Direction.STAY);
                } else {
                    stepDirections.add(unitNum, movements.get(unitNum)[stepNum]);
                }
            }

            doMovementStep(units, stepDirections);
        }
        //TODO: death

        //TODO: attacks
    }

    private void doMovementStep(List<Unit> units, List<Direction> directions) {
        // list of each unit's initial position
        List<Position> initialPositions = new ArrayList<>(units.size());
        for (int i = 0; i < units.size(); i ++) {
            initialPositions.add(i, units.get(i).getPos());
        }

        // list of where each unit is trying to go
        List<Position> goalPositions = new ArrayList<>(units.size());
        for (int i = 0; i < initialPositions.size(); i ++) {
            goalPositions.add(i, initialPositions.get(i).getNewPosition(directions.get(i)));
        }

        // list of where the units will actually end up (will be its goalPosition or its initialPosition)
        List<Position> finalPositions = Arrays.asList(new Position[10]);
        // this array will be filled first with the positions of colliding units, then the rest

        // handle collisions between units and terrain (or the map boundary)
        for (int i = 0; i < goalPositions.size(); i ++) {
            if (! inBounds(goalPositions.get(i)) || map.tileAt(goalPositions.get(i)).getType() != Tile.Type.BLANK) {
                System.out.println("Terrain Collision");

                finalPositions.set(i, initialPositions.get(i));
                units.get(i).takeCollisionDamage();
            } else if (map.tileAt(goalPositions.get(i)).getUnit() != null) {
                System.out.println("Stationary Unit Collision");

                // handle collision with stationary unit
                boolean isMoving = false;
                for (Unit moving : units) {
                    if (moving == map.tileAt(goalPositions.get(i)).getUnit()) {
                        isMoving = true;
                        break;
                    }
                }

                if (!isMoving) {
                    finalPositions.set(i, initialPositions.get(i));
                    units.get(i).takeCollisionDamage();
                    map.tileAt(goalPositions.get(i)).getUnit().takeCollisionDamage();
                }
            }
        }

        // handle collisions between two units
        for (int i = 0; i < goalPositions.size(); i ++) {
            for (int j = i+1; j < goalPositions.size(); j ++) {
                if (goalPositions.get(i).equals(goalPositions.get(j)) || // two units moving onto the same tile
                        (goalPositions.get(i).equals(initialPositions.get(j)) && // two units trying to move through each other
                        goalPositions.get(j).equals(initialPositions.get(i)))) {
                    finalPositions.set(i, initialPositions.get(i));
                    units.get(i).takeCollisionDamage();
                    finalPositions.set(j, initialPositions.get(j));
                    units.get(j).takeCollisionDamage();
                }
            }
        }

        // handle 'ripple collisions'
        boolean foundRipple;
        do {
            foundRipple = false;
            for (int i = 0; i < goalPositions.size(); i++) {
                if (finalPositions.get(i) == null) { // only check for ripple collisions for bots that haven't already collided
                    for (int j = 0; j < goalPositions.size(); j++) {
                        if (finalPositions.get(j) != null && finalPositions.get(j).equals(goalPositions.get(i))) {
                            System.out.println("Ripple Collision");
                            foundRipple = true;
                            finalPositions.set(i, initialPositions.get(i));
                            units.get(i).takeCollisionDamage();
                            units.get(j).takeCollisionDamage();
                        }
                    }
                }
            }
        } while (foundRipple);

        // set remaining final positions to the goal positions
        for (int i = 0; i < units.size(); i ++) {
            if (finalPositions.get(i) == null) {
                finalPositions.set(i, goalPositions.get(i));
            }
        }

        // move the units
        for (int i = 0; i < units.size(); i ++) {
            if (! finalPositions.get(i).equals(initialPositions.get(i))) {
                moveUnit(units.get(i), initialPositions.get(i), finalPositions.get(i));
            }
        }
    }

    private boolean inBounds(Position pos) {
        return (pos.x >= 0 && pos.x < map.width() && pos.y >= 0 && pos.y < map.height());
    }

    private void moveUnit(Unit unit, Position initial, Position dest) {
        unit.setPos(dest);
        map.tileAt(initial).setUnit(null);
        map.tileAt(dest).setUnit(unit);
    }

    public String getMapString() {
        return map.toString();
    }

    public static final int P1_WINNER = 0;
    public static final int P2_WINNER = 1;
    public static final int TIE = 2;
    public static final int NO_WINNER = 3;
}
