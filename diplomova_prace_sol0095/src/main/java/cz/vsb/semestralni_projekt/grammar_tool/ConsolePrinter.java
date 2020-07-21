package cz.vsb.semestralni_projekt.grammar_tool;

public interface ConsolePrinter {

    String OS = System.getProperty("os.name").toLowerCase();

    default void writeToConsole(Colors color, String text){
        if(OS.indexOf("win") >= 0)
            System.out.println(text);
        else
            System.out.println(color.getColor() + text + Colors.WHITE.getColor());
    }
}
