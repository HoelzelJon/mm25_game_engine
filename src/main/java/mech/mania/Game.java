package mech.mania;

import com.google.gson.*;
import mech.mania.playerCommunication.Decision;
import mech.mania.playerCommunication.UnitSetup;
import mech.mania.visualizer.perTurn.MovementRepresentation;
import mech.mania.visualizer.perTurn.MovementType;
import mech.mania.visualizer.perTurn.RoundRepresentation;
import mech.mania.visualizer.perTurn.TurnRepresentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Stores the state of the game, as well as handling much of the logic during each turn
 */
public class Game {
    public static final int UNITS_PER_PLAYER = 3;

    private Board map; // current map
    private Unit[] p1Units; // array of Player 1's units
    private Unit[] p2Units; // array of Player 2's units
    private String gameId;
    private String[] playerNames;
    private int turnsTaken;

    private Gson gameStateSerializer;

    private static Unit[] initUnitList(UnitSetup[] setups, int playerNum, Board map) {
        List<UninitializedUnit> nonSetupUnits = map.getInitialUnits(playerNum);

        Unit[] ret = new Unit[nonSetupUnits.size()];
        for (int i = 0; i < ret.length; i ++) {
            ret[i] = new Unit(nonSetupUnits.get(i), setups[i]);
            map.tileAt(nonSetupUnits.get(i).getPos()).setUnit(ret[i]);
        }
        return ret;
    }

    public Game(String id,
                String player1Name,
                String player2Name,
                UnitSetup[] p1UnitSetups,
                UnitSetup[] p2UnitSetups,
                Board map) {
        this.playerNames = new String[] {player1Name, player2Name};
        this.gameId = id;
        this.map = map;
        turnsTaken = 0;

        p1Units = initUnitList(p1UnitSetups, 1, map);
        p2Units = initUnitList(p2UnitSetups, 2, map);

        gameStateSerializer = new GsonBuilder().addSerializationExclusionStrategy(
            new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    if (fieldAttributes.getDeclaringClass() == Game.class) {
                        return fieldAttributes.getName().contains("Serializer") ||
                                fieldAttributes.getName().equals("recentRounds");
                    }

                    return false;
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            }).create();
    }

    /**
     * @param units array of units to check
     * @return true if any of the units in the array are alive
     */
    private static boolean hasLiveUnit(Unit[] units){
        return Arrays.stream(units).anyMatch(Unit::isAlive);
    }

    /**
     * @return P1_WINNER or P2_WINNER, if the other player's bots are all dead
     *         TIE if all bots are dead
     *         NO_WINNER if there are still live bots for each player
     */
    int getWinner() {
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

    /**
     * Implements all the game logic for one full turn
     *
     * @param p1Decision the decision for player 1 to take
     * @param p2Decision the decision for player 2 to take
     */
    TurnRepresentation doTurn(Decision p1Decision, Decision p2Decision) {
        turnsTaken ++;
        TurnRepresentation turnRepresentation = new TurnRepresentation(turnsTaken);

        for (int priority = 1; priority <= UNITS_PER_PLAYER; priority ++) {
            ArrayList<Unit> unitsToMove = new ArrayList<>();
            ArrayList<Direction[]> movements = new ArrayList<>();
            ArrayList<Direction> attackDirections = new ArrayList<>();

            for (int unitNum = 0; unitNum < UNITS_PER_PLAYER; unitNum ++) {
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


            turnRepresentation.addRound(doRound(priority, unitsToMove, movements, attackDirections));
        }

        return turnRepresentation;
    }

    /**
     * Helper function for doTurn -- does all game logic for one round
     *
     * @param units the units involved in this round (should all have same priority)
     * @param movements movements.get(i) is an array representing the movement of units.get(i)
     * @param attackDirections  the direction for each bot's attack
     */
    private RoundRepresentation doRound(int roundIndex, List<Unit> units, List<Direction[]> movements, List<Direction> attackDirections) {
        RoundRepresentation roundRepresentation = new RoundRepresentation(roundIndex);

        int numMovementSteps = 0;
        for (Direction[] unitMovements : movements) {
            if (unitMovements.length > numMovementSteps) {
                numMovementSteps = unitMovements.length;
            }
        }

        List<Integer> botIdsThatCollidedThisRound = new ArrayList<>();

        for (int stepNum = 0; stepNum < numMovementSteps; stepNum ++) {
            List<Direction> stepDirections = new ArrayList<>();

            for (int unitNum = 0; unitNum < units.size(); unitNum ++) {
                if (movements.get(unitNum).length > stepNum
                        && units.get(unitNum).getSpeed() > stepNum
                        && !botIdsThatCollidedThisRound.contains(units.get(unitNum).getId())) {
                    stepDirections.add(unitNum, movements.get(unitNum)[stepNum]);
                } else {
                    stepDirections.add(unitNum, Direction.STAY);
                }
            }

            List<MovementRepresentation> movementStepRepresentation = doMovementStep(units, stepDirections);
            roundRepresentation.addMovementStep(movementStepRepresentation);

            for (MovementRepresentation move : movementStepRepresentation) {
                if (move.getMovementType() == MovementType.Collision) {
                    botIdsThatCollidedThisRound.add(move.getBotId());
                }
            }

            doDeaths();
        }

        roundRepresentation.addAttacks(map.doAttacks(units, attackDirections));

        doDeaths();

        return roundRepresentation;
    }

    /**
     * Does a single movement step for some set of units
     *
     * @param units         a list of units to move
     * @param directions    the directions to move each unit (i.e. units.get(i) should move in direction directions.get(i))
     * @return a boolean array indicating whether each unit has collided during this movement step
     */
    private List<MovementRepresentation> doMovementStep(List<Unit> units,
                                                  List<Direction> directions) {
        List<MovementRepresentation> movementRepresentation = new ArrayList<>();
        for (int unitIndex = 0; unitIndex < units.size(); unitIndex ++) {
            movementRepresentation.add(unitIndex, new MovementRepresentation(units.get(unitIndex).getId(), directions.get(unitIndex)));
        }

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

        // list of whether a given unit has collided during this movement step
        boolean[] collided = new boolean[units.size()];

        // list of terrain tiles to damage through collisions
        List<Tile> collidedTiles = new ArrayList<>();

        // handle collisions between units and terrain (or the map boundary)
        for (int i = 0; i < goalPositions.size(); i ++) {
            if (! map.inBounds(goalPositions.get(i)) || map.tileAt(goalPositions.get(i)).getType() != Tile.Type.BLANK) {
                collided[i] = true;
                units.get(i).takeCollisionDamage();

                // add to game log
                movementRepresentation.get(i).setCollision(goalPositions.get(i));

                if (map.inBounds(goalPositions.get(i))) {
                    // add the terrain to the list of terrain to damage
                    collidedTiles.add(map.tileAt(goalPositions.get(i)));
                }
            } else if (map.tileAt(goalPositions.get(i)).getUnit() != null) {
                // handle collision with stationary unit
                boolean isMoving = units.contains(map.tileAt(goalPositions.get(i)).getUnit());

                if (!isMoving) {
                    collided[i] = true;
                    units.get(i).takeCollisionDamage();
                    map.tileAt(goalPositions.get(i)).getUnit().takeCollisionDamage();

                    // add to game log
                    movementRepresentation.get(i).setCollision(goalPositions.get(i));
                }
            }
        }

        // handle collisions between two units
        for (int i = 0; i < goalPositions.size(); i ++) {
            for (int j = i+1; j < goalPositions.size(); j ++) {
                if (!collided[i] && !collided[j] &&
                        (goalPositions.get(i).equals(goalPositions.get(j)) || // two units moving onto the same tile
                        (goalPositions.get(i).equals(initialPositions.get(j)) && // two units trying to move through each other
                        goalPositions.get(j).equals(initialPositions.get(i))))) {
                    collided[i] = true;
                    units.get(i).takeCollisionDamage();
                    collided[j] = true;
                    units.get(j).takeCollisionDamage();

                    // add to game log
                    movementRepresentation.get(i).setCollision(goalPositions.get(i));
                    movementRepresentation.get(j).setCollision(goalPositions.get(j));
                }
            }
        }

        // handle 'ripple collisions'
        boolean foundRipple;
        do {
            foundRipple = false;
            for (int i = 0; i < goalPositions.size(); i++) {
                if (!collided[i]) { // only check for ripple collisions for bots that haven't already collided
                    for (int j = 0; j < goalPositions.size(); j++) {
                        if (collided[j] && initialPositions.get(j).equals(goalPositions.get(i))) {
                            foundRipple = true;
                            collided[i] = true;
                            units.get(i).takeCollisionDamage();
                            units.get(j).takeCollisionDamage();

                            // add to game log
                            movementRepresentation.get(i).setCollision(goalPositions.get(i));
                            movementRepresentation.get(j).setCollision(goalPositions.get(j));
                            break;
                        }
                    }
                }
            }
        } while (foundRipple);

        // damage each collided tile
        for (Tile t : collidedTiles) {
            t.collided();
        }

        // move the units
        List<Unit> moving = new ArrayList<>();
        List<Position> destinations = new ArrayList<>();
        for (int i = 0; i < units.size(); i ++) {
            if (!collided[i]) {
                moving.add(units.get(i));
                destinations.add(goalPositions.get(i));
            }
        }

        map.moveUnits(moving, destinations);

        return movementRepresentation;
    }

    /**
     * For any dead units, removes them from the board
     */
    private List<Integer> doDeaths() {
        List<Integer> ret = new ArrayList<>();
        ret.addAll(doDeaths(p1Units));
        ret.addAll(doDeaths(p2Units));
        return ret;
    }

    /**
     * handles death for an array of units
     * @param units array of units to check death conditions for
     */
    private List<Integer> doDeaths(Unit[] units) {
        List<Integer> ret = new ArrayList<>();
        for (Unit u : units) {
            if (u.isAlive()) {
                Position oldPos = u.getPos();

                if (u.doDeath()) {
                    map.tileAt(oldPos).setUnit(null);
                    ret.add(u.getId());
                }
            }
        }
        return ret;
    }

    public String getMapString() {
        return map.toString();
    }

    public String getUnitStatsString(){
        StringBuilder ret = new StringBuilder();

        ret.append("Player 1 Unit Stats:\tPlayer 2 Unit Stats:\n");

        for (int i = 0; i < p1Units.length; i++) {
            if (p1Units[i].isAlive()) {
                ret.append(p1Units[i].getId() + ": hp = " + p1Units[i].getHp() + "\t\t\t\t");
            } else {
                ret.append("        \t\t\t\t");
            }

            if (p2Units[i].isAlive()) {
                ret.append(p2Units[i].getId() + ": hp = " + p2Units[i].getHp() + "\n");
            } else {
                ret.append("        \n");
            }
        }

        return ret.toString();
    }

    public Unit[] getPlayerUnits(int playerNum){
        if(playerNum == 1){
            return p1Units;
        }
        else if (playerNum == 2){
            return p2Units;
        }
        else{
            return null;
        }
    }

    public String getPlayerName(int playerNum) {
        if (playerNum > 0 && playerNum <= 2) {
            return playerNames[playerNum-1];
        } else {
            return "";
        }
    }

    public static final int P1_WINNER = 0;
    public static final int P2_WINNER = 1;
    public static final int TIE = 2;
    public static final int NO_WINNER = 3;

    public String getInitialVisualizerJson() {
        return gameStateSerializer.toJson(this);
    }

    public String getRecentPlayerJson() {
        return gameStateSerializer.toJson(this);
    }
}
