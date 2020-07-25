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

public class GrammarApplier implements  PropertyLoader, ConsolePrinter {

    public String useRule(Class<?> lexerClass, Class<?> parserClass, String query){
        try{
            Lexer lexer = setLexer(query, lexerClass);
            Parser parser = setParser(lexer, parserClass);
            removeListeners(lexer, parser);

            setInterpreters(lexer, parser);


            Method entryPointMethod = parserClass.getMethod(loadProperty("grammar.rule"));
            ParseTree parserTree = (ParseTree) entryPointMethod.invoke(parser);
            String tree = null;

            if(parserTree != null && parser.getNumberOfSyntaxErrors() == 0){
                tree =  parserTree.toStringTree(parser);
            }

            clearDFA(lexer, parser);

            return tree;
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
        lexer.setInterpreter(new LexerATNSimulator(lexer, lexer.getATN(), lexer.getInterpreter().decisionToDFA, new PredictionContextCache()));
        parser.setInterpreter(new ParserATNSimulator(parser, parser.getATN(), parser.getInterpreter().decisionToDFA, new PredictionContextCache()));
    }

    private Lexer setLexer(String query, Class<?> lexerClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        CodePointCharStream chs = fromString(query);
        Constructor lexerCTor = lexerClass.getConstructor(CharStream.class);
        Lexer lexer = (Lexer) lexerCTor.newInstance(chs);
        return lexer;
    }

    private Parser setParser(Lexer lexer, Class<?> parserClass) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Constructor parserCTor = parserClass.getConstructor(TokenStream.class);
        Parser parser = (Parser) parserCTor.newInstance(tokens);
        return parser;
    }
}
