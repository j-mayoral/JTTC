package compiler;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class CompilerTool {

  private final File target;
  private final String source;

  private final JavaCompiler compiler;
  private final DiagnosticCollector<JavaFileObject> diagnostics;
  private final StandardJavaFileManager fileManager;


  public CompilerTool(File target, String source) {
    this.target = target;
    this.source = source;

    compiler = ToolProvider.getSystemJavaCompiler();
    diagnostics = new DiagnosticCollector<JavaFileObject>();
    fileManager = compiler.getStandardFileManager(diagnostics, null, null);
  }

  public boolean compile() {
    Iterable<? extends JavaFileObject> compilationUnits = fileManager
      .getJavaFileObjectsFromPaths(List.of(Paths.get(source)));
    try {
      return compile(target, compilationUnits);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean compile(File file,
      Iterable<? extends JavaFileObject> compilationUnits) throws IOException {
    fileManager.setLocation(
      StandardLocation.CLASS_OUTPUT,
      List.of(file));
    JavaCompiler.CompilationTask task
      = compiler.getTask(null, fileManager, diagnostics, null,
      null, compilationUnits);
    boolean success = task.call();
    fileManager.close();
    return success;
  }
}
