package cz.vsb.semestralni_projekt.grammar_tool;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public interface PropertyLoader {

    default String loadProperty(String name){
        String resourceName = "file.properties";
        String itemResult = "";
        Properties prop = new Properties();

        try(InputStream resourceStream = new FileInputStream(resourceName)) {
            prop.load(resourceStream);
            itemResult = prop.getProperty(name);
        } catch (IOException e) {
            e.printStackTrace(); }

        return itemResult;
    }
}
