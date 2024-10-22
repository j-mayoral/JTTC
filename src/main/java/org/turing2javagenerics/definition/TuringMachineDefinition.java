package org.turing2javagenerics.definition;

import org.generated.ParseException;
import org.generated.TuringMachineDefinitionParser;
import org.turing2javagenerics.Report;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public record TuringMachineDefinition(String machineName,
    List<State> states, List<AlphabetSymbol> alphabet,
    List<Transition> transitions) {

  public static Report<TuringMachineDefinition> parseFile(final String source) {
    try {
      return new Report.Ok<>(TuringMachineDefinitionParser.parseFile(source));
    } catch (FileNotFoundException e) {
      return new Report.Ko<>("Error processing file: " + source + ": " + e);
    } catch (ParseException e) {
      return new Report.Ko<>("Parsing error in file " + source + ": " + e);
    }
  }

  public boolean check() {
    return noRepeated(states) && noRepeated(alphabet)
      && coherentTransitions();
  }

  private static boolean noRepeated(final List<?> states) {
    return states.size() == new HashSet<>(states).size();
  }

  private boolean coherentTransitions() {
    HashSet<State> statesSet = new HashSet<>(states);
    HashSet<AlphabetSymbol> alphabetSet = new HashSet<>(alphabet);
    for (Transition transition : transitions) {
      if (!(statesSet.contains(transition.stateFrom())
              && statesSet.contains(transition.stateTo())
              && alphabetSet.contains(transition.letterFrom())
              && alphabetSet.contains(transition.letterTo()))
      ) return false;
    }
    return true;
  }
}