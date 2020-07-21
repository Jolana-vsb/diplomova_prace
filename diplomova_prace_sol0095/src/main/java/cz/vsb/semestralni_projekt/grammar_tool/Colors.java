package cz.vsb.semestralni_projekt.grammar_tool;

public enum Colors {
    RED("\u001B[31m"),  GREEN("\u001B[32m"), WHITE("\u001B[0m"), YELLOW("\u001B[33m");

    private final String color;

    public String getColor()
    {
        return this.color;
    }

    private Colors(String color)
    {
        this.color = color;
    }
}
