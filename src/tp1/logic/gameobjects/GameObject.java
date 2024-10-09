package tp1.logic.gameobjects;

import tp1.logic.Game;
import tp1.logic.Position;

public abstract class GameObject {

	protected Position pos;
	protected boolean isAlive;
	protected Game game;
	
	public GameObject(Game game, Position pos) {
		this.isAlive = true;
		this.pos = pos;
		this.game = game;
	}
	
	public boolean isInPosition(Position p) {
		// TODO fill your code here, it should depends on the status of the object
		return false;
	}
 	
	public boolean isAlive() {
		return isAlive;
	}
	
	// TODO implement and decide, Which one is abstract?
	// public boolean isSolid()
	// public boolean isExit()
	// public void update()
	
	public abstract String getIcon();

}
