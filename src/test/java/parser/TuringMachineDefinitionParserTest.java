package parser;

import org.generated.ParseException;
import org.generated.TuringMachineDefinitionParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.turing2javagenerics.definition.AlphabetSymbol;
import org.turing2javagenerics.definition.State;
import org.turing2javagenerics.definition.Transition;
import org.turing2javagenerics.definition.TuringMachineDefinition;
import turingMachineTests.BABTest;
import turingMachineTests.PalindromeTest;
import utils.TestUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.turing2javagenerics.definition.AlphabetSymbol.letter;
import static org.turing2javagenerics.definition.Direction.L;
import static org.turing2javagenerics.definition.Direction.R;
import static org.turing2javagenerics.definition.State.state;

public class TuringMachineDefinitionParserTest {

  /**
   * OK and KO are syntactically right, but KO is not coherent with the alphabet
   * usage in the transition definitions.
   */
  private static final String OK = "OK {\n" +
    "        INITIAL_STATE: a0\n" +
    "        HALT_STATE: c2\n"   +
    "        STATES : (b1)\n" +
    "        BLANK_SYMBOL: c\n" +
    "        ALPHABET : (a, b)\n" +
    "        TRANSITIONS {\n" +
    "                (a0, a) -> (b1, b, R)," +
    "                (b1, b) -> (c2, c, L),\n" +
    "                (c2, b) -> (a0, c, L)\n" +
    "        }\n" +
    "\n" +
    "}\n";

  private static final String KO = "KO {\n" +
    "        INITIAL_STATE: a0\n" +
    "        HALT_STATE: c2\n"   +
    "        STATES : (b1)\n" +
    "        BLANK_SYMBOL: c\n" +
    "        ALPHABET : (a, b)\n" +
    "        TRANSITIONS {\n" +
    "                (a0, a) -> (b1, wrong, R)," +
    "                (b1, b) -> (c2, c, L),\n" +
    "                (c2, b) -> (a0, c, L)\n" +
    "        }\n" +
    "\n" +
    "}\n";

  private static final State a0 = state("a0");
  private static final State b1 = state("b1");
  private static final State c2 = state("c2");

  private static final AlphabetSymbol a = letter("a");
  private static final AlphabetSymbol b = letter("b");
  private static final AlphabetSymbol c = letter("c");
  private static final AlphabetSymbol wrong = letter("wrong");

  private static final Transition t1 = Transition.transition(a0, a, b1, b, R);
  private static final Transition wrongTransition
    = Transition.transition(a0, a, b1, wrong, R);
  private static final Transition t2 = Transition.transition(b1, b, c2, c, L);
  private static final Transition t3 = Transition.transition(c2, b, a0, c, L);

  TuringMachineDefinition OKExpected = new TuringMachineDefinition("OK",
    List.of(a0, b1, c2),
    List.of(a, b, c),
    List.of(t1, t2, t3));

  TuringMachineDefinition KOExpected = new TuringMachineDefinition("KO",
    List.of(a0, b1, c2),
    List.of(a, b, c),
    List.of(wrongTransition, t2, t3));

  public static List<String> files() {
    return Arrays.stream(
        Objects.requireNonNull(
          new File(TestUtils.EXAMPLE_FILES_PATH).listFiles(File::isFile)))
             .map(File::getName).toList();
  }

  @Test
  public void testParseOK() {
    try {
      TuringMachineDefinition defOK
        = TuringMachineDefinitionParser.parseDef(OK);
      Assertions.assertEquals(OKExpected, defOK);
      Assertions.assertTrue(defOK.check());
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testParseKO() {
    try {
      TuringMachineDefinition defKO
        = TuringMachineDefinitionParser.parseDef(KO);
      Assertions.assertEquals(KOExpected, defKO);
      Assertions.assertFalse(defKO.check());
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  @ParameterizedTest
  @MethodSource("files")
  public void parseExamples(String file) {
    TestUtils.prepare(file);
  }

  @Test
  public void parseBABFileTest() {
    check(BABTest.TM, "bab.trng");
  }

  @Test
  public void parsePalindromeFileTest() {
    check(PalindromeTest.TM, "palindrome.trng");
  }

  public static void check(TuringMachineDefinition def, String file) {
    Assertions.assertEquals(def, TestUtils.prepare(file));
  }
}
