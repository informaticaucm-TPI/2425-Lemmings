package tp1;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class Tests {
	public static final String DIR = "tests/pr22/";
	public static final String FILE_PREFIXES[] = {
			"00_1-play"
			, "01_1-command", "01_2-play"
			, "01_3-newRoles"
			, "01_4-newRoles_errors"
			, "02_1-newRoles"
			};

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
					System.out.println("Line: %d".formatted(lineNumber));
					System.out.println("Expected: %s".formatted(expectedLine));
					System.out.println("Actual: %s".formatted(actualLine));
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

}
