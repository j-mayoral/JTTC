package org.turing2javagenerics.generator;

import org.turing2javagenerics.Report;
import org.turing2javagenerics.definition.AlphabetSymbol;
import org.turing2javagenerics.definition.State;
import org.turing2javagenerics.definition.Transition;
import org.turing2javagenerics.definition.TuringMachineDefinition;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public interface JavaGenericCodeGenerator<T> {

  public Report<T> transform(TuringMachineDefinition def,
    List<AlphabetSymbol> input);

  public static final JavaGenericCodeGenerator<String> TO_STRING_REPORT =
    (def, input) ->
      (def.check())
        ? new Report.Ok<>(new CodeWriter(def, input).getCode())
        : new Report.Ko<>("Incoherent turing machine definition");

  public static Report<Void> toFile(final String path,
      final TuringMachineDefinition def, final List<AlphabetSymbol> input) {
    return new ToFile(path).transform(def, input);
  }

  public static class ToFile implements JavaGenericCodeGenerator<Void> {

    private final String path;

    public ToFile(final String path) {
      this.path = path;
    }

    @Override
    public Report<Void> transform(final TuringMachineDefinition def,
        final List<AlphabetSymbol> input) {
      final String fileName = def.machineName() + ".java";
      final String filePath = path + "/" + fileName;
      final String packageName = String.valueOf(Paths.get(path).getFileName());
      Report<String> code = TO_STRING_REPORT.transform(def, input);
      if (!code.isOk()) return new Report.Ko<>(code.getError());
      final String content = "package " + packageName + ";\n\n"
                               + code.get();
      try {
        Files.writeString(Path.of(filePath), content);
      } catch (IOException e) {
        return new Report.Ko<>("Unable to open or write in file "
            + filePath + ": " + e);
      }
      return new Report.Ok<>(null);
    }
  }

  public static class CodeWriter {
    private final List<AlphabetSymbol> input;
    private final StringBuilder sb;
    private final String machineName;
    private final List<AlphabetSymbol> sigma;
    private final List<State> states;
    private final Collection<Transition> transitions;

    public CodeWriter(final TuringMachineDefinition def,
        final List<AlphabetSymbol> input) {
      this.input = input;
      this.sb = new StringBuilder();
      machineName = def.machineName();
      sigma = def.alphabet();
      states = def.states();
      transitions = def.transitions();

    }

    public String getCode() {
      open();
      declareRun(input);
      start();
      stop();
      alphabetMethods();
      auxClasses();
      classE();
      stateClasses();
      letterClasses();
      stateWCLasses();
      transitions();
      close();
      return sb.toString();
    }

    private void transitions() {
      sb.append(
        TransitionFunctionsCodeGenerator.TO_STRING.transform(states, transitions));
    }

    private void close() {
      sb.append("}\n");
    }

    private void stateWCLasses() {
      final Set<String> LR = Set.of("L", "R");
      for (final String s : LR) {
        for (int i = 0; i < states.size(); i++) {
          final String name = states.get(i).name();
          switch (s) {
            case "L":
              sb
                .append("public interface ").append(name)
                .append("WL<T> extends\n")

                .append("ML<N< ? super ").append(name)
                .append("L<? super T>>>").append(",\n")

                .append("MR<N<? super ").append(name)
                .append("WL<? super MR<? super N<? super T>>>>>").append(",\n");
              break;
            case "R":
              sb
                .append("public interface ").append(name)
                .append("WR<T> extends\n")

                .append("MR<N< ? super ").append(name)
                .append("R<? super T>>>").append(",\n")

                .append("ML<N<? super ").append(name)
                .append("WR<? super ML<? super N<? super T>>>>>").append(",\n");
              break;
          }
          for (final AlphabetSymbol letter : sigma) {
            String letterName = letter.name();
            sb.append("L").append(letterName).append("<N<? super ").append(name)
              .append("W").append(s).append("<? super L").append(letterName)
              .append("<? super N<? super T>>>>>").append(",\n");
          }
          if (last(i)) {
            sb.append("E<E<? super  Z>>{}\n");
          } else {
            sb.append("E<").append(name).append((s.equals("L")) ? "LR" : "RL")
              .append("<? super N<? super T>>>{}\n");
          }
        }
      }
    }

    private boolean last(final int i) {
      return i == states.size() - 1;
    }

    private void letterClasses() {
      for (final AlphabetSymbol letter : sigma) {
        sb.append("public interface L").append(letter.name()).append("<T> {}\n");
      }
    }

    private void stateClasses() {
      for (final State q : states) {
        sb.append("public interface ").append(q.name()).append("LR<T> {}\n")
          .append("public interface ").append(q.name()).append("RL<T> {}\n");
      }
    }

    private void classE() {
      sb.append("public interface E<T> extends\n");
      sb.append(states.stream().map(q ->
        q.name() + "LR<N<? super " + q.name() +
          "WR<? super E<? super E<? super T>>>>>" + ",\n" +
          q.name() + "RL<N<? super " + q.name() +
          "WL<? super E<? super E<? super T>>>>>"
      ).collect(Collectors.joining(",\n", "", "{}\n")));
    }

    private void auxClasses() {
      sb.append(
        "public interface N<T> {}\n" +
          "public interface ML<T> {}\n" +
          "public interface MR<T>{}\n" +
          "public interface Z {}\n");
    }

    private void alphabetMethods() {
      for (final AlphabetSymbol s : sigma) {
        sb.append("abstract ").append(machineName).append("<L").append(s.name())
          .append("<? super N<? super T>>>")
          .append(s.name()).append("();\n");
      }
    }

    private void stop() {
      sb.append("abstract ").append(states.get(0).name())
        .append("WR<? super L").append(sigma.get(sigma.size() - 1).name())
        .append("<? super N<? super T>>> stop();\n");
    }

    private void start() {
      sb.append("static ").append(machineName).append("<ML<? super N<? super L")
        .append(sigma.get(sigma.size() - 1).name())
        .append("<? super N<? super E<? super E<? super Z>>>>>>> start;\n");
    }

    private void declareRun(List<AlphabetSymbol> letters) {
      sb.append("public  E<? super E<? super Z>> run() {\n")
        .append("return start.");
      sb.append(letters.stream().map(s -> s.name().toLowerCase() + "()")
        .collect(Collectors.joining(".", "", ".")));
      sb.append("stop();\n").append("}\n");
    }

    private void open() {
      sb.append("public abstract class ").append(machineName).append("<T> {\n");
    }
  }
}
