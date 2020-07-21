Nápověda pro nástroj
##############################################################
V souboru file.properties lze nalézt tyto konfigurace:

	grammar.name - nastavení jména gramatiky bez přípony (např. z MySql.g4 bude pouze MySql),
	grammar.rule - nastavení počátečního neterminálu (např. root), nelze volat současně více pravidel,
	grammar.package - nastavení názvu package, který budou mít vygenerované třídy (např. cz.customgrammar),
	grammar.outputDirectory - nastavení absolutní cesty ke složce, do které se vygenerují zdrojové kódy gramatiky,
	grammar.inputGrammar - nastavení absolutní cesty gramatiky. Pokud gramatika obsahuje dva soubory (lexer i parser), je nutné nastavit obě absolutní cesty k těmto souborům oddělené mezerou,
	grammar.tags - nastavení tagů, podle kterých se bude vyhledávat (např. mysql). Pokud se zadává více tagů pro vyhledávání, je nutné je oddělit mezerou,
	xml.input - nastavení absolutní cesty vstupního XML souboru, 
	xml.output - nastavení absolutní cesty výstupního souboru.

##############################################################
Pro správné fungování je nutné všechny uvedené konfigurace nastavit.
Výstupem vznikne XML soubor, ve kterém budou derivační stromy dotazů, na které bylo úspěšně aplikované zvolené pravidlo.


##############################################################
##############################################################
Příklad použití:
	grammar.name=MySql
	grammar.rule=root
	grammar.package=cz.customgrammar
	grammar.outputDirectory=C:/Users/test/Desktop/projekt/nástroj/GrammarTest
	grammar.inputGrammar=C:/Users/test/Desktop/projekt/nástroj/GrammarTest/MySqlLexer.g4 C:/Users/test/Desktop/projekt/nástroj/GrammarTest/MySqlParser.g4
	grammar.tags=mysql
	xml.input=C:/Users/test/Desktop/projekt/nástroj/Posts.xml
	xml.output=C:/Users/test/Desktop/projekt/nástroj/test.xml