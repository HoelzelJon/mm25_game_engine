package mech.mania;

/**
 * Main class -- where the magic happens
 */
public class Main {
    public static void main(String[] args) {
        String gameID = args[0];

        PlayerCommunicator player1 = new HumanPlayerCommunicator(1); //ServerPlayerCommunicator(1, "http://127.0.0.1:5000/");
        PlayerCommunicator player2 = new HumanPlayerCommunicator(2); //ServerPlayerCommunicator(2, "http://127.0.0.1:5000/");

        //TODO: get attack patterns from each player

//        int[][] attack = {{0, 0, 1, 0, 0},
//                          {0, 0, 1, 0, 0},
//                          {1, 1, 0, 1, 1},
//                          {0, 0, 1, 0, 1},
//                          {0, 0, 1, 0, 0}}; // when printed in-game, the 1's should be pointing up
//        int[][][] p1Attacks = {attack, attack, attack};
//        int[][][] p2Attacks = {attack, attack, attack};

        int[][][] p1Attacks = player1.getAttackPatterns();
        int[][][] p2Attacks = player2.getAttackPatterns();

        Game game = new Game(gameID, p1Attacks, p2Attacks);
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

//            Decision p1Decision = new Decision(priorities, p1Movements, attacks); //TODO: player1.getDecision();
//            Decision p2Decision = new Decision(priorities, p2Movements, attacks); //TODO: player2.getDecision();

            Decision p1Decision = player1.getDecision(game);
            Decision p2Decision = player2.getDecision(game);

            game.doTurn(p1Decision, p2Decision);

            printGameMap(game);
            printVisualizerJson(game);

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

    static void printInitialState(Game game) {
        System.out.println(game.getRecentVisualizerJson());
    }

    static void printGameMap(Game game) {
        System.out.println(game.getMapString() + "\n");
    }

    static void printVisualizerJson(Game game) {
        System.out.println(game.getRecentVisualizerJson());
    }
}
