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

    private Lexer lexer;
    private Parser parser;
    private String query;
    private int rowID;
    private Class<?> lexerClass;
    private Class<?> parserClass;
    private TreeToXML treeToXML;
    private ResultPreparator resultPreparator;
    private DataPreparator dataPreparator;


    public Processor(String query, int rowID, Class<?> lexerClass, Class<?> parserClass, TreeToXML treeToXML) {
        this.query = query.toLowerCase();
        this.rowID = rowID;
        this.lexerClass = lexerClass;
        this.parserClass = parserClass;
        this.treeToXML = treeToXML;
        this.resultPreparator = new ResultPreparator();
        this.dataPreparator = new DataPreparator();
    }

    @Override
    public void run() {
        if(dataPreparator.containsSelect(query)){
            query = dataPreparator.encodeXML(query);
            useRule();
        }
    }

    private boolean useRule(){
        try{
            setLexerAndParser();
            removeListeners();
            setInterpreters();

            Method entryPointMethod = parserClass.getMethod(loadProperty("grammar.rule"));
            ParseTree parserTree = (ParseTree) entryPointMethod.invoke(parser);

            if(parserTree != null && parser.getNumberOfSyntaxErrors() == 0){
                String tree =  parserTree.toStringTree(parser);
                if(!tree.equals("(root <EOF>)")){
                    resultPreparator.prepareData(query, tree, rowID);
                    treeToXML.writeToFile(resultPreparator.getXmlData());
                }
            }

            clearDFA();
            return true;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        writeToConsole(Colors.RED, "Incorrect grammar name or rule name, program will be stopped!");
        return false;
    }

    private void removeListeners(){
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
    }

    private void clearDFA(){
        lexer.getInterpreter().clearDFA();
        parser.getInterpreter().clearDFA();
    }

    private void setInterpreters(){
        lexer.setInterpreter(new LexerATNSimulator(lexer, lexer.getATN(), lexer.getInterpreter().decisionToDFA, new PredictionContextCache()));
        parser.setInterpreter(new ParserATNSimulator(parser, parser.getATN(), parser.getInterpreter().decisionToDFA, new PredictionContextCache()));
    }

    private void setLexerAndParser() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        CodePointCharStream chs = fromString(query);
        Constructor lexerCTor = lexerClass.getConstructor(CharStream.class);
        lexer = (Lexer) lexerCTor.newInstance(chs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        Constructor parserCTor = parserClass.getConstructor(TokenStream.class);
        parser = (Parser) parserCTor.newInstance(tokens);
    }
}
