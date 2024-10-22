package compiler;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CompilerTool {

  private final String target;
  private final String source;

  private final JavaCompiler compiler;
  private final DiagnosticCollector<JavaFileObject> diagnostics;
  private final StandardJavaFileManager fileManager;


  public CompilerTool(String target, String source) {
    this.target = target;
    this.source = source;

    compiler = ToolProvider.getSystemJavaCompiler();
    diagnostics = new DiagnosticCollector<JavaFileObject>();
    fileManager = compiler.getStandardFileManager(diagnostics, null, null);
  }

  public boolean compile() {
    Iterable<? extends JavaFileObject> compilationUnits = fileManager
      .getJavaFileObjectsFromPaths(List.of(Paths.get(source)));
    File file = createDir();
    try {
      return compile(file, compilationUnits);
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
    deleteChildrenFiles(file);
    return success;
  }

  private File createDir() {
    File file = new File(target);
    file.mkdir();
    return file;
  }

  private static void deleteChildrenFiles(File file) {
    for (File f : file.listFiles()) deleteDir(f);
  }

  private static void deleteDir(File file) {
    File[] contents = file.listFiles();
    if (contents != null) {
      for (File f : contents) {
        deleteDir(f);
      }
    }
    file.delete();
  }
}
