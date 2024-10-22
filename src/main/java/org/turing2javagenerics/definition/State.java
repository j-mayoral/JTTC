package org.turing2javagenerics.definition;

import java.util.Arrays;
import java.util.Objects;

public record State(String name) {
  public static State state(final String name) {return new State(name);}
}
