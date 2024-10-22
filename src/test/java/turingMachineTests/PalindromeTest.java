package turingMachineTests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.turing2javagenerics.definition.AlphabetSymbol;
import org.turing2javagenerics.definition.State;
import org.turing2javagenerics.definition.Transition;
import org.turing2javagenerics.definition.TuringMachineDefinition;
import utils.TestUtils;

import java.util.List;

import static org.turing2javagenerics.definition.AlphabetSymbol.letter;
import static org.turing2javagenerics.definition.Direction.L;
import static org.turing2javagenerics.definition.Direction.R;
import static org.turing2javagenerics.definition.State.state;
import static org.turing2javagenerics.definition.Transition.transition;
import static utils.TestUtils.Result.KO;
import static utils.TestUtils.Result.OK;
import static utils.TestUtils.pair;

/**
 * Check if a string of as and bs is a palindrome
 */
public class PalindromeTest {

  private static final String machineName = "Palindrome";

  private static TuringMachineDefinition fromFile = null;

  private static final State init = state("init");
  private static final State q1 = state("q1");
  private static final State q2 = state("q2");
  private static final State q3 = state("q3");
  private static final State q4 = state("q4");
  private static final State q5 = state("q5");
  private static final State q6 = state("q6");
  private static final State halt = state("halt");

  private static final List<State> states
    = List.of(init, q1, q2, q3, q4, q5, q6, halt);

  private static final AlphabetSymbol a = letter("a");
  private static final AlphabetSymbol b = letter("b");
  private static final AlphabetSymbol $ = letter("$");

  private static final List<AlphabetSymbol> alphabet
    = List.of(a, b, $);

  private static final List<Transition>
    transitions =
      List.of(
          transition(init, $, q1, $, R),

          transition(q1, a, q2, $, R),
          transition(q1, b, q5, $, R),
          transition(q1, $, halt, $, R),

          transition(q2, a, q2, a, R),
          transition(q2, b, q2, b, R),
          transition(q2, $, q3, $, L),

          transition(q3, $, halt, $, R),
          transition(q3, a, q4, $, L),

          transition(q4, a, q4, a, L),
          transition(q4, b, q4, b, L),
          transition(q4, $, q1, $, R),

          transition(q5, a, q5, a, R),
          transition(q5, b, q5, b, R),
          transition(q5, $, q6, $, L),

          transition(q6, b, q4, $, L),
          transition(q6, $, halt, $, R)
      );

  public static final TuringMachineDefinition TM =
      new TuringMachineDefinition(machineName, states, alphabet, transitions);

  public static List<TestUtils.ResultPair> expectedPairs() {
    return List.of(
      pair(OK, a, b, b, a),
      pair(OK, a, a, a, a),
      pair(OK, a),

      pair(KO, a, b, b),
      pair(KO, a, a, a, b),
      pair(KO, b, a, a, b, a)
    );
  }

  @ParameterizedTest
  @MethodSource("expectedPairs")
  public void test(final TestUtils.ResultPair result) {
    TestUtils.check(TM, result.input(), result.expected());
    TestUtils.check(fromFile, result.input(), result.expected());
  }

  @BeforeAll
  public static void prepare() {fromFile = TestUtils.prepare("palindrome.trng");}
}
