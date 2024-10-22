package org.turing2javagenerics.definition;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record AlphabetSymbol(String name) {
  public static List<AlphabetSymbol> alphabet(final String... symbols) {
    return Arrays.stream(symbols).map(AlphabetSymbol::letter).toList();
  }
  public static AlphabetSymbol letter(final String name) {
    return new AlphabetSymbol(name);
  }
}
