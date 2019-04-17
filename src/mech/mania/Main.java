package mech.mania;

/**
 * Main class -- where the magic happens
 */
public class Main {
    public static void main(String[] args) {
        String gameID = args[0];
        String mapDirectory = args[1];
        String p1Name = args[2];
        String p2Name = args[3];
        String p1URL = args[4];
        String p2URL = args[5];

        Map map = new Map(mapDirectory);

        PlayerCommunicator player1 = new HumanPlayerCommunicator(1); //ServerPlayerCommunicator(1, p1URL);
        PlayerCommunicator player2 = new HumanPlayerCommunicator(2); //ServerPlayerCommunicator(2, p2URL);

        UnitSetup[] p1setup = player1.getUnitsSetup(gameID, map);
        UnitSetup[] p2setup = player2.getUnitsSetup(gameID, map);

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

        Game game = new Game(gameID, new String[] {p1Name, p2Name}, p1setup, p2setup, map);

        printInitialVisualizerJson(game);

        while (game.getWinner() == Game.NO_WINNER) {

            Decision p1Decision = player1.getDecision(game);
            Decision p2Decision = player2.getDecision(game);

            game.doTurn(p1Decision, p2Decision);

            printRoundVisualizerJson(game);

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

    static void printInitialVisualizerJson(Game game) {
        System.out.println(game.getInitialVisualizerJson() + "\n");
    }

    static void printRoundVisualizerJson(Game game) {
        System.out.println(game.getRoundVisualizerJson() + "\n");
    }
}
