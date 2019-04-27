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

        Map map = new Map(mapDirectory, gameID);

        PlayerCommunicator player1 = new /*HumanPlayerCommunicator(1); */ServerPlayerCommunicator(1, p1URL);
        PlayerCommunicator player2 = new /*HumanPlayerCommunicator(2); */ServerPlayerCommunicator(2, p2URL);

        int[][][] p1Attacks = player1.getAttackPatterns(gameID, map);
        int[][][] p2Attacks = player2.getAttackPatterns(gameID, map);

        Game game = new Game(gameID, new String[] {p1Name, p2Name}, p1Attacks, p2Attacks, map);

        printInitialVisualizerJson(game);

        while (game.getWinner() == Game.NO_WINNER) {

            Decision p1Decision = player1.getDecision(game);
            Decision p2Decision = player2.getDecision(game);

            game.doTurn(p1Decision, p2Decision);

            //printGameMap(game);
            printRoundVisualizerJson(game);

            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
            }
        }

        if (game.getWinner() == Game.TIE) {
            System.out.println("{\"Winner\": 1}");
        } else if (game.getWinner() == Game.P1_WINNER) {
            System.out.println("{\"Winner\": 2}");
        } else if (game.getWinner() == Game.P2_WINNER) {
            System.out.println("{\"Winner\": 3}");
        }
    }

    static void printGameMap(Game game) {
        System.out.println(game.getMapString() + "\n");
    }

    static void printInitialVisualizerJson(Game game) {
        System.out.println(game.getInitialVisualizerJson());
    }

    static void printRoundVisualizerJson(Game game) {
        System.out.println(game.getRoundVisualizerJson());
    }
}
