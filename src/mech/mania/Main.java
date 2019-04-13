package mech.mania;

/**
 * Main class -- where the magic happens
 */
public class Main {
    public static void main(String[] args) {
        String gameID = args[0];
        Map map = new Map();
        PlayerCommunicator player1 = new HumanPlayerCommunicator(1); //ServerPlayerCommunicator(1, "http://127.0.0.1:5000/");
        PlayerCommunicator player2 = new HumanPlayerCommunicator(2); //ServerPlayerCommunicator(2, "http://127.0.0.1:5000/");

        int[][][] p1Attacks = player1.getAttackPatterns(gameID, map);
        int[][][] p2Attacks = player2.getAttackPatterns(gameID, map);

        Game game = new Game(gameID, p1Attacks, p2Attacks, map);

        printInitialState(game);

        while (game.getWinner() == Game.NO_WINNER) {

            Decision p1Decision = player1.getDecision(game);
            Decision p2Decision = player2.getDecision(game);

            game.doTurn(p1Decision, p2Decision);

            printGameMap(game);
            printVisualizerJson(game);

            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
            }
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
        System.out.println(game.getRecentVisualizerJson() + "\n");
    }

    static void printGameMap(Game game) {
        System.out.println(game.getMapString() + "\n");
    }

    static void printVisualizerJson(Game game) {
        System.out.println(game.getRecentVisualizerJson() + "\n");
    }
}
