package mech.mania;

public class game_API {
	Game game;
	String playerName;

	public game_API(Game g, String playerName){
		this.game = g;
		this.playerName = playerName;
	}

	public Decision getTurnDecision(){
		return null;
	}

	public UnitSetup[] getUnitSetups(){
		return null;
	}

	public Direction[] pathTo(Position start, Position end, Position[] tilesToAvoid){
		return null;
	}

	public Position getPositionAfterMovement(Position init, Direction[] movementSteps){
		return null;
	}

	public Unit[] getMyUnits() {		
		return null;
	}
	
	public Unit[] getEnemyUnits() {
		return null;
	}
	
	public Unit getUnitAt(Position pos) {
		return game.getMap().tileAt(pos).getUnit();
	}
	
	public Tile getTile(Position pos) {
		return game.getMap().tileAt(pos);
	}
}
