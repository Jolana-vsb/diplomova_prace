package cz.vsb.semestralni_projekt.grammar_tool;

public class ResultPreparator {
    private int space;
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

    private void addTagAndName(String tag, String name, int space){
        if(tag.length() > 0 && !tag.equals(" "))
            addString("<" + tag + ">", space);

        if(name.length() > 0 && !name.equals(" ")){
            name = symbolsToXMLFormat(name);
            addString(name, space + 1);
        }
    }

    private void addString(String str, int space){
        for(int i = 0; i < space; i++){
            this.xmlData.append("  ");
        }
        this.xmlData.append(str + "\n");
    }

    public void prepareData(String select, String selectTree, int rowId){
        this.space = 1;
        setSelectStart(select, rowId);
        getXMLFromTree(selectTree);
        setSelectEnd();
    }

    private void getXMLFromTree(String tree){

        TreeToXMLParser xmlParser = new TreeToXMLParser();
        space++;

        for(int i = 1; i < tree.length()-1; i++){
            xmlParser.checkCharacter(i, tree.charAt(i));

            if(xmlParser.canTakeSubstring()){
                String tag = "", name = "";
                String treeSubstring = xmlParser.getSubstring(tree);
                Boolean writeName = false;
                boolean writeTag = true;
                boolean callRecursive = true;

                for(int k = 1; k < treeSubstring.length(); k++){
                    if(treeSubstring.charAt(k) == ' '){
                        writeTag = false;
                        if(treeSubstring.charAt(k+1) == '\'' || treeSubstring.charAt(k+1) == '\"' || treeSubstring.charAt(k+1) == '`'){
                            name = treeSubstring.substring(k+1, treeSubstring.length()-1);
                            callRecursive = false;
                            break;
                        }
                        else
                            writeName = true;
                    }
                    if(writeTag)
                        tag+=treeSubstring.charAt(k);
                    else if(writeName && treeSubstring.charAt(k) != '(' && treeSubstring.charAt(k) != ')')
                        name+=treeSubstring.charAt(k);
                    else if(treeSubstring.charAt(k) == '(' || treeSubstring.charAt(k) == ')')
                        break;
                }

                if(callRecursive)
                    callRecursion(tag, name, treeSubstring, xmlParser);
                else
                    endFunction(tag, name, xmlParser);
            }
        }
    }

    private void callRecursion(String tag, String name, String treeSubstring, TreeToXMLParser xmlParser){
        addTagAndName(tag, name, space);
        getXMLFromTree(treeSubstring);
        space--;
        addAndRestart(tag, xmlParser);
    }

    private void endFunction(String tag, String name, TreeToXMLParser xmlParser){
        addTagAndName(tag, name, space);
        addAndRestart(tag, xmlParser);
    }

    private void addAndRestart(String tag, TreeToXMLParser xmlParser){
        if(tag.length() > 0 && !tag.equals(" "))
            addString("</" + tag + ">", space);

        xmlParser.restartVariables();
    }

    public String getXmlData(){
        return  this.xmlData.toString();
    }
}
