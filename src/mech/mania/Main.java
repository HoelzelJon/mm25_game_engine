package mech.mania;

/*
 * Main class -- where the magic happens
 */
public class Main {
    public static void main(String[] args) {
        Player player1 = new Player();
        Player player2 = new Player();

        //TODO: figure out how to do initial spawns
        //TODO: get attack patterns from each player

        int size = 4;
        Position[] p1Positions = new Position[3];
        p1Positions[0] = new Position(0, 0);
        p1Positions[1] = new Position(1, 0);
        p1Positions[2] = new Position(0, 1);

        Position[] p2Positions = new Position[3];
        p2Positions[0] = new Position(size-1, size-1);
        p2Positions[1] = new Position(size-2, size-1);
        p2Positions[2] = new Position(size-1, size-2);

        Game game = new Game(size, p1Positions, p2Positions);

        while (game.getWinner() == Game.NO_WINNER) {
            System.out.println("Start of main loop");
            //player1.sendGameState(game);
            //player2.sendGameState(game);

            int[] priorities = {3, 2, 1};
            Direction[][] p1Movements = new Direction[3][1];
            p1Movements[0][0] = Direction.RIGHT;
            p1Movements[1][0] = Direction.RIGHT;
            p1Movements[2][0] = Direction.RIGHT;
            Direction[][] p2Movements = new Direction[3][1];
            p2Movements[0][0] = Direction.LEFT;
            p2Movements[1][0] = Direction.LEFT;
            p2Movements[2][0] = Direction.LEFT;

            Direction[] attacks = new Direction[3];

            Decision p1Decision = new Decision(priorities, p1Movements, attacks); //TODO: player1.getDecision();
            Decision p2Decision = new Decision(priorities, p2Movements, attacks); //TODO: player2.getDecision();

            game.doTurn(p1Decision, p2Decision);

            printTurnLog(game);

            try {
                Thread.sleep(1000);
            } catch (Exception ex) {}
        }
    }

    static void printTurnLog(Game g) {
        System.out.println(g.getMapString());
        //TODO
    }
}
