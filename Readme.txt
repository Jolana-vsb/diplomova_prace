Nápověda pro nástroj
##############################################################
V souboru file.properties lze nalézt tyto konfigurace:

	grammar.name - nastavení jména gramatiky bez přípony (např. z MySql.g4 bude pouze MySql),
	grammar.rule - nastavení počátečního neterminálu (např. root), nelze volat současně více pravidel,
	grammar.package - nastavení názvu package, který budou mít vygenerované třídy (např. cz.customgrammar),
	grammar.outputDirectory - nastavení absolutní cesty ke složce, do které se vygenerují zdrojové kódy gramatiky,
	grammar.inputDirectory - nastavení absolutní cesty ke složce s gramatikou,
	grammar.inputGrammar - nastavení názvů gramatik s příponou (např. MySqlParser.g4). Pokud se zadává více gramatik, je nutné je oddělit mezerou,
	grammar.tags - nastavení tagů, podle kterých se bude vyhledávat (např. mysql). Pokud se zadává více tagů pro vyhledávání, je nutné je oddělit mezerou,
	xml.input - nastavení absolutní cesty vstupního XML souboru, 
	xml.output - nastavení absolutní cesty výstupního souboru.

##############################################################
Pro správné fungování je nutné všechny uvedené konfigurace nastavit.
Výstupem vznikne XML soubor, ve kterém budou derivační stromy dotazů, na které bylo úspěšně aplikované zvolené pravidlo.
Pozor na lomítka v absolutních cestách. Pokud se zadá absolutní cesta s opačným lomítkem, vyvolá se výjimka.
U některých gramatik je k fungování potřeba více souborů - nestačí pouze lexer a parser. Například u PL/SQL gramatiky jsou navíc 2 java soubory (PlSqlLexerBase.java, PlSqlParserBase.java). Kvůli takové situaci je zavedena konfigurace "grammar.inputDirectory", kde stačí zadat pouze cestu k souborům gramatik. 

##############################################################
##############################################################
Příklad použití:
	grammar.name=MySql
	grammar.rule=root
	grammar.package=cz.customgrammar
	grammar.outputDirectory=C:/Users/test/Desktop/projekt/nástroj/GrammarTest
	grammar.inputDirectory=C:/Users/test/Desktop/projekt/nástroj/GrammarTest
	grammar.inputGrammar=MySqlLexer.g4 MySqlParser.g4 PlSqlLexer.g4 PlSqlParser.g4 SQLite.g4
	grammar.tags=mysql
	xml.input=C:/Users/test/Desktop/projekt/nástroj/Posts.xml
	xml.output=C:/Users/test/Desktop/projekt/nástroj/test.xml