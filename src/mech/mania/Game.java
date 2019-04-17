package mech.mania;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stores the state of the game, as well as handling much of the logic during each turn
 */
public class Game {
    private Map map; // current map
    private Unit[] p1Units; // array of Player 1's units
    private Unit[] p2Units; // array of Player 2's units
    private String gameId;
    private String[] playerNames;

    private Gson gameRoundSerializer;
    private Gson gameStateSerializer;

    private GameRound recentRound;

    /**
     * @param positions array of positions for each unit to be initialized to
     * @param map       map to add the units onto
     * @return array of units
     */
    private static Unit[] initUnitList(Position[] positions, UnitSetup[] setups, Map map) {
        Unit[] ret = new Unit[positions.length];
        for (int i = 0; i < positions.length; i ++) {
            ret[i] = new Unit(positions[i], setups[i]);
            map.tileAt(positions[i]).setUnit(ret[i]);
        }
        return ret;
    }

    public Game(String setGameId, UnitSetup[] p1UnitSetups, UnitSetup[] p2UnitSetups) {

        gameId = setGameId;

        map = new Map();
        Position[] p1Positions = map.getP1InitialPositions();
        Position[] p2Positions = map.getP2InitialPositions();

        p1Units = initUnitList(p1Positions, p1UnitSetups, map);
        p2Units = initUnitList(p2Positions, p2UnitSetups, map);

        // add custom serializer to only serialize the ID section of Unit and Tile
        gameRoundSerializer = new GsonBuilder().addSerializationExclusionStrategy(
            new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    if (fieldAttributes.getDeclaringClass() == Unit.class) {
                        // if we are serializing the Unit class then only take the ID field
                        return !fieldAttributes.getName().equals("id");
                    }

                    else if (fieldAttributes.getDeclaringClass() == Tile.class) {
                        // if we are serializing the Tile class then only take the ID field
                        return !fieldAttributes.getName().equals("id");
                    }

                    return false;
                }

                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            }).create();

        gameStateSerializer = new GsonBuilder().addSerializationExclusionStrategy(
            new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    if (fieldAttributes.getDeclaringClass() == Game.class) {
                        return fieldAttributes.getName().contains("Serializer") ||
                                fieldAttributes.getName().equals("recentRound");
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
        for (Unit u : units) {
            if (u.isAlive()) {
                return true;
            }
        }
        return false;
    }



    /**
     * @return P1_WINNER or P2_WINNER, if the other player's bots are all dead
     *         TIE if all bots are dead
     *         NO_WINNER if there are still live bots for each player
     */
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

    /**
     * Implements all the game logic for one full turn
     *
     * @param p1Decision the decision for player 1 to take
     * @param p2Decision the decision for player 2 to take
     */
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

    /**
     * Helper function for doTurn -- does all game logic for one round
     *
     * @param units the units involved in this round (should all have same priority)
     * @param movements movements.get(i) is an array representing the movement of units.get(i)
     * @param attackDirections  the direction for each bot's attack
     */
    private void doRound(List<Unit> units, List<Direction[]> movements, List<Direction> attackDirections) {
        int maxBotSpeed = 0;
        for (int i = 0; i < movements.size(); i ++) {
            if (movements.get(i).length > maxBotSpeed) {
                maxBotSpeed = movements.get(i).length;
            }
        }

        List<RoundMovement> roundMovements = new ArrayList<>();


        for (int stepNum = 0; stepNum < maxBotSpeed; stepNum ++) {

            List<Direction> stepDirections = new ArrayList<>();

            for (int unitNum = 0; unitNum < units.size(); unitNum ++) {
                if (movements.get(unitNum).length <= stepNum || units.get(unitNum).getSpeed() <= stepNum) {
                    // if the movement array isn't large enough or if the unit's speed isn't high enough, don't let that unit move
                    stepDirections.add(unitNum, Direction.STAY);
                } else {
                    stepDirections.add(unitNum, movements.get(unitNum)[stepNum]);
                }
            }

            boolean[] collided = doMovementStep(units, stepDirections, roundMovements);

            // should stop units from moving after colliding in a round
            for (int unitNum = units.size() - 1; unitNum >= 0; unitNum --) {
                if (collided[unitNum]) {
                    units.remove(unitNum);
                    movements.remove(unitNum);
                }
            }
        }

        doDeaths();

        List<Attack> attacks = new ArrayList<>();
        List<DamagedTile> damagedTiles = new ArrayList<>();
        List<DamagedUnit> damagedUnits = new ArrayList<>();

        for (int unitNum = 0; unitNum < units.size(); unitNum ++) {
            if (units.get(unitNum).isAlive()) {
                HashMap<Object, Integer> affectedTiles =
                        map.doAttackDamage(units.get(unitNum).getAttack(attackDirections.get(unitNum)),
                                           units.get(unitNum).getPos());

                // loop through the payload, Pair of a Tile/Unit and a damage value
                // and link that with the unit that attacked in order to create DamagedUnit object
                for (java.util.Map.Entry<Object, Integer> affectedTile : affectedTiles.entrySet()) {
                    // two possibilities: the Object (key) is a Unit or a Tile
                    if (affectedTile.getKey() instanceof Unit) {
                        damagedUnits.add(new DamagedUnit(
                                units.get(unitNum),           // offender unit
                                (Unit) affectedTile.getKey(), // who was damaged
                                affectedTile.getValue()       // amount damaged for
                        ));
                    }

                    // if affected Object was a tile (no Unit standing on it)
                    else if (affectedTile.getKey() instanceof Tile) {
                        damagedTiles.add(new DamagedTile(
                                units.get(unitNum),           // who did damaging
                                (Tile) affectedTile.getKey(), // tile that was damaged
                                affectedTile.getValue()       // amount damaged for
                        ));
                    }
                }

                // STAY means that attack isn't executed
                if (attackDirections.get(unitNum) != Direction.STAY) {
                    attacks.add(new Attack(units.get(unitNum), attackDirections.get(unitNum)));
                }
            }
        }

        doDeaths();

        // create a container class that allows us to serialize to a JSON
        recentRound = new GameRound(
                roundMovements.toArray(new RoundMovement[0]),
                damagedTiles.toArray(new DamagedTile[0]),
                damagedUnits.toArray(new DamagedUnit[0]),
                attacks.toArray(new Attack[0]));
    }

    /**
     * Does a single movement step for some set of units
     *
     * @param units         a list of units to move
     * @param directions    the directions to move each unit (i.e. units.get(i) should move in direction directions.get(i))
     * @param roundMovements List of RoundMovement objects that gets updated with result from function
     * @return a boolean array indicating whether each unit has collided during this movement step
     */
    private boolean[] doMovementStep(List<Unit> units,
                                     List<Direction> directions,
                                     List<RoundMovement> roundMovements) {
        List<Movement> unitMovements = new ArrayList<>();
        List<UnitCollision> unitCollisions = new ArrayList<>();
        List<TileCollision> tileCollisions = new ArrayList<>();

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
        // this array will be filled first with the positions of colliding units, then the rest

        // list of terrain tiles to damage through collisions
        List<Tile> collidedTiles = new ArrayList<>();

        // handle collisions between units and terrain (or the map boundary)
        for (int i = 0; i < goalPositions.size(); i ++) {
            if (! inBounds(goalPositions.get(i)) || map.tileAt(goalPositions.get(i)).getType() != Tile.Type.BLANK) {
                System.out.println("Terrain Collision");

                collided[i] = true;
                units.get(i).takeCollisionDamage();

                // add to game log
                tileCollisions.add(new TileCollision(
                        units.get(i),
                        null,
                        Unit.COLLISION_DAMAGE,
                        0));

                if (inBounds(goalPositions.get(i))) {
                    // add the terrain to the list of terrain to damage
                    collidedTiles.add(map.tileAt(goalPositions.get(i)));
                }
            } else if (map.tileAt(goalPositions.get(i)).getUnit() != null) {
                // handle collision with stationary unit
                boolean isMoving = false;
                for (Unit moving : units) {
                    if (moving == map.tileAt(goalPositions.get(i)).getUnit()) {
                        isMoving = true;
                        break;
                    }
                }

                if (!isMoving) {
                    System.out.println("Stationary Unit Collision");

                    collided[i] = true;
                    units.get(i).takeCollisionDamage();
                    map.tileAt(goalPositions.get(i)).getUnit().takeCollisionDamage();

                    // add to game log
                    tileCollisions.add(new TileCollision(
                            units.get(i),
                            map.tileAt(goalPositions.get(i)),
                            Unit.COLLISION_DAMAGE,
                            Tile.COLLISION_DAMAGE));
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
                    System.out.println("2-Unit collision");

                    collided[i] = true;
                    units.get(i).takeCollisionDamage();
                    collided[j] = true;
                    units.get(j).takeCollisionDamage();

                    // add to game log
                    unitCollisions.add(new UnitCollision(
                            units.get(i),
                            units.get(j),
                            Unit.COLLISION_DAMAGE,
                            Unit.COLLISION_DAMAGE
                    ));
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
                            System.out.println("Ripple Collision");
                            foundRipple = true;
                            collided[i] = true;
                            units.get(i).takeCollisionDamage();
                            units.get(j).takeCollisionDamage();

                            // add to game log
                            unitCollisions.add(
                                    new UnitCollision(
                                            units.get(i),
                                            units.get(j),
                                            Unit.COLLISION_DAMAGE,
                                            Unit.COLLISION_DAMAGE
                                    ));
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
                unitMovements.add(new Movement(
                        units.get(i),     // same indices as above
                        directions.get(i) // same indices as above
                ));
            }
        }

        roundMovements.add(new RoundMovement(
                unitMovements.toArray(new Movement[0]),
                unitCollisions.toArray(new UnitCollision[0]),
                tileCollisions.toArray(new TileCollision[0])
        ));

        map.moveUnits(moving, destinations);

        // necessary to return this array; can't return roundMovements here
        return collided;
    }

    /**
     * For any dead units, removes them from the board
     */
    private void doDeaths() {
        doDeaths(p1Units);
        doDeaths(p2Units);
    }

    /**
     * handles death for an array of units
     * @param units array of units to check death conditions for
     */
    private void doDeaths(Unit[] units) {
        for (Unit u : units) {
            if (u.isAlive()) {
                Position oldPos = u.getPos();

                if (u.doDeath()) {
                    map.tileAt(oldPos).setUnit(null);
                }
            }
        }
    }

    /**
     * @param pos a position that may or may not be on the map
     * @return true if the position lies within the map, false otherwise
     */
    private boolean inBounds(Position pos) {
        return (pos.x >= 0 && pos.x < map.width() && pos.y >= 0 && pos.y < map.height());
    }

    public String getMapString() {
        return map.toString();
    }

    public String getUnitStatsString(){
        StringBuilder ret = new StringBuilder();

        ret.append("Player 1 Unit Stats:\tPlayer 2 Unit Stats:\n");

        for (int i = 0; i < p1Units.length; i++) {
            if (p1Units[i].isAlive()) {
                ret.append(p1Units[i].getId() + ": hp = " + p1Units[i].getHp() + "\t\t\t");
            } else {
                ret.append("        \t\t\t");
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

    public static final int P1_WINNER = 0;
    public static final int P2_WINNER = 1;
    public static final int TIE = 2;
    public static final int NO_WINNER = 3;

    public String getInitialVisualizerJson() {
        return gameStateSerializer.toJson(this);
    }

    public String getRoundVisualizerJson() {
        return gameRoundSerializer.toJson(recentRound);
    }

    public String getRecentPlayerJson() {
        return gameStateSerializer.toJson(this);
    }
}
