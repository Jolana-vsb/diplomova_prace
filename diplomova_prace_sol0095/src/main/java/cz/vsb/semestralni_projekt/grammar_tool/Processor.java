package cz.vsb.semestralni_projekt.grammar_tool;

public class Processor implements Runnable, PropertyLoader, ConsolePrinter {

    private String query;
    private int rowID;
    private TreeToXML treeToXML;
    private DataPreparator dataPreparator;
    private GrammarApplier grammarApplier;

    public Processor(String query, int rowID, TreeToXML treeToXML, GrammarApplier grammarApplier) {
        this.query = query.toLowerCase();
        this.rowID = rowID;
        this.treeToXML = treeToXML;
        this.dataPreparator = new DataPreparator();
        this.grammarApplier = grammarApplier;
    }

    @Override
    public void run() {
        if(dataPreparator.containsSelect(query)){
            query = dataPreparator.encodeXML(query);
            String tree = grammarApplier.useRule(query, rowID);

            if(tree != null)
                treeToXML.writeToFile(tree);
        }
    }
}
