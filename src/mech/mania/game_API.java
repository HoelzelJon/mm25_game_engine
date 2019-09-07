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

	public Unit[] getMyUnits() {		
		return null;
	}
	
	public Unit[] getEnemyUnits() {
		return null;
	}
	
	public Unit getUnitAt(Position pos) {
		return null;
	}
	
	public Tile getTile(Position pos) {
		return null;
	}
}
