package tp1.view;

import static tp1.util.MyStringUtils.repeat;

import tp1.logic.Game;
import tp1.util.MyStringUtils;

public class ConsoleColorsView extends ConsoleView {
//	https://www.rapidtables.com/web/color/RGB_Color.html
//    private static final String COLOR_METAL_WALL_BACKGROUND  = ConsoleColorsAnsiCodes.ANSI_BLUE_BACKGROUND;
//    private static final String COLOR_WALL_BACKGROUND  = ConsoleColorsAnsiCodes.ANSI_CYAN_BACKGROUND;
//    private static final String COLOR_EXIT_BACKGROUND  = ConsoleColorsAnsiCodes.ANSI_PURPLE_BACKGROUND;
    private static final String COLOR_METAL_WALL_BACKGROUND  = ConsoleColorsAnsiCodes.ANSI_RGB_BACKGROUND.formatted(128, 128, 128); // ConsoleColorsAnsiCodes.ANSI_BLUE_BACKGROUND
    private static final String COLOR_WALL_BACKGROUND  = ConsoleColorsAnsiCodes.ANSI_RGB_BACKGROUND.formatted(218, 165, 32); // ConsoleColorsAnsiCodes.ANSI_CYAN_BACKGROUND
    private static final String COLOR_EXIT_BACKGROUND  = ConsoleColorsAnsiCodes.ANSI_CYAN_BACKGROUND;
	
	private static final String CELL_TXT = repeat(SPACE, CELL_SIZE);
	private static final String EMPTY_CELL = CELL_TXT;
	private static final String WALL_CELL = COLOR_WALL_BACKGROUND + CELL_TXT + ConsoleColorsAnsiCodes.ANSI_RESET;
	private static final String METAL_WALL_CELL = COLOR_METAL_WALL_BACKGROUND + CELL_TXT + ConsoleColorsAnsiCodes.ANSI_RESET;
	private static final String EXIT_TEXT = ConsoleColorsAnsiCodes.ANSI_WHITE + MyStringUtils.center("E", CELL_SIZE);
	private static final String EXIT_CELL = COLOR_EXIT_BACKGROUND + EXIT_TEXT + ConsoleColorsAnsiCodes.ANSI_RESET;
	private static final String EXIT_PLUS = COLOR_EXIT_BACKGROUND + "%s" + ConsoleColorsAnsiCodes.ANSI_RESET;

	public ConsoleColorsView(Game game) {
		super(game);
	}
	
	@Override
	protected String consoleCell(String celStr) {
		String consoleStr = celStr;

		if (celStr.equals(Messages.EMPTY)) consoleStr = EMPTY_CELL;
		else if (celStr.equals(Messages.WALL)) consoleStr = WALL_CELL;
		else if (celStr.equals(Messages.METALWALL)) consoleStr = METAL_WALL_CELL;
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
