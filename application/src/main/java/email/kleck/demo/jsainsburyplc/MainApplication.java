package email.kleck.demo.jsainsburyplc;

import email.kleck.demo.jsainsburyplc.module.config.ConfigConstants;
import email.kleck.demo.jsainsburyplc.module.config.Configuration;
import email.kleck.demo.jsainsburyplc.module.parser.Transformer;
import email.kleck.demo.jsainsburyplc.module.parser.WebParser;
import email.kleck.demo.jsainsburyplc.module.parser.api.JsonResponse;
import email.kleck.demo.jsainsburyplc.module.parser.connector.WebConnector;
import email.kleck.demo.jsainsburyplc.module.parser.internal.Tree;

import java.io.IOException;
import java.util.Properties;

/**
 * The application entry point to execute the app from command line
 */
public class MainApplication {

    public static void main(String[] args) {

        try {
            Configuration config = new Configuration();
            Properties properties = config.readConfiguration();

            WebConnector connector = new WebConnector();
            StringBuilder contents = new StringBuilder();
            WebParser parser = new WebParser();
            Transformer transformer = new Transformer();

            contents.append(connector.readWebsite(properties.getProperty(ConfigConstants.PARAM_TARGET_URL)).toString());
            Tree tree = parser.extractProducts(contents, properties);
            JsonResponse result = transformer.transformTree(tree, properties);
            System.out.println(result.toString());
        } catch (IOException e) {
            System.err.println("Error occurred at: " + e.getLocalizedMessage());
        }

    }

}
