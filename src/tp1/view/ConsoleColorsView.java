package tp1.view;

import static tp1.util.MyStringUtils.repeat;

import tp1.logic.Game;
import tp1.util.MyStringUtils;

public class ConsoleColorsView extends ConsoleView {
	
	private static final String CELL_TXT = repeat(SPACE, CELL_SIZE);
	private static final String EMPTY_CELL = CELL_TXT;
	private static final String WALL_CELL = ConsoleColorsAnsiCodes.ANSI_CYAN_BACKGROUND + CELL_TXT + ConsoleColorsAnsiCodes.ANSI_RESET;
	private static final String EXIT_TEXT = ConsoleColorsAnsiCodes.ANSI_WHITE + MyStringUtils.center("E", CELL_SIZE);
	private static final String EXIT_CELL = ConsoleColorsAnsiCodes.ANSI_PURPLE_BACKGROUND + EXIT_TEXT + ConsoleColorsAnsiCodes.ANSI_RESET;
	private static final String EXIT_PLUS = ConsoleColorsAnsiCodes.ANSI_PURPLE_BACKGROUND + "%s" + ConsoleColorsAnsiCodes.ANSI_RESET;

	public ConsoleColorsView(Game game) {
		super(game);
	}
	
	@Override
	protected String consoleCell(String celStr) {
		String consoleStr = celStr;

		if (celStr.equals(Messages.EMPTY)) consoleStr = EMPTY_CELL;
		else if (celStr.equals(Messages.WALL)) consoleStr = WALL_CELL;
		else if (celStr.equals(Messages.EXIT_DOOR)) consoleStr = EXIT_CELL;
		else if (celStr.contains(Messages.EXIT_DOOR)) consoleStr = exitCel(celStr);

		else  consoleStr = lemmyCell(celStr);
		
		return consoleStr;
	}

	private static String lemmyCell(String lemmy) {
		return ConsoleColorsAnsiCodes.ANSI_GREEN + MyStringUtils.center(lemmy, CELL_SIZE) + ConsoleColorsAnsiCodes.ANSI_RESET;
	}
	private static String exitCel(String celStr) {
		return EXIT_PLUS.formatted(MyStringUtils.center(celStr, CELL_SIZE));
	}
}
