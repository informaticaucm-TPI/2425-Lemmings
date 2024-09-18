package tp1.view;

import static tp1.util.MyStringUtils.repeat;

import java.util.Scanner;

import tp1.logic.Game;
import tp1.util.MyStringUtils;

public class ConsoleView extends GameView {
	
	protected static final String SPACE = " ";
	private static final String LATERAL_TAB = "  ";

	private static final String CELL_BORDER_CHAR = "—";

	private static final String VERTICAL_DELIMITER = "┃"; // "|";

	private static final String NEW_LINE = System.lineSeparator();

	public static final int CELL_SIZE = 5;

	private static final String CELL_BORDER = repeat(CELL_BORDER_CHAR, CELL_SIZE);

	private static final String ROW_BORDER = repeat(CELL_BORDER, Game.DIM_X);

	private static final String UPPER_ROW_BORDER = "┌" + ROW_BORDER + "┐" + NEW_LINE;
	private static final String LOWER_ROW_BORDER = "└" + ROW_BORDER + "┘" + NEW_LINE;

	private static final String TAB = MyStringUtils.repeat(SPACE, CELL_SIZE);

	private static final String INDENTED_UPPER_ROW_BORDER = TAB + UPPER_ROW_BORDER;
	private static final String INDENTED_LOWER_ROW_BORDER = TAB + LOWER_ROW_BORDER ;

	Scanner scanner;

	public ConsoleView(Game game) {
		super(game);
		scanner = new Scanner(System.in);
	}
		
	/**
	 * Builds a string that represent the game status
	 * 
	 * @return the string that represents the game status.
	 */
	private String getInfo() {
		StringBuilder buffer = new StringBuilder();
		/* @formatter:off */
		buffer
		.append(Messages.NUMBER_OF_CYCLES).append(SPACE).append(game.getCycle()).append(NEW_LINE)
		.append(Messages.NUM_LEMMINGS).append(SPACE).append(game.numLemmingsInBoard()).append(NEW_LINE)
		.append(Messages.DEAD_LEMMINGS).append(SPACE).append(game.numLemmingsDead()).append(NEW_LINE)
		.append(Messages.EXIT_LEMMINGS).append(SPACE).append(game.numLemmingsExit())
		.append(SPACE).append(VERTICAL_DELIMITER).append(game.numLemmingsToWin()).append(NEW_LINE);
		/* @formatter:on */
		return buffer.toString();
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		// Game Status
		str.append(getInfo());
		str.append(NEW_LINE);

		// Paint game board
		str.append(colNumbersLine());
		str.append(LATERAL_TAB + SPACE);
		str.append(UPPER_ROW_BORDER);

		String colName;
		for (int row = 0; row < Game.DIM_Y; row++) {
			str.append(LATERAL_TAB);
			colName = Character.toString('A'+row%27);
			str.append(colName);
			str.append(VERTICAL_DELIMITER);

			for (int col = 0; col < Game.DIM_X; col++) {
				str.append(consoleCell(game.positionToString(col, row)));
				//str.append(VERTICAL_DELIMITER);
			}
			str.append(VERTICAL_DELIMITER);
			str.append(colName);
			str.append(NEW_LINE);
			//str.append(ROW_BORDER);
		}

		str.append(LATERAL_TAB + SPACE);
		str.append(LOWER_ROW_BORDER);
		str.append(colNumbersLine());

		return str.toString();
	}

	private static String colNumbersLine() {
		StringBuilder str = new StringBuilder();
		str.append(LATERAL_TAB + SPACE+ SPACE);
		for (int col = 0; col < Game.DIM_X; col++) {
			str.append(MyStringUtils.center(Integer.toString((col+1)%100), CELL_SIZE));
		}
		str.append(NEW_LINE);
		return str.toString();
	}
	
	private String endMessage() {
		StringBuilder sb = new StringBuilder();
		if(game.playerWins()) sb.append(Messages.PLAYER_WINS);
		else if (game.playerLooses()) sb.append(Messages.PLAYER_LOOSES);
		else sb.append(Messages.PLAYER_QUITS);
		return sb.toString();
	}

	protected String consoleCell(String celStr) {
		return MyStringUtils.center(celStr, CELL_SIZE);
	}
	
	@Override
	public void showWelcome() {
		System.out.println(Messages.WELCOME);
   }

	@Override
	public void showGame() {
		System.out.println(this.toString());
	}

	@Override
	public void showEndMessage() {
		System.out.println(this.endMessage());
	}

	@Override
	public void showError(String message) {
        System.out.println(Messages.ERROR.formatted(message));		
	}
	
	@Override
	public void showMessage(String message) {
		System.out.println(message);
	}

	/**
	 * Show prompt and request command.
	 *
	 * @return the player command as words
	 */
	@Override
	public String[] getPrompt() {
		System.out.print(Messages.PROMPT);
		String line = scanner.nextLine();
		String[] words = line.trim().split("\\s+");

        System.out.println(Messages.DEBUG.formatted(line));		

		return words;
	}
}
