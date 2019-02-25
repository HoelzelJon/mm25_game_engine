package mech.mania;

/**
 * Main class -- where the magic happens
 */
public class Main {
    public static void main(String[] args) {
        Player player1 = new Player();
        Player player2 = new Player();

        //TODO: figure out how to do initial spawns
        //TODO: get attack patterns from each player

        int size = 6;
        Position[] p1Positions = {
                                new Position(0, 0),
                                new Position(1, 0),
                                new Position(0, 1)};

        Position[] p2Positions = {
                                new Position(size-1, size-1),
                                new Position(size-2, size-1),
                                new Position(size-1, size-2)};

        Game game = new Game(size, p1Positions, p2Positions);

        while (game.getWinner() == Game.NO_WINNER) {
            //player1.sendGameState(game);
            //player2.sendGameState(game);

            int[] priorities = {1,1,1};
            Direction[][] p1Movements = {
                                    {Direction.RIGHT, Direction.UP},
                                    {Direction.RIGHT, Direction.UP},
                                    {Direction.RIGHT, Direction.UP}};
            Direction[][] p2Movements = {
                                    {Direction.LEFT},
                                    {Direction.LEFT},
                                    {Direction.LEFT}};

            Direction[] attacks = new Direction[3];

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
