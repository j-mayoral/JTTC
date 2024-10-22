package org.turing2javagenerics;

import org.turing2javagenerics.definition.AlphabetSymbol;
import org.turing2javagenerics.definition.TuringMachineDefinition;
import org.turing2javagenerics.generator.JavaGenericCodeGenerator;

import java.util.Arrays;
import java.util.List;

public class Main {

  private static final String help =
    """
      args="<path-to-turing-machine-definition-file> <output-path> <input>"
      
        <path-to-turing-machine-definition-file>: path to a turing machine definition file.
        <output-path>: destination path where the generated Java code will be placed.
        <input>: input string on which the machine will run.
      
      Example:
        ./gradlew run --args="examples/bab.trng examples/output a a b a b a a"\s
      """;

  public static void main(String[] args) {
    if (args.length == 1 && "help".equals(args[0])) {
      System.out.println(help);
      System.exit(0);
    }
    if (args.length < 3) {
      System.out.println("Not enough input arguments.");
      System.exit(1);
    }
    String source = args[0];
    String target = args[1];
    List<AlphabetSymbol> input = AlphabetSymbol.alphabet(
      Arrays.copyOfRange(args, 2, args.length));
    Report<TuringMachineDefinition> def = TuringMachineDefinition.parseFile(source);
    if(!def.isOk()) {
      System.out.println(def.getError());
      System.exit(-1);
    }
    Report<Void> report = JavaGenericCodeGenerator.toFile(target, def.get(), input);
    if (!report.isOk()) {
      System.out.println(report.getError());
      System.exit(-1);
    }
  }
}