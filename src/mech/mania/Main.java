package mech.mania;

/**
 * Main class -- where the magic happens
 */
public class Main {
    public static void main(String[] args) {
        Map map = new Map();
        PlayerCommunicator player1 = new HumanPlayerCommunicator(1); //ServerPlayerCommunicator(1, "http://127.0.0.1:5000/");
        PlayerCommunicator player2 = new HumanPlayerCommunicator(2); //ServerPlayerCommunicator(2, "http://127.0.0.1:5000/");

        int[][][] p1Attacks = player1.getAttackPatterns(map);
        int[][][] p2Attacks = player2.getAttackPatterns(map);

        Game game = new Game(p1Attacks, p2Attacks, map);

        String gameHistory = "\n";

        while (game.getWinner() == Game.NO_WINNER) {
            gameHistory += game.getMapString() + "\n";

            Decision p1Decision = player1.getDecision(game);
            Decision p2Decision = player2.getDecision(game);

            game.doTurn(p1Decision, p2Decision);

            printTurnLog(game);

            String[] p1 = p1Decision.getActions();
            String[] p2 = p2Decision.getActions();
            for (int i = 0; i < 3; i++) {
                gameHistory += "\nplayer1:\n" + p1[i] + "\nplayer2:\n" + p2[i] + "\n";
            }

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

        System.out.print(gameHistory);
    }

    static void printTurnLog(Game g) {
        //System.out.println(g.getMapString() + "\n");
        //TODO
    }
}
