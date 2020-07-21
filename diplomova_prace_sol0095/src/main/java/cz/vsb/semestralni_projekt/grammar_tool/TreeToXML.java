package cz.vsb.semestralni_projekt.grammar_tool;

import java.io.*;

public class TreeToXML implements PropertyLoader, ConsolePrinter {

    private BufferedWriter bufferedWriter;

    private synchronized void printString(String str, int space){
        try {
            for(int i = 0; i < space; i++){
                bufferedWriter.write("  ");
            }
            bufferedWriter.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String data){
        printString(data, 0);
    }

    public void startWriting(){
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(loadProperty("xml.output"), true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeToConsole(Colors.WHITE, "Starting write to XML...");
        printString("<sqlSelects>\n", 0);
    }

    public void stopWriting(){
        printString("</sqlSelects>", 0);
        writeToConsole(Colors.GREEN, "...writing to XML was successfull.");
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
