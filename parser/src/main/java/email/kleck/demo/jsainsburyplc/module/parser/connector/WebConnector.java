package email.kleck.demo.jsainsburyplc.module.parser.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Web connector class to open a website and get the contents
 */
public class WebConnector {

    /**
     * Read the html contents of a website
     *
     * @param url the url to read
     * @return the html contents
     * @throws IOException if the connection failed
     */
    public StringBuilder readWebsite(String url) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }
        }
        return sb;
    }

}
