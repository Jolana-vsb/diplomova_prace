package cz.vsb.semestralni_projekt.grammar_tool;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;

import java.util.Arrays;
import java.util.List;

public class ResultPreparator {
    private StringBuilder xmlData = new StringBuilder();
    private StringBuilder specialWord = new StringBuilder();

    private String symbolsToXMLFormat(String str){
        String replaced = str.replaceAll("&", "&amp;")
                .replaceAll("\"","&quot;")
                .replaceAll(">","&gt;")
                .replaceAll("<","&lt;")
                .replaceAll("'","&apos;");

        return replaced;
    }

    private void setSelectStart(String select, int rowId){
        this.xmlData.append("<sqlSelect><rowId>" + rowId + "</rowId><selectCode>" + symbolsToXMLFormat(select) + "</selectCode>");
    }

    private void setSelectEnd(){
        this.xmlData.append("</sqlSelect>\n");
    }

    public void prepareData(String select, ParseTree tree, int rowId, Parser parser){
        setSelectStart(select, rowId);
        getXMLTree(parser, tree, select);
        setSelectEnd();
    }

    private void getXMLTree(Parser parser, ParseTree tree, String query) {
        recursive(tree, Arrays.asList(parser.getRuleNames()),query);
    }

    private void recursive(ParseTree tree, List<String> ruleNames, String query) {
        String element = Trees.getNodeText(tree, ruleNames);

        if(element.equals("<EOF>"))
            element = element.replaceAll("<", "").replaceAll(">", "");

        boolean occurrence = !query.contains(element);
        if(occurrence) {
            if (specialWord.length() > 0) {
                specialWord.append("</specialWord>");
                xmlData.append(specialWord);
                specialWord.setLength(0);
            }
            xmlData.append("<" + element + ">");
        }
        else {
            if (specialWord.length() > 0)
                specialWord.append(" " + symbolsToXMLFormat(element));
            else
                specialWord.append("<specialWord>" + symbolsToXMLFormat(element));
        }

        if (tree instanceof ParserRuleContext) {
            ParserRuleContext prc = (ParserRuleContext) tree;
            if (prc.children != null) {
                for (ParseTree child : prc.children)
                    recursive(child, ruleNames,query);
            }
        }
        if(occurrence){
            if (specialWord.length() > 0) {
                specialWord.append("</specialWord>");
                xmlData.append(specialWord);
                specialWord.setLength(0);
            }
            xmlData.append("</" + element + ">");
        }
    }

    public String getXmlData(){
        return  this.xmlData.toString();
    }
}
