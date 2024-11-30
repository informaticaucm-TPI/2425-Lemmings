package tp1;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.AnnotatedType;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import tp1.control.commands.NoParamsCommand;
import tp1.control.commands.ResetCommand;

public class Tests_V3_1 {
	public static final String DIR =
			doSave()?
				"tests/pr3+reset+save/"
				: doReset() ? 
						"tests/pr3+reset/"
						: "tests/pr3/";

	private static boolean doReset() {
		Class<?> clazz = ResetCommand.class;
		Class<?> superClazz = clazz.getSuperclass();
		return !superClazz.equals(NoParamsCommand.class);
	}
	private static boolean doSave() {
		try {
			Class.forName("tp1.control.commands.SaveCommand");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static final String FILE_PREFIXES[] = {
			"00_1-play"
			, "01_1-command", "01_2-play"
			, "01_3-newRoles"
			, "01_4-newRoles_errors"
			, "02_1-newRoles"
			, "01_5-newRoles_conf_0"
			, "01_6-file_errors"
			, "01_7-file_reset"
			, "02_2-reset_load_save"
			};

	private static boolean areSegmentsEquivalent(String segment1, String segment2) {
	    // Ordena los caracteres de ambos segmentos y los compara
	    return sortCharacters(segment1.trim()).equals(sortCharacters(segment2.trim()));
	}

	private static String sortCharacters(String segment) {
	    return segment.chars()
	                  .sorted()
	                  .collect(StringBuilder::new,
	                           StringBuilder::appendCodePoint,
	                           StringBuilder::append)
	                  .toString();
	}

	
	public static boolean areLinesEquivalent(String expectedLine, String actualLine) {
        // Dividir ambas líneas por los delimitadores '|'
        String[] expectedParts = expectedLine.split("┃");
        String[] actualParts = actualLine.split("┃");

        // 1. Ambas líneas deben tener exactamente dos delimitadores
        if (expectedParts.length != 3 || actualParts.length != 3) {
            return false;
        }

        // 2. El contenido fuera de los delimitadores debe ser idéntico
        if (!expectedParts[0].equals(actualParts[0]) || !expectedParts[2].equals(actualParts[2])) {
            return false;
        }

        // 3. El contenido dentro de los delimitadores debe dividirse en DIM_X trozos
        String[] expectedSegments = splitIntoSegments(expectedParts[1], tp1.logic.Game.DIM_X);
        String[] actualSegments = splitIntoSegments(actualParts[1], tp1.logic.Game.DIM_X);

        if (expectedSegments == null || actualSegments == null) {
            return false; // Si no se pueden dividir correctamente
        }

        // Comparar cada segmento entre ambas líneas
        for (int i = 0; i < tp1.logic.Game.DIM_X; i++) {
            if (!areSegmentsEquivalent(expectedSegments[i], actualSegments[i])) {
            	//System.out.println("Expected segment: " + expectedSegments[i]);
            	//System.out.println("Actual   segment: " + actualSegments[i]);
                return false;
            }
        }

        // Si todas las condiciones se cumplen, las líneas son equivalentes
        return true;
    }

    private static String[] splitIntoSegments(String content, int segments) {
        if (content.length() % segments != 0) {
            return null; // No se puede dividir en partes iguales
        }

        int segmentLength = content.length() / segments;
        String[] result = new String[segments];

        for (int i = 0; i < segments; i++) {
            result[i] = content.substring(i * segmentLength, (i + 1) * segmentLength);
        }

        return result;
    }
	
    
	
	private static boolean compareOutput(Path expectedPath, Path actualPath) throws FileNotFoundException, IOException {
		boolean same = true;
		try (BufferedReader expected = new BufferedReader(new FileReader(expectedPath.toFile()));
				BufferedReader actual = new BufferedReader(new FileReader(actualPath.toFile()))) {

			String expectedLine = expected.readLine();
			String actualLine = actual.readLine();
			int lineNumber = 1;
			while (same && expectedLine != null && actualLine != null) {
				same = expectedLine.equals(actualLine);
				if (!same) {
					if(!areLinesEquivalent(expectedLine,actualLine)){
						String lineMessage = "Line: %d%n".formatted(lineNumber);
						String expectedMessage = "Expected: %s%n".formatted(expectedLine);
						String actualMessage = "Actual  : %s%n".formatted(actualLine);
						System.out.println(lineMessage + expectedMessage + actualMessage);
						
						fail(lineMessage + expectedMessage + actualMessage);
					}
					else
						same = true;
				}
				expectedLine = expected.readLine();
				actualLine = actual.readLine();
				lineNumber++;
			}

			same = same && expectedLine == null && actualLine == null;
		}
		return same;
	}

	public void parameterizedTest(Path input, Path expected, Path output, String[] args) {
		try (PrintStream out = new PrintStream(output.toFile()); InputStream in = new FileInputStream(input.toFile())) {
			PrintStream oldOut = System.out;
			InputStream oldIn = System.in;

			System.setOut(out);
			System.setIn(in);

			Main.main(args);

			System.setOut(oldOut);
			System.setIn(oldIn);

			if (!compareOutput(expected, output)) {
				fail();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e1) {
			e1.printStackTrace();
			fail();
		}
	}

	private void testN(int n) {
		String mapa = FILE_PREFIXES[n].substring(0, 2);
		parameterizedTest(Paths.get(DIR + FILE_PREFIXES[n] + "_input.txt"), 
				          Paths.get(DIR + FILE_PREFIXES[n] + "_expected.txt"),
				          Paths.get(DIR + FILE_PREFIXES[n] + "_output.txt"),
				new String[] { mapa, "NO_COLORS" });
	}
	
	@Test
	public void test00() { 	testN(0); }
	@Test
	public void test01() { 	testN(1); }
	@Test
	public void test02() { 	testN(2); }
	@Test
	public void test03() { 	testN(3); }
	@Test
	public void test04() { 	testN(4); }
	@Test
	public void test05() { 	testN(5); }
	@Test
	public void test06() { 	testN(6); }
	@Test
	public void test07() { 	testN(7); }
	@Test
	public void test08() { 	testN(8); }
	@Test
	public void test09() { 	testN(9); }
}