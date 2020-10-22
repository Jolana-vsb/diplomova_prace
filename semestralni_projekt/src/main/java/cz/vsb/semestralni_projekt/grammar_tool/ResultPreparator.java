package cz.vsb.semestralni_projekt.grammar_tool;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;

import java.util.Arrays;
import java.util.List;

public class ResultPreparator {
    private static int space = 2;
    private StringBuilder xmlData = new StringBuilder();

    private String symbolsToXMLFormat(String str){
        String replaced = str.replaceAll("&", "&amp;")
                .replaceAll("\"","&quot;")
                .replaceAll(">","&gt;")
                .replaceAll("<","&lt;")
                .replaceAll("'","&apos;");

        return replaced;
    }

    private void setSelectStart(String select, int rowId){
        this.xmlData.append("  <sqlSelect>\n    <rowId>\n      " + rowId + "\n    </rowId>\n    <selectCode>\n      " + symbolsToXMLFormat(select) + "\n    </selectCode>\n");
    }

    private void setSelectEnd(){
        this.xmlData.append("  </sqlSelect>\n");
    }

    public void prepareData(String select, ParseTree tree, int rowId, Parser parser){
        setSelectStart(select, rowId);
        getXMLTree(parser, tree, select);
        setSelectEnd();
    }

    private void getXMLTree(Parser parser, ParseTree tree, String query) {
        recursive(tree, space, Arrays.asList(parser.getRuleNames()),query);
    }

    private void recursive(ParseTree tree, int offset, List<String> ruleNames, String query) {
        for (int i = 0; i < offset; i++) {
            xmlData.append("  ");
        }
        String element = Trees.getNodeText(tree, ruleNames);

        if(element.equals("<EOF>"))
            element = element.replaceAll("<", "").replaceAll(">", "");

        boolean occurrence = !query.contains(element);
        if(occurrence)
            xmlData.append("<" + element + ">").append("\n");
        else
            xmlData.append(symbolsToXMLFormat(element)).append("\n");

        if (tree instanceof ParserRuleContext) {
            ParserRuleContext prc = (ParserRuleContext) tree;
            if (prc.children != null) {
                for (ParseTree child : prc.children)
                    recursive(child, offset + 1, ruleNames,query);
            }
        }
        if(occurrence){
            for (int i = 0; i < offset; i++)
                xmlData.append("  ");
            xmlData.append("</" + element + ">").append("\n");
        }
    }

    public String getXmlData(){
        return  this.xmlData.toString();
    }
}
