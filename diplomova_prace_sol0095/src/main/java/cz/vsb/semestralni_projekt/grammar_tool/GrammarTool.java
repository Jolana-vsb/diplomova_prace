package cz.vsb.semestralni_projekt.grammar_tool;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class GrammarTool implements PropertyLoader, ConsolePrinter {
    private String grammarName;
    private TreeToXML treeToXML;
    private Class<?> lexerClass;
    private Class<?> parserClass;
    private ExecutorService service;
    private GrammarApplier grammarApplier;

    public GrammarTool(){
        grammarName = loadProperty("grammar.name");
        treeToXML = new TreeToXML();
        service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        grammarApplier = new GrammarApplier();
    }

    public void runTool(){
        if(!generateAndCompileGrammar()){
            writeToConsole(Colors.RED, "Generating and compiling was not successfull, program will be stopped!");
            return;
        }
        useGrammar();
    }

    private boolean generateAndCompileGrammar(){
        if(!(new GrammarGenerator().useGrammar()) || !(new GrammarCompiler().compileGrammar()))
            return false;
        if(!loadClasses())
            return  false;
        return  true;
    }

    private void useGrammar(){
        XMLFileReader fr = new XMLFileReader();

        if(!fr.startReading())
            return;

        treeToXML.startWriting();

        while(!fr.getEndOfFile()){
            if(fr.readFile()){
                service.execute(new Processor(fr.getCode(), fr.getRowID(), lexerClass, parserClass, treeToXML, grammarApplier));
            }
        }

        service.shutdown();

        try {
            service.awaitTermination(6L, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        treeToXML.stopWriting();
    }

    private boolean loadClasses(){
        try {
            ClassLoader cl = new URLClassLoader(new URL[]{new File(loadProperty("grammar.outputDirectory")).toURI().toURL()});
            String packageName = loadProperty("grammar.package") + ".";
            lexerClass = cl.loadClass(packageName + grammarName + "Lexer");
            parserClass = cl.loadClass(packageName + grammarName + "Parser");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        writeToConsole(Colors.RED, "Cannot load classes, program will be stopped!");
        return false;
    }
}
