package cz.vsb.semestralni_projekt.grammar_tool;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLFileReader implements PropertyLoader, ConsolePrinter {
    private FileInputStream inputStream = null;
    private BufferedReader reader = null;
    private boolean expression = false;
    private String tag, rowID;
    private boolean endOfFile = false;
    private boolean canTakeCodeElements = false;
    private String tagElement = "Tags=\"&lt;";
    private String rowIdElement = "<row Id=\"";
    private String codeElementStart = "&lt;code&gt;", getCodeElementEnd = "&lt;/code&gt;";
    private String parentIDElement = "ParentId=\"";
    private String[] tags;
    private String line = "";
    private HashSet<String> ids = new HashSet<>();
    private ArrayList<String> codeElemets = new ArrayList<>();

    public boolean startReading(){
        tags = loadProperty("grammar.tags").split(" ");
        try {
            writeToConsole(Colors.WHITE, "Starting to read...");
            inputStream = new FileInputStream(loadProperty("xml.input"));
            reader = new BufferedReader(new InputStreamReader(inputStream));
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        writeToConsole(Colors.RED, "Input file was not found, program will be stopped!");
        return false;
    }

    public void stopReading(){
        try {
            if(reader != null)
                reader.close();
            if(inputStream != null)
                inputStream.close();
            writeToConsole(Colors.GREEN, "Reading finished! ...file writing continue...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Boolean searchedTag(String tag){
        for(String t : tags){
            if(tag.contains(t))
                return true;
        }
        return false;
    }

    private void checkForRow(){
        canTakeCodeElements = false;
        String row  = getOccurence(rowIdElement +"(.*?) />", line);

        if(!hasOccurence(row))
            return;

        line = null;
        tag = getOccurence(tagElement +"(.*?)\"", row);

        if(hasOccurence(tag) && searchedTag(tag))
            getRowID(row);
        else if(!hasOccurence(tag)){
            String parentID = getOccurence(parentIDElement +"(.*?)\"", row);
            if(parentID == null)
                return;
            parentID = parentID.replaceAll("\\D+","");

            if(ids.contains(parentID))
                getRowID(row);
            else
                return;
        }

        if(canTakeCodeElements)
            getCodeElements(row);
    }

    private void getRowID(String row){
        rowID = getOccurence(rowIdElement +"(.*?)\"", row).replaceAll("\\D+","");
        ids.add(rowID);
        canTakeCodeElements = true;
    }

    private void getCodeElements(String row){
        getAllCodeElements(codeElementStart + "(.*?)" + getCodeElementEnd, row);

        if(codeElemets.size() > 0)
            expression = true;
    }

    private boolean hasOccurence(String str){
        if(str != null)
            return true;

        return false;
    }

    private String getOccurence(String regex, String str){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        if(matcher.find())
            return matcher.group();

        return null;
    }

    private void getAllCodeElements(String regex, String row){
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(row);

        while (m.find()) {
            String code = m.group();
            codeElemets.add(code.substring(codeElementStart.length(), code.length() - getCodeElementEnd.length()));
        }
    }

    public boolean readFile(){
        if(codeElemets.size() == 0)
            expression = false;

        try{
            while (!expression && (line = reader.readLine()) != null) {
                checkForRow();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(line == null && !expression){
            endOfFile = true;
            stopReading();
            return false;
        }
        return true;
    }

    public boolean getEndOfFile(){
        return endOfFile;
    }

    public String getTag() {
        return tag;
    }

    public String getCode() {
        return codeElemets.remove(0);
    }

    public int getRowID() {
        return Integer.parseInt(rowID.replaceAll("\\D+",""));
    }
}
