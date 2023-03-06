package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Runs a couple of tests to make sure Log4j2 is setup.
 *
 * NOTE: There are better ways to test log configuration---we will keep it
 * simple here because we just want to make sure you can run and configure
 * Log4j2.
 *
 * This is also not the most informative configuration---it is just one of the
 * most testable ones that require you to learn about how to handle stack trace
 * output.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2023
 */
@TestClassOrder(ClassOrderer.ClassName.class)
public class LoggerSetupTest {
	/** Used to capture console output. */
	private static List<String> captured = null;

	/** Tracks where the root output starts. */
	private static int classStart = -1;

	/** Path to expected debug.log file. */
	private static Path debug = Path.of("src", "test", "resources", "debug.txt");

	/**
	 * Setup that runs before each test.
	 *
	 * @throws IOException if an I/O error occurs
	 */
	@BeforeAll
	public static void setup() throws IOException {
		// delete any old debug files
		Files.deleteIfExists(Path.of("debug.log"));

		// make sure class is expected # of lines
		String name = LoggerSetup.class.getSimpleName() + ".java";
		Path java = Path.of("src", "main", "java");
		Path cs272 = Path.of("edu", "usfca", "cs272");
		Path file = java.resolve(cs272).resolve(name);
		long lines = Files.lines(file).count();
		Assumptions.assumeTrue(lines == 47, "Aborting tests; it looks like you modified the " +
		"LoggerSetup file! Make sure to restore it to the original version.");

		// capture all system console output
		PrintStream original = System.out;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		System.setOut(new PrintStream(output));

		// run main() only ONCE to avoid duplicate entries
		// and shutdown log manager to flush the debug files
		LoggerSetup.main(null);
		LogManager.shutdown();

		// restore system.out
		System.setOut(original);

		// save result and output to console
		captured = output.toString().lines().map(String::strip).toList();
		classStart = Collections.indexOfSubList(captured, List.of("Class Logger:"));

		captured.forEach(System.out::println);
	}

	/**
	 * Tests configuration file location and name.
	 */
	@Nested
	@TestMethodOrder(OrderAnnotation.class)
	public class A_ConfigFileTests {
		/**
		 * Make sure you are not using the log4j2-test.* name. That is the config file
		 * name used for test code not main code.
		 *
		 * @throws IOException if IO error occurs
		 */
		@Test
		@Order(1)
		public void testNotTest() throws IOException {
			var found = Files.walk(Path.of("."))
					.filter(Files::isRegularFile)
					.map(Path::getFileName)
					.map(Path::toString)
					.filter(name -> name.matches("log4j2-test\\.\\w+"))
					.toList();

			assertEquals(Collections.emptyList(), found,
					"Do not use this filename to configure logging of main code.");
		}

		/**
		 * Make sure you are using the correct file name.
		 *
		 * @throws IOException if IO error occurs
		 */
		@Test
		@Order(2)
		public void testNameCorrect() throws IOException {
			var found = Files.walk(Path.of("."))
					.filter(Files::isRegularFile)
					.map(Path::getFileName)
					.map(Path::toString)
					.filter(name -> name.matches("log4j2\\.\\w+"))
					.toList();

			assertTrue(!found.isEmpty(), "Could not find configuration file with correct name.");
		}

		/**
		 * Make sure you are using the correct location for the configuration file.
		 *
		 * @throws IOException if IO error occurs
		 */
		@Test
		@Order(3)
		public void testLocationCorrect() throws IOException {
			var found = Files.walk(Path.of("src", "main", "resources"))
					.filter(Files::isRegularFile)
					.map(Path::getFileName)
					.map(Path::toString)
					.filter(name -> name.matches("log4j2.*\\.\\w+"))
					.toList();

			assertTrue(!found.isEmpty(), "Could not find configuration file in the correct location.");
		}

		/**
		 * Make sure you are using the correct extension for the configuration file.
		 *
		 * @throws IOException if IO error occurs
		 */
		@Test
		@Order(4)
		public void testExtensionCorrect() throws IOException {
			var found = Files.walk(Path.of("."))
					.filter(Files::isRegularFile)
					.map(Path::getFileName)
					.map(Path::toString)
					.filter(name -> name.matches("(?i)log4j2.*\\.(properties|ya?ml|jso?n|xml)"))
					.toList();

			assertTrue(!found.isEmpty(), "Could not find configuration file with the correct extension.");
		}
	}

	/**
	 * Tests Root logger console output.
	 */
	@Nested
	public class B_RootConsoleTests {
		/**
		 * Tests the root logger console output and compares to expected.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		public void testRootConsole() throws IOException {
			if (classStart < 0) {
				fail("Could not find root logger vs class logger console output.");
			}

			String actual = String.join("\n", captured.subList(1, classStart));

			String expected = """
					INFO: Ibis
					WARN: Wren
					ERROR: Eastern Eagle
					FATAL: Catching Falcon
					""";

			assertEquals(expected.strip(), actual.strip());
		}
	}

	/**
	 * Tests LoggerSetup logger console output.
	 */
	@Nested
	public class C_LoggerConsoleTests {
		/**
		 * Captures the console output and compares to expected.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		public void testClassConsole() throws IOException {
			if (classStart < 0) {
				fail("Could not find root logger vs class logger console output.");
			}

			String actual = String.join("\n", captured.subList(classStart + 1, captured.size()));
			String expected = "FATAL: Catching Falcon";

			assertEquals(expected.strip(), actual.strip());
		}
	}

	/**
	 * Tests part of file output.
	 */
	@Nested
	public class D_DebugFileTests {
		/**
		 * Tests that the expected levels are in the output file.
		 *
		 * @param expected the expected level output to find in the debug file
		 * @throws IOException if unable to read debug file
		 */
		@ParameterizedTest
		@ValueSource(strings = { "Turkey", "Duck", "Ibis", "Wren", "Eagle", "Falcon" })
		public void testLevels(String expected) throws IOException {
			String actual = Files.readString(Path.of("debug.log"), UTF_8);
			String regex = String.format("(?s).*?\\b%s\\b.*?", expected);
			assertTrue(actual.matches(regex), "Unable to find in log file: " + expected);
		}
	}

	/**
	 * Tests part of file output.
	 */
	@Nested
	public class E_FileNormalLayoutTests {
		/**
		 * Open the debug.log file as a list and compare to expected.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		public void testFile() throws IOException {
			Predicate<String> filter = line -> line.matches("(?i)\\[.*?\\b(TR|DE|IN|WA).*?\\].*?");

			// only test the non-exception output from files
			String expected = Files.lines(debug, UTF_8)
					.filter(filter)
					.map(String::stripTrailing)
					.collect(Collectors.joining("\n"));

			String actual = Files.lines(Path.of("debug.log"), UTF_8)
					.filter(filter)
					.map(String::stripTrailing)
					.collect(Collectors.joining("\n"));

			assertEquals(expected, actual, "Compare debug.log and test/resources/debug.txt in Eclipse.");
		}
	}

	/**
	 * Tests part of file output.
	 */
	@Nested
	public class F_FileExceptionTests {
		/**
		 * Open the debug.log file as a list and compare to expected.
		 *
		 * @throws IOException if an I/O error occurs
		 */
		@Test
		public void testFile() throws IOException {
			Predicate<String> filter = line -> line.matches("(?i).*?\\b(ER|FA|at)\\w*?\\b.*?");

			// only test the exception output from files
			String expected = Files.lines(debug, UTF_8)
					.filter(filter)
					.map(String::stripTrailing)
					.collect(Collectors.joining("\n"));

			String actual = Files.lines(Path.of("debug.log"), UTF_8)
					.filter(filter)
					.map(String::stripTrailing)
					.collect(Collectors.joining("\n"));

			assertEquals(expected, actual, "Compare debug.log and test/resources/debug.txt in Eclipse.");
		}
	}
}
