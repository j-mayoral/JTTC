package turingMachineTests;

import org.generated.ParseException;
import org.generated.TuringMachineDefinitionParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.turing2javagenerics.definition.AlphabetSymbol;
import org.turing2javagenerics.definition.State;
import org.turing2javagenerics.definition.Transition;
import org.turing2javagenerics.definition.TuringMachineDefinition;
import utils.TestUtils;

import java.util.List;

import static org.turing2javagenerics.definition.AlphabetSymbol.letter;
import static org.turing2javagenerics.definition.Direction.R;
import static org.turing2javagenerics.definition.State.state;
import static org.turing2javagenerics.definition.Transition.transition;
import static utils.TestUtils.Result.KO;
import static utils.TestUtils.Result.OK;
import static utils.TestUtils.pair;

/**
 * Check if a String of as and bs contains the substring bab.
 */
public class BABTest {

  private static TuringMachineDefinition fromFile = null;
  private static final String machineName = "BAB";

  private static final State init = state("init");
  private static final State q0 = state("q0");
  private static final State q1 = state("q1");
  private static final State q2 = state("q2");
  private static final State halt = state("halt");

  private static final AlphabetSymbol a = letter("a");
  private static final AlphabetSymbol b = letter("b");
  private static final AlphabetSymbol $ = letter("$");

  private static final List<AlphabetSymbol> alphabet = List.of(a, b , $);
  private static final List<State> states = List.of(init, q0, q1, q2, halt);

  private static final List<Transition> TRANSITIONS =
      List.of(
          transition(init, $, q0, $, R),
          transition(q0, a, q0, a, R),
          transition(q0, b, q1, b, R),
          transition(q1, a, q2, a, R),
          transition(q1, b, q1, b, R),
          transition(q2, a, q0, a, R),
          transition(q2, b, halt, b, R)
          );
  public static final TuringMachineDefinition TM =
  new TuringMachineDefinition(machineName, states, alphabet, TRANSITIONS);

  public static List<TestUtils.ResultPair> expectedPairs() {
    return List.of(
      pair(OK, b, a, b),
      pair(OK, a, a, a, b, a, b),
      pair(OK, a, b, a, b, b, b, a),

      pair(KO, a, b),
      pair(KO, b, b, b, a, a),
      pair(KO, b)
    );
  }

  @ParameterizedTest
  @MethodSource("expectedPairs")
  public void test(final TestUtils.ResultPair expected) {
    TestUtils.check(TM, expected.input(), expected.expected());
    TestUtils.check(fromFile, expected.input(), expected.expected());
  }

  @BeforeAll
  public static void prepare() {fromFile = TestUtils.prepare("bab.trng");}
}
