package cz.vsb.semestralni_projekt.grammar_tool;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import static org.antlr.v4.runtime.CharStreams.fromString;

public class GrammarApplier implements  PropertyLoader, ConsolePrinter {

    private Class<?> lexerClass;
    private Class<?> parserClass;

    public boolean loadClasses(){
        try {
            ClassLoader cl = new URLClassLoader(new URL[]{new File(loadProperty("grammar.outputDirectory")).toURI().toURL()});
            String packageName = loadProperty("grammar.package") + ".";
            String grammarName = loadProperty("grammar.name");
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

    public String useRule(String query, int rowID){
        try{
            Lexer lexer = setLexer(query);
            Parser parser = setParser(lexer);
            removeListeners(lexer, parser);
            setInterpreters(lexer, parser);

            Method entryPointMethod = parserClass.getMethod(loadProperty("grammar.rule"));
            ParseTree parserTree = (ParseTree) entryPointMethod.invoke(parser);

            clearDFA(lexer, parser);

            if(parserTree != null && parser.getNumberOfSyntaxErrors() == 0){
                ResultPreparator resultPreparator = new ResultPreparator();
                resultPreparator.prepareData(query, parserTree, rowID, parser);
                return resultPreparator.getXmlData();
            }

            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        writeToConsole(Colors.RED, "Incorrect grammar name or rule name or expression is incorrect!");
        return null;
    }

    private void removeListeners(Lexer lexer, Parser parser){
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
    }

    private void clearDFA(Lexer lexer, Parser parser){
        lexer.getInterpreter().clearDFA();
        parser.getInterpreter().clearDFA();
    }

    private void setInterpreters(Lexer lexer, Parser parser){
        lexer.setInterpreter(new LexerATNSimulator(lexer, lexer.getATN(), getDFA(lexer.getATN()), new PredictionContextCache()));
        parser.setInterpreter(new ParserATNSimulator(parser, parser.getATN(), getDFA(parser.getATN()), new PredictionContextCache()));
    }

    private Lexer setLexer(String query) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        CodePointCharStream chs = fromString(query);
        Constructor lexerCTor = lexerClass.getConstructor(CharStream.class);
        Lexer lexer = (Lexer) lexerCTor.newInstance(chs);
        return lexer;
    }

    private Parser setParser(Lexer lexer) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Constructor parserCTor = parserClass.getConstructor(TokenStream.class);
        Parser parser = (Parser) parserCTor.newInstance(tokens);
        return parser;
    }

    private DFA[] getDFA(ATN atn) {
        DFA[] result = new DFA[atn.getNumberOfDecisions()];
        for (int i = 0; i < atn.getNumberOfDecisions(); i++) {
            result[i] = new DFA(atn.getDecisionState(i), i);
        }
        return result;
    }
}
