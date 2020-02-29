package email.kleck.demo.jsainsburyplc;

import email.kleck.demo.jsainsburyplc.module.config.ConfigConstants;
import email.kleck.demo.jsainsburyplc.module.config.Configuration;
import email.kleck.demo.jsainsburyplc.module.parser.WebParser;
import email.kleck.demo.jsainsburyplc.module.parser.api.JsonResponse;
import email.kleck.demo.jsainsburyplc.module.parser.connector.WebConnector;

import java.io.IOException;
import java.util.Properties;

/**
 * The application entry point to execute the app from command line
 */
public class MainApplication {

    public static void main(String[] args) {

        Configuration config = new Configuration();
        Properties properties = config.readConfiguration();

        WebConnector connector = new WebConnector();
        StringBuilder contents = new StringBuilder();
        try {
            contents.append(connector.readWebsite(properties.getProperty(ConfigConstants.PARAM_TARGET_URL)).toString());
            WebParser parser = new WebParser();
            JsonResponse result = parser.extractProducts(contents, properties);
            System.out.println(result.toString());
        } catch (IOException e) {
            System.err.println("Error occurred at: " + e.getLocalizedMessage());
        }

    }

}
