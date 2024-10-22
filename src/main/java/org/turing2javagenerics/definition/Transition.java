package org.turing2javagenerics.definition;

public record Transition(State stateFrom, AlphabetSymbol letterFrom,
  State stateTo, AlphabetSymbol letterTo, Direction direction) {

  public static Transition transition(final State stateFrom,
      final AlphabetSymbol letterFrom, final State stateTo,
      final AlphabetSymbol letterTo, final Direction direction) {
    return new Transition(stateFrom, letterFrom, stateTo, letterTo, direction);
  }

}
