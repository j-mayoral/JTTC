package org.turing2javagenerics.generator;

import org.turing2javagenerics.definition.*;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public interface TransitionFunctionsCodeGenerator<T> {

  public String transform(final Collection<State> states, final Collection<Transition> transitions);

  public static final TransitionFunctionsCodeGenerator<String> TO_STRING =
    (states, transitions) -> {
      StringBuilder sb = new StringBuilder();
      Flatten flatten = new Flatten();
      flatten.read(transitions);
      for (final State q : states) flatten.toJavaGeneric(q, sb);
      return sb.toString();
    };


  static class Flatten {

    private final Map<State, Collection<Target>> map;

    public Flatten() {
      this.map = new HashMap<>();
    }

    public void read(Collection<Transition> trs) {
      for (final Transition tr : trs) flat(tr);
    }

    private void flat(Transition tr) {
      final Collection<Target> target = map.get(tr.stateFrom());
      if (target != null) {
        target.add(Target.of(tr));
      } else {
        final Set<Target> v = new HashSet<>();
        v.add(Target.of(tr));
        map.put(tr.stateFrom(), v);
      }
    }

    private void toJavaGeneric(final State q, final StringBuilder sb) {
      if (map.get(q) != null && !map.get(q).isEmpty()) {
        sb.append("public interface ").append(q.name())
          .append("L<T> extends\n");
        sb.append(map.get(q).stream()
                    .map(this::swl)
                    .collect(Collectors.joining(",\n", "", "{}\n")));
        sb.append("public interface ").append(q.name())
          .append("R<T> extends\n");
        sb.append(map.get(q).stream()
                    .map(this::swr)
                    .collect(Collectors.joining(",\n", "", "{}\n")));
      } else {
        sb.append("public interface ").append(q.name()).append("L<T>{}\n");
        sb.append("public interface ").append(q.name()).append("R<T>{}\n");
      }
    }

    private String swl(final Target tr) {
      final var letterFrom = tr.letterFrom().name();
      final var letter = tr.letterTo().name();
      final var stateTo = tr.stateTo().name();
      return switch (tr.direction()) {
        case L -> "L" + letterFrom
                    + "<N<? super " + stateTo + "WL<? super ML"
                    + "<? super N<? super L" + letter
                    + "<? super N<? super T>>>>>>>";
        case R -> "L" + letterFrom
                    + "<N<? super " + stateTo + "WL<? super L" + letter
                    + "<? super N<? super MR<? super N<? super T>>>>>>>";
      };
    }

    private String swr(final Target tr) {
      final var letterFrom = tr.letterFrom().name();
      final var letter = tr.letterTo().name();
      final var stateTo = tr.stateTo().name();
      return switch (tr.direction()) {
        case L -> "L" + letterFrom + "<N<? super " + stateTo + "WR<? super L"
                    + letter + "<? super N<? super ML<? super N<? super T>>>>>>>";
        case R -> "L" + letterFrom + "<N<? super " + stateTo + "WR<? super MR"
                    + "<? super N<? super L" + letter
                    + "<? super N<? super T>>>>>>>";
      };
    }
  }
}
