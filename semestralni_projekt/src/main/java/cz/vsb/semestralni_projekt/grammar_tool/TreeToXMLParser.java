package cz.vsb.semestralni_projekt.grammar_tool;

public class TreeToXMLParser {

    private  int rBrIndex = -1, lBrIndex = -1, rBrCount = 0, lBrCount = 0, aposCount = 0, quoteCount = 0, backAposCount = 0, parity = 2;
    private boolean firstLBr = true;
    private boolean backSlash = false;

    TreeToXMLParser(){

    }

    public void checkCharacter(int i, char ch){
        if(ch == '(' && checkParity(aposCount) && checkParity(quoteCount) && checkParity(backAposCount)){
            if(firstLBr){
                lBrIndex = i;
                firstLBr = false;
            }
            lBrCount++;
        }
        else if(ch == ')' && checkParity(aposCount) && checkParity(quoteCount) && checkParity(backAposCount)){
            rBrIndex = i;
            rBrCount++;
        }
        else if(!backSlash && ch == '\'' && checkParity(quoteCount) && checkParity(backAposCount)){
            aposCount++;
        }
        else if(!backSlash && ch == '\"' && checkParity(aposCount) && checkParity(backAposCount)){
            quoteCount++;
        }
        else if(!backSlash && ch == '`' && checkParity(quoteCount) && checkParity(aposCount)){
            backAposCount++;
        }
        else if(!backSlash && ch == '\\'){
            backSlash = true;
        }
        else{
            backSlash = false;
        }
    }

    private boolean checkParity(int val){
        if(val % parity == 0)
            return  true;
        return  false;
    }

    public boolean canTakeSubstring(){
        if(lBrCount > 0 && lBrCount == rBrCount)
            if(checkParity(aposCount) && checkParity(quoteCount) && checkParity(backAposCount))
                return  true;
        return false;
    }

    public String getSubstring(String tree){
        return tree.substring(lBrIndex, rBrIndex+1);
    }

    public void restartVariables(){
        firstLBr = true;
        lBrCount = 0;
        rBrCount = 0;
        aposCount = 0;
        quoteCount = 0;
        backAposCount = 0;
    }

}

