package mech.mania;

import com.google.gson.Gson;

public class game_API {

	private Game game;
	private String playerName;
	private int playerNum; //Set playerNum from JSON

	public game_API(Game g, String playerName){
		this.game = g;
		this.playerName = playerName;
	}

	public void getTurnDecision(){ 
		/*
		Their code here
		*/
	}

	public void getUnitSetups(){
		/*
		Their code here
		*/
	}

	public Direction[] pathTo(Position start, Position end, Position[] tilesToAvoid){
		//Implement shortest path algorithm
		return null;
	}

	public Position getPositionAfterMovement(Position init, Direction[] movementSteps){
		Position p = new Position(init.getX(), init.getY());
		for(int i = 0; i < movementSteps.length; i++){
			p = p.getNewPosition(movementSteps[i]);
		}
		return p;
	}

	public Unit[] getMyUnits() {
		if(playerNum == 1){
			return game.getP1Units();
		}else{
			return game.getP2Units();
		}		
		return game.getP1Units();
	}
	
	public Unit[] getEnemyUnits() {
		if(playerNum == 1){
			return game.getP1Units();
		}else{
			return game.getP2Units();
		}
	}
	
	public Unit getUnitAt(Position pos) {
		return game.getMap().tileAt(pos).getUnit();
	}
	
	public Tile getTile(Position pos) {
		return game.getMap().tileAt(pos);
	}
}
