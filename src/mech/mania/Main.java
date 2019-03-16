package mech.mania;

import java.util.Arrays;

/**
 * Main class -- where the magic happens
 */
public class Main {
    public static void main(String[] args) {
        HumanPlayerCommunicator player1 = new HumanPlayerCommunicator(1); //ServerPlayerCommunicator(1, "http://127.0.0.1:5000/");
        HumanPlayerCommunicator player2 = new HumanPlayerCommunicator(2); //ServerPlayerCommunicator(2, "http://127.0.0.1:5000/");

        //int[][][] p1Attacks = player1.getAttackPatterns(1); 
        //int[][][] p2Attacks = player2.getAttackPatterns(1);

        /*int size = 6;
        Position[] p1Positions = {new Position(0,0)};
        Position[] p2Positions = {new Position(size-1, size-1)};*/

        int size = 6;
        Position[] p1Positions = {
                                new Position(0, 0),
                                new Position(1, 0),
                                new Position(0, 1)};

        Position[] p2Positions = {
                                new Position(size-1, size-1),
                                new Position(size-2, size-1),
                                new Position(size-1, size-2)};

        int[][] attack = {{0, 0, 4, 0, 0},
                          {0, 0, 4, 0, 0},
                          {3, 3, 0, 1, 1},
                          {0, 0, 2, 0, 1},
                          {0, 0, 2, 0, 0}}; // when printed in-game, the 1's should be pointing up
        int[][][] p1Attacks = {attack, attack, attack};
        int[][][] p2Attacks = {attack, attack, attack};

        System.out.println(Arrays.deepToString(attack));
        System.out.println(Arrays.deepToString(Map.toGameCoords(attack)));

        Game game = new Game(size, p1Positions, p2Positions, p1Attacks, p2Attacks, "ID");

        while (game.getWinner() == Game.NO_WINNER) {
            /*int[] priorities = {1,1,1};
            Direction[][] p1Movements = {
                                    {Direction.STAY},
                                    {Direction.UP},
                                    {Direction.RIGHT}};
            Direction[][] p2Movements = {
                                    {Direction.LEFT},
                                    {Direction.LEFT},
                                    {Direction.LEFT}};

            Direction[] attacks = new Direction[3];*/

            //Decision p1Decision = new Decision(priorities, p1Movements, attacks);
            Decision p1Decision = player1.getDecision(game);
            Decision p2Decision = player2.getDecision(game);

            game.doTurn(p1Decision, p2Decision);

            printTurnLog(game);

            try {
                Thread.sleep(1000);
            } catch (Exception ex) {}
        }

        if (game.getWinner() == Game.TIE) {
            System.out.println("It's a tie!");
        } else if (game.getWinner() == Game.P1_WINNER) {
            System.out.println("Player 1 wins!");
        } else if (game.getWinner() == Game.P2_WINNER) {
            System.out.println("Player 2 wins!");
        }
    }

    static void printTurnLog(Game g) {
        System.out.println(g.getMapString() + "\n");
        //TODO
    }
}
