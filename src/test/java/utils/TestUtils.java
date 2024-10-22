package utils;

import compiler.CompilerTool;
import org.generated.ParseException;
import org.generated.TuringMachineDefinitionParser;
import org.junit.jupiter.api.Assertions;
import org.turing2javagenerics.definition.AlphabetSymbol;
import org.turing2javagenerics.definition.TuringMachineDefinition;
import org.turing2javagenerics.generator.JavaGenericCodeGenerator;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;


public class TestUtils {

  public static final String TRASH_PATH = "src/test/java/trash";

  public static final String EXAMPLE_FILES_PATH = "examples/";

  public static void check(final TuringMachineDefinition tm,
      final List<AlphabetSymbol> input, final Result result) {

    new JavaGenericCodeGenerator.ToFile(TestUtils.TRASH_PATH).apply(tm, input);

    boolean success = new CompilerTool(TestUtils.TRASH_PATH,
      TestUtils.TRASH_PATH + "/" + tm.machineName() + ".java").compile();

    Assertions.assertEquals(result.toBool(), success);
  }

  public enum Result {
    OK {@Override public boolean toBool() {return true;}},

    KO {@Override public boolean toBool() {return false;}};

    public abstract boolean toBool();
  }

  public record ResultPair(Result expected, List<AlphabetSymbol> input){}

  public static ResultPair pair(Result result, AlphabetSymbol... input) {
    return new ResultPair(result, Arrays.asList(input));
  }

  public static TuringMachineDefinition prepare(final String source) {
    TuringMachineDefinition def;
    try {
      def = TuringMachineDefinitionParser.parseFile(EXAMPLE_FILES_PATH + source);
    } catch (ParseException | FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    return def;
  }
}
