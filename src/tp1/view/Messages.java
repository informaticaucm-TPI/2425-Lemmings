package tp1.view;

import tp1.util.MyStringUtils;

public class Messages {
	
	public static final String VERSION = "1.0";

	public static final String GAME_NAME = "Lemmings";

	public static final String USAGE = "Usage: %s [<level>]".formatted(GAME_NAME);

	public static final String WELCOME = String.format("%s %s%n", GAME_NAME, VERSION);

	public static final String LEVEL_NOT_A_NUMBER = "The level must be a number";

	public static final String LEVEL_NOT_A_NUMBER_ERROR = String.format("%s: %%s", LEVEL_NOT_A_NUMBER);

	public static final String CONFIGURED_LEVEL = "Level: %s";

	public static final String PROMPT = "Command > ";

	public static final String DEBUG = "%n[DEBUG] Executing: %s%n";

	public static final String ERROR = "[ERROR] Error: %s%n";

	public static final String LINE_SEPARATOR = System.lineSeparator();

	public static final String HELP_AVAILABLE_COMMANDS = "Available commands:";

	public static final String HELP_DETAILS_COMMAND_HELP_SEPARATOR = ": ";

	/* @formatter:off */
	public static final String[] HELP_LINES = new String[] { "Available commands:",
			"[r]eset: start a new game",
			"[h]elp: print this help message",
			"[e]xit: end the execution of the game",
			"[s]et[R]ole: sets the lemming role",
			"[e]xit: end the execution of the game",
			"[n]one | \"\": skips cycle"};
	/* @formatter:on */

	public static final String HELP = String.join(System.lineSeparator(), HELP_LINES);
	
	public static final String UNKNOWN_COMMAND = "Unknown command";

	public static final String COMMAND_PARAMETERS_MISSING = "Missing parameters";

	public static final String COMMAND_INCORRECT_PARAMETER_NUMBER = "Incorrect parameter number";

	public static final String INVALID_POSITION = "Invalid position (%s, %s)";

	public static final String INVALID_GAME_OBJECT = String.format("Invalid object %n");

	public static final String INVALID_COMMAND = "Invalid command";

	public static final String NUMBER_OF_CYCLES = "Number of cycles:";

	public static final String REMAINING_LEMMINGS = "Remaining lemmings:";

	public static final String DEAD_LEMMINGS = "Lemmings dead:";

	public static final Object EXIT_LEMMINGS = "Lemmings exit door:";

	public static final String NUM_LEMMINGS = "Lemmings in board:";

	public static final String GAME_OVER = "Game over";

	public static final String PLAYER_QUITS = "Player leaves the game";

	public static final String PLAYER_WINS = "Player wins!";
	public static final String PLAYER_LOOSES = "Player looses...";

	public static final String POSITION = "(%s,%s)";

//Commands
	public static final String COMMAND_NONE_NAME = "none";
	public static final String COMMAND_NONE_SHORTCUT = "n";
	public static final String COMMAND_NONE_DETAILS = "[n]one | \"\"";
	public static final String COMMAND_NONE_HELP = "user does not perform any action";
	
	public static final String COMMAND_EXIT_NAME = "exit";
	public static final String COMMAND_EXIT_SHORTCUT = "e";
	public static final String COMMAND_EXIT_DETAILS = "[e]xit";
	public static final String COMMAND_EXIT_HELP = "exits the game";

	public static final String COMMAND_HELP_NAME = "help";
	public static final String COMMAND_HELP_SHORTCUT = "h";
	public static final String COMMAND_HELP_DETAILS = "[h]elp";
	public static final String COMMAND_HELP_HELP = "shows this help";
	
	public static final String COMMAND_LIST_NAME = "list";
	public static final String COMMAND_LIST_SHORTCUT = "l";
	public static final String COMMAND_LIST_DETAILS = "[l]ist";
	public static final String COMMAND_LIST_HELP = "prints the list of current lemming actions";

//Symbols
	public static final String EMPTY = "";
	public static final String WALL = MyStringUtils.repeat("▓",ConsoleView.CELL_SIZE);
	public static final String EXIT_DOOR = "🚪";
	public static final String LEMMING_RIGHT = "B";
	public static final String LEMMING_LEFT = "ᗺ";
	public static final String LEMMING_PARACHUTE = "🪂";
	public static final String LEMMING_BLOCKING = "¯\\ツ/¯";
	public static final String LEMMING_DOWN_CAVING = "´･ω･`";

//Actions
	public static final String WALKER_ACTION_SYMBOL = "W";
	public static final String WALKER_ACTION_DESCRIPTION = "Walker";
	public static final String WALKER_ACTION_DETAILS = "[W]alker";
	public static final String WALKER_ACTION_HELP = "Lemming that walks";

	public static final String PARACHUTE_ACTION_SYMBOL = "P";
	public static final String PARACHUTE_ACTION_DESCRIPTION = "Parachuter";
	public static final String PARACHUTE_ACTION_DETAILS = "[P]arachuter";
	public static final String PARACHUTE_ACTION_HELP = "Lemming falls with a parachute";

	public static final String DOWN_CAVING_ACTION_SYMBOL = "DC";
	public static final String DOWN_CAVING_ACTION_DESCRIPTION = "Down Caving";
	public static final String DOWN_CAVINGE_ACTION_DETAILS = "[D]own [C]aving";
	public static final String DOWN_CAVING_ACTION_HELP = "Lemming caves downwards";

	public static final String BLOCKING_ACTION_SYMBOL = "B";
	public static final String BLOCKING_ACTION_DESCRIPTION = "Blocking";
	public static final String BLOCKING_ACTION_DETAILS = "[B]locking";
	public static final String BLOCKING_ACTION_HELP = "Lemming blocks other lemmings";


}
