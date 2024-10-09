package tp1.logic;

public class Game implements GameStatus{

	public static final int DIM_X = 10;
	public static final int DIM_Y = 10;

	public Game(int nLevel) {
		// TODO Auto-generated constructor stub
	}
	
// GameStatus methods
	@Override
	public int getCycle() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numLemmingsInBoard() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numLemmingsDead() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numLemmingsExit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int numLemmingsToWin() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String positionToString(int col, int row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean playerWins() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerLooses() {
		// TODO Auto-generated method stub
		return false;
	}

// GameModel methods
	// @Override
	public void update() {
		// TODO Auto-generated method stub
	}
	// @Override
	public void exit() {
		// TODO Auto-generated method stub		
	}
	//@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}
	
	// TODO Auto-generated method stub
	
// GameWorld methods (callbacks)
	// @Override
	public boolean isInAir(Position pos) {
		// TODO Auto-generated method stub
        return false;
    }
		
	// @Override
	public void lemmingArrived() {
		// TODO Auto-generated method stub
	}
	// TODO Auto-generated method stub
	
// Other methods
	// TODO you should write a toString method to return the string that represents the object status
	// @Override
	// public String toString()
}
