package mech.mania;

import mech.mania.playerCommunication.UnitDecision;
import mech.mania.playerCommunication.UnitSetup;
import mech.mania.visualizer.perTurn.MovementRepresentation;
import mech.mania.visualizer.perTurn.MovementType;
import mech.mania.visualizer.perTurn.RoundRepresentation;
import mech.mania.visualizer.perTurn.TurnRepresentation;

import java.util.*;
import java.util.stream.Collectors;

import static mech.mania.Winner.*;

/**
 * Stores the state of the game, as well as handling much of the logic during each turn
 */
public class Game {
    // stores the turn number to do the shrinking, along with the
    private static final List<Integer> TURN_NUMBERS_TO_SHRINK_BOARD = Arrays.asList(15, 20, 24, 27, 29, 30);
    public static final int UNITS_PER_PLAYER = 3;

    private Board board; // current board
    private List<Unit> p1Units; // array of Player 1's units
    private List<Unit> p2Units; // array of Player 2's units
    private String gameId;
    private String[] playerNames;
    private int turnNumber;
    private int borderSize;

    private static List<Unit> initUnitList(List<UnitSetup> setups, int playerNum, Board board) {
        List<Unit> ret = new ArrayList<>();
        for (UninitializedUnit tempUnit : board.getInitialUnits(playerNum)) {
            Unit newUnit = new Unit(tempUnit, setups.stream().filter(u -> u.getUnitId() == tempUnit.getUnitId()).findAny().get());
            ret.add(newUnit);
            board.tileAt(tempUnit.getPos()).setUnit(newUnit);
        }

        return ret;
    }

    public Game(String id,
                String player1Name,
                String player2Name,
                List<UnitSetup> p1UnitSetups,
                List<UnitSetup> p2UnitSetups,
                Board board) {
        this.playerNames = new String[] {player1Name, player2Name};
        this.gameId = id;
        this.board = board;
        turnNumber = 0;
        borderSize = 0;

        p1Units = initUnitList(p1UnitSetups, 1, board);
        p2Units = initUnitList(p2UnitSetups, 2, board);
    }

    /**
     * @return P1_WINNER or P2_WINNER, if the other player's bots are all dead
     *         TIE if all bots are dead
     *         NO_WINNER if there are still live bots for each player
     */
    Winner getWinner() {
        if (!p1Units.isEmpty()) {
            if (!p2Units.isEmpty()) {
                return NO_WINNER;
            } else {
                return P1_WINNER;
            }
        } else { // Player 1 has no live units
            if (!p2Units.isEmpty()) {
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
    TurnRepresentation doTurn(List<UnitDecision> p1Decision, List<UnitDecision> p2Decision) {
        turnNumber++;
        TurnRepresentation turnRepresentation = new TurnRepresentation(turnNumber);

        for (int priority = 1; priority <= UNITS_PER_PLAYER; priority ++) {
            List<Unit> unitsToActThisRound = new ArrayList<>();
            List<UnitDecision> unitDecisionsThisRound = new ArrayList<>();

            addUnitsAndDecisionsByPriority(unitsToActThisRound, unitDecisionsThisRound, priority, p1Units, p1Decision);
            addUnitsAndDecisionsByPriority(unitsToActThisRound, unitDecisionsThisRound, priority, p2Units, p2Decision);

            turnRepresentation.addRound(doRound(priority, unitsToActThisRound, unitDecisionsThisRound));
        }

        if (borderSize < TURN_NUMBERS_TO_SHRINK_BOARD.size() && turnNumber == TURN_NUMBERS_TO_SHRINK_BOARD.get(borderSize)) {
            board.addBorder(borderSize, turnRepresentation);
            borderSize ++;

            // if any units died, remove them from the players' unit lists
            for (Integer unitId : turnRepresentation.getUnitsKilledByOuterWall()) {
                p1Units.removeIf(u -> u.getId() == unitId);
                p2Units.removeIf(u -> u.getId() == unitId);
            }
        }

        return turnRepresentation;
    }

    private static void addUnitsAndDecisionsByPriority(List<Unit> unitListToAddTo,
                                                       List<UnitDecision> decisionListToAddTo,
                                                       int priority,
                                                       List<Unit> unitListToQuery,
                                                       List<UnitDecision> decisionListToQuery) {
        Optional<UnitDecision> unitDecision = decisionListToQuery.stream().filter(dec -> dec.getPriority() == priority).findFirst();
        if (unitDecision.isPresent()) { // list may not contain the priority if a unit has died already
            Optional<Unit> unitToAdd = unitListToQuery.stream().filter(u -> u.getId() == unitDecision.get().getUnitId()).findFirst();

            if (unitToAdd.isPresent()) { // unit may not exist if it died in a previous round of this turn
                unitListToAddTo.add(unitToAdd.get());
                decisionListToAddTo.add(unitDecision.get());
            }
        }
    }

    /**
     * Helper function for doTurn -- does all game logic for one round
     *
     * @param units the units involved in this round
     * @param decisions the decision that each unit in the array should take
     */
    private RoundRepresentation doRound(int roundIndex, List<Unit> units, List<UnitDecision> decisions) {
        RoundRepresentation roundRepresentation = new RoundRepresentation(roundIndex);

        int numMovementSteps = 0;
        for (Unit u : units) {
            if (u.getSpeed() > numMovementSteps) {
                numMovementSteps = u.getSpeed();
            }
        }

        List<Integer> botIdsThatCollidedThisRound = new ArrayList<>();

        for (int stepNum = 0; stepNum < numMovementSteps; stepNum ++) {
            List<Direction> stepDirections = new ArrayList<>();

            for (int unitNum = 0; unitNum < units.size(); unitNum ++) {
                if (units.get(unitNum).getSpeed() > stepNum
                        && !botIdsThatCollidedThisRound.contains(units.get(unitNum).getId())) {
                    stepDirections.add(unitNum, decisions.get(unitNum).getMovement().get(stepNum));
                } else {
                    // have the non-moving bot do a 'STAY' movement, as visualizer requested
                    stepDirections.add(unitNum, Direction.STAY);
                }
            }

            List<MovementRepresentation> movementStepRepresentation = doMovementStep(units, stepDirections);
            if (!movementStepRepresentation.isEmpty()) {
                roundRepresentation.addMovementStep(movementStepRepresentation);
            }

            for (MovementRepresentation move : movementStepRepresentation) {
                if (move.getMovementType() == MovementType.Collision) {
                    botIdsThatCollidedThisRound.add(move.getBotId());
                }
            }

            List<Integer> deadUnitIds = doDeaths();
            units.removeIf(u -> deadUnitIds.contains(u.getId()));
        }

        roundRepresentation.addAttacks(
                board.doAttacks(
                        units,
                        decisions.stream().map(UnitDecision::getAttack).collect(Collectors.toList())));

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

        // handle collisions between units and terrain (or the board boundary)
        for (int i = 0; i < goalPositions.size(); i ++) {
            if (! board.inBounds(goalPositions.get(i)) || board.tileAt(goalPositions.get(i)).getType() != Tile.Type.BLANK) {
                collided[i] = true;
                units.get(i).takeCollisionDamage();


                if (board.inBounds(goalPositions.get(i))) {
                    // add the terrain to the list of terrain to damage
                    collidedTiles.add(board.tileAt(goalPositions.get(i)));
                    movementRepresentation.get(i).collidedWithTerrain(goalPositions.get(i),
                            board.tileAt(goalPositions.get(i)).getHp() - 1,
                            units.get(i).getHp());
                } else {
                    // add to game log
                    movementRepresentation.get(i).collidedWithTerrain(goalPositions.get(i),
                            -1,
                            units.get(i).getHp());
                }
            } else if (board.tileAt(goalPositions.get(i)).getUnit() != null) {
                // handle collision with stationary unit
                boolean isMoving = units.contains(board.tileAt(goalPositions.get(i)).getUnit());

                if (!isMoving) {
                    collided[i] = true;
                    units.get(i).takeCollisionDamage();
                    board.tileAt(goalPositions.get(i)).getUnit().takeCollisionDamage();

                    // add to game log
                    movementRepresentation.get(i).collidedWithUnit(goalPositions.get(i),
                            board.tileAt(goalPositions.get(i)).getUnit().getHp(),
                            units.get(i).getHp(),
                            board.tileAt(goalPositions.get(i)).getUnit().getId());
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
                    movementRepresentation.get(i).collidedWithUnit(goalPositions.get(i),
                            units.get(j).getHp(),
                            units.get(i).getHp(),
                            units.get(j).getId());
                    movementRepresentation.get(j).collidedWithUnit(goalPositions.get(j),
                            units.get(i).getHp(),
                            units.get(j).getHp(),
                            units.get(i).getId());
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
                            movementRepresentation.get(i).collidedWithUnit(goalPositions.get(i),
                                    units.get(j).getHp(),
                                    units.get(i).getHp(),
                                    units.get(j).getId());
                            movementRepresentation.get(j).collidedWithUnit(goalPositions.get(j),
                                    units.get(i).getHp(),
                                    units.get(j).getHp(),
                                    units.get(i).getId());
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

        board.moveUnits(moving, destinations);

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
    private List<Integer> doDeaths(List<Unit> units) {
        List<Unit> unitsToRemove = new ArrayList<>();
        for (Unit u : units) {
            if (u.shouldDie()) {
                board.tileAt(u.getPos()).setUnit(null);
                unitsToRemove.add(u);
            }
        }

        units.removeAll(unitsToRemove);
        return unitsToRemove.stream().map(Unit::getId).collect(Collectors.toList());
    }

    public String getGameId() {
        return gameId;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public Board getBoard() {
        return board;
    }

    public String getBoardString() {
        return board.toString();
    }

    public String getUnitStatsString(){
        StringBuilder ret = new StringBuilder();

        ret.append("Player 1 Unit Stats:\tPlayer 2 Unit Stats:\n");

        for (int i = 0; i < Math.max(p1Units.size(), p2Units.size()); i++) {
            if (p1Units.size() > i) {
                ret.append("*" + p1Units.get(i).getId() + ": hp = " + p1Units.get(i).getHp() + "\t\t");
            } else {
                ret.append("          \t\t");
            }

            if (p2Units.size() > i) {
                ret.append("*" + p2Units.get(i).getId() + ": hp = " + p2Units.get(i).getHp() + "\n");
            } else {
                ret.append("          \n");
            }
        }

        return ret.toString();
    }

    public List<Unit> getPlayerUnits(int playerNum){
        if (playerNum == 1){
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
}
