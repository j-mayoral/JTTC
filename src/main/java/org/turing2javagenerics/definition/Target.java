package org.turing2javagenerics.definition;

public record Target(AlphabetSymbol letterFrom, State stateTo,
  AlphabetSymbol letterTo, Direction direction) {

  public static Target of(final Transition tr) {
    return new Target(tr.letterFrom(), tr.stateTo(), tr.letterTo(), tr.direction());
  }
}
