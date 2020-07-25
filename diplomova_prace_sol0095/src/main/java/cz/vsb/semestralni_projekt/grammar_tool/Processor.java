package cz.vsb.semestralni_projekt.grammar_tool;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.tree.ParseTree;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.antlr.v4.runtime.CharStreams.fromString;

public class Processor implements Runnable, PropertyLoader, ConsolePrinter {

    private String query;
    private int rowID;
    private Class<?> lexerClass;
    private Class<?> parserClass;
    private TreeToXML treeToXML;
    private ResultPreparator resultPreparator;
    private DataPreparator dataPreparator;
    private GrammarApplier grammarApplier;

    public Processor(String query, int rowID, Class<?> lexerClass, Class<?> parserClass, TreeToXML treeToXML, GrammarApplier grammarApplier) {
        this.query = query.toLowerCase();
        this.rowID = rowID;
        this.lexerClass = lexerClass;
        this.parserClass = parserClass;
        this.treeToXML = treeToXML;
        this.resultPreparator = new ResultPreparator();
        this.dataPreparator = new DataPreparator();
        this.grammarApplier = grammarApplier;
    }

    @Override
    public void run() {
        if(dataPreparator.containsSelect(query)){
            query = dataPreparator.encodeXML(query);
            String tree;

            synchronized (grammarApplier){
                tree = grammarApplier.useRule(lexerClass, parserClass, query);
            }

            if(tree != null && !tree.equals("(root <EOF>)")){
                resultPreparator.prepareData(query, tree, rowID);
                treeToXML.writeToFile(resultPreparator.getXmlData());
            }
        }
    }
}
