package mech.mania;

import java.util.ArrayList;

/**
 * Main class -- where the magic happens
 */
public class Main {
    public static void main(String[] args) {
        PlayerCommunicator player1 = new GUIPlayerCommunicator(1); //ServerPlayerCommunicator(1, "http://127.0.0.1:5000/");
        PlayerCommunicator player2 = new GUIPlayerCommunicator(2); //ServerPlayerCommunicator(2, "http://127.0.0.1:5000/");

        UnitSetup[] p1setup = player1.getUnitsSetup();
        UnitSetup[] p2setup = player2.getUnitsSetup();

        for (int i = 0; i < 3; i++) {
            System.out.println("p1 bot " + i + " setup health: " + p1setup[i].health);
            System.out.println("p1 bot " + i + " setup speed: " + p1setup[i].speed);
            InputValidator.printAttackPattern(p1setup[i].attackPattern);
        }
        for (int i = 0; i < 3; i++) {
            System.out.println("p2 bot " + i + "setup health: " + p2setup[i].health);
            System.out.println("p2 bot " + i + "setup speed: " + p2setup[i].speed);
            InputValidator.printAttackPattern(p2setup[i].attackPattern);
        }

        String gameID = args[0];

        Game game = new Game(gameID, p1setup, p2setup);
        printInitialState(game);

        while (game.getWinner() == Game.NO_WINNER) {
//            int[] priorities = {1,2,3};
//            Direction[][] p1Movements = {
//                                    {Direction.STAY, Direction.LEFT, Direction.UP},
//                                    {Direction.DOWN, Direction.STAY, Direction.RIGHT},
//                                    {Direction.RIGHT, Direction.DOWN}};
//            Direction[][] p2Movements = {
//                                    {Direction.STAY, Direction.STAY, Direction.DOWN},
//                                    {Direction.STAY, Direction.DOWN, Direction.DOWN},
//                                    {Direction.RIGHT, Direction.DOWN, Direction.DOWN, Direction.LEFT}};
//
//            Direction[] attacks = {Direction.STAY, Direction.UP, Direction.STAY};

//            Decision p1Decision = new Decision(priorities, p1Movements, attacks);
//            Decision p2Decision = new Decision(priorities, p2Movements, attacks);

            Decision p1Decision = player1.getDecision(game);
            Decision p2Decision = player2.getDecision(game);

            game.doTurn(p1Decision, p2Decision);

            printGameMap(game);
            printVisualizerJson(game);
            printPlayerJson(game);

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

    static void printGameMap(Game game) {
        System.out.println(game.getMapString() + "\n");
    }

    static void printInitialState(Game game) {
        System.out.println(game.getRecentVisualizerJson() + "\n");
    }

    static void printVisualizerJson(Game game) {
        System.out.println(game.getRecentVisualizerJson() + "\n");
    }

    static void printPlayerJson(Game game) {
        System.out.println(game.getRecentPlayerJson() + "\n");
    }
}
