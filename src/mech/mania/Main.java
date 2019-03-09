package mech.mania;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Main class -- where the magic happens
 */
public class Main {
    public static void main(String[] args) {
        Player player1 = new Player();
        Player player2 = new Player();

        //TODO: get attack patterns from each player

        int[][] attack = {{0, 0, 1, 0, 0},
                          {0, 0, 1, 0, 0},
                          {1, 1, 0, 1, 1},
                          {0, 0, 1, 0, 1},
                          {0, 0, 1, 0, 0}}; // when printed in-game, the 1's should be pointing up
        int[][][] p1Attacks = {attack, attack, attack};
        int[][][] p2Attacks = {attack, attack, attack};

        Game game = new Game(p1Attacks, p2Attacks);

        while (game.getWinner() == Game.NO_WINNER) {
            //player1.sendGameState(game);
            //player2.sendGameState(game);

            int[] priorities = {1,2,3};
            Direction[][] p1Movements = {
                                    {Direction.STAY},
                                    {Direction.DOWN},
                                    {Direction.RIGHT}};
            Direction[][] p2Movements = {
                                    {Direction.UP},
                                    {Direction.STAY},
                                    {Direction.LEFT}};

            Direction[] attacks = {Direction.STAY, Direction.STAY, Direction.STAY};

            Decision p1Decision = new Decision(priorities, p1Movements, attacks); //TODO: player1.getDecision();
            Decision p2Decision = new Decision(priorities, p2Movements, attacks); //TODO: player2.getDecision();

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
