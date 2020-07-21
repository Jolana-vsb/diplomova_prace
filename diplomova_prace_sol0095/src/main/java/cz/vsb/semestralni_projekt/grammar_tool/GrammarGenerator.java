package cz.vsb.semestralni_projekt.grammar_tool;

import org.antlr.v4.Tool;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GrammarGenerator implements PropertyLoader, ConsolePrinter {

    private String[] arguments;

    private void prepareArguments(){
        writeToConsole(Colors.WHITE, "Preparing arguments for grammar...");
        String packageName = loadProperty("grammar.package");
        String output = loadProperty("grammar.outputDirectory");

        String outputWithPackage = packageName.replace(".", "/");
        Path outputPath = Paths.get(output, outputWithPackage);

        ArrayList<String> args = new ArrayList<>();
        args.add("-o");
        args.add(outputPath.toString());
        args.add("-package");
        args.add(packageName);

        String[] grammars = getGrammars();

        for(String s : grammars)
            args.add(s);

        arguments = args.toArray(new String[args.size()]);
        writeToConsole(Colors.GREEN, "...arguments were prepared.");
    }

    private String[] getGrammars(){
        String input = loadProperty("grammar.inputGrammar");
        return input.split(" ");
    }

    private boolean generateGrammar(){
        writeToConsole(Colors.WHITE, "Generating grammar...");
        if(arguments[4].length() < 1){
            writeToConsole(Colors.RED, "Incorrect arguments!");
            return false;
        }

        Tool tool = new Tool(arguments);
        tool.processGrammarsOnCommandLine();

        if(tool.getNumErrors() > 0){
            writeToConsole(Colors.RED, "Error in grammar generating!");
            return false;
        }
        writeToConsole(Colors.GREEN, "...generating ended.");
        return true;
    }

    public boolean useGrammar(){
        prepareArguments();
        return generateGrammar();
    }
}
