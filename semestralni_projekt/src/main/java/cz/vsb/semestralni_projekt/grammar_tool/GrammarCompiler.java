package cz.vsb.semestralni_projekt.grammar_tool;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GrammarCompiler implements PropertyLoader, ConsolePrinter {

    public Boolean compileGrammar(){
        writeToConsole(Colors.WHITE, "Compiling grammar files...");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticsCollector, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager
                .getJavaFileObjectsFromStrings( getFiles());
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticsCollector, null,
                null, compilationUnits);
        boolean success = task.call();

        if (!success) {
            List<Diagnostic<? extends JavaFileObject>> diagnostics = diagnosticsCollector.getDiagnostics();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
                System.out.println(diagnostic.getMessage(null));
            }

            writeToConsole(Colors.RED, "Compiling was not successful!");
        }
        else{
            writeToConsole(Colors.GREEN, "...compiling was successful.");
        }

        try {
            fileManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return success;
    }

    private ArrayList<String> getFiles(){
        File[] files = loadFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        for(File f : files)
            fileNames.add(f.getAbsolutePath());

        return fileNames;
    }

    private File[] loadFiles(){
        File dir = new File(getFolder());
        return dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(".java"));
    }

    private String getFolder(){
        String packageName = loadProperty("grammar.package");
        String output = loadProperty("grammar.outputDirectory");
        String outputWithPackage = packageName.replace(".", "/");
        return Paths.get(output, outputWithPackage).toString();
    }
}
