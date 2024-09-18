package tp1.view;

import tp1.logic.Game;

public abstract class GameView {

	protected Game game;
	
	public GameView(Game game) {
		this.game = game;
	}
	
	// show methods
	public abstract void showWelcome();
	public abstract void showGame();
	public abstract void showEndMessage();
	public abstract void showError(String message);
	public abstract void showMessage(String message);

	// get data from view methods
	public abstract String[] getPrompt();
}
