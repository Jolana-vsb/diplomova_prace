package cz.vsb.semestralni_projekt.grammar_tool;

import org.antlr.v4.Tool;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrammarGenerator implements PropertyLoader, ConsolePrinter {

    private String[] arguments;
    private String packageName;
    private String outputDirectory;
    private String packagePath;
    private String newClassPackage;

    public GrammarGenerator(){
        packageName = loadProperty("grammar.package");
        outputDirectory = loadProperty("grammar.outputDirectory");
        packagePath = packageName.replace(".", "/");
        newClassPackage = "package " + packageName + ";";
    }

    private void prepareArguments(){
        writeToConsole(Colors.WHITE, "Preparing arguments for grammar...");

        Path outputPath = Paths.get(outputDirectory, packagePath);

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
        String inputGrammar = loadProperty("grammar.inputGrammar");
        String inputDirectory = loadProperty("grammar.inputDirectory");
        String[] grammars = inputGrammar.split(" ");

        for(int i = 0; i < grammars.length; i++){
            grammars[i] = inputDirectory + "/" + grammars[i];
        }
        return grammars;
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

        prepareAdditionalFiles();
        writeToConsole(Colors.GREEN, "...generating ended.");
        return true;
    }

    public boolean useGrammar(){
        prepareArguments();
        return generateGrammar();
    }

    private ArrayList<String> getFiles(){
        File[] files = loadFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        for(File f : files)
            fileNames.add(f.getAbsolutePath());

        return fileNames;
    }

    private File[] loadFiles(){
        File dir = new File(loadProperty("grammar.inputDirectory"));
        return dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(".java"));
    }

    private void prepareAdditionalFiles(){
        ArrayList<String> additionalFiles = getFiles();

        for(String fileName : additionalFiles)
            prepareAdditionalFile(fileName);
    }

    private void prepareAdditionalFile(String fileName){
        try {
            File file = new File(fileName);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputDirectory + "/" + packagePath +"/" + file.getName(), false));
            String line;
            StringBuilder fullClass = new StringBuilder();
            boolean hasPackage = false;

            while((line = bufferedReader.readLine()) != null){
                String classPackage = getOccurence("package" + "(.*?)" + ";", line);
                if(classPackage != null){
                    hasPackage = true;
                    line = line.replaceAll("package(.*?);", newClassPackage);
                }
                fullClass.append(line + "\n");
            }

            if(!hasPackage)
                fullClass.insert(0, newClassPackage);

            bufferedWriter.write(fullClass.toString());
            bufferedReader.close();
            bufferedWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getOccurence(String regex, String str){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if(matcher.find())
            return matcher.group();

        return null;
    }
}
