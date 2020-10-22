package cz.vsb.semestralni_projekt.grammar_tool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GrammarTool implements PropertyLoader, ConsolePrinter {
    private TreeToXML treeToXML;
    private ExecutorService service;
    private GrammarApplier grammarApplier;

    public GrammarTool(){
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
        if(!grammarApplier.loadClasses())
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
                service.execute(new Processor(fr.getCode(), fr.getRowID(), treeToXML, grammarApplier));
            }
        }

        service.shutdown();

        try {
            service.awaitTermination(3L, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        treeToXML.stopWriting();
    }
}
