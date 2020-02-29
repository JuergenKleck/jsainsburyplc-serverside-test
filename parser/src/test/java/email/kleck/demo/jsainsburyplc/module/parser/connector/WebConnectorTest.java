package email.kleck.demo.jsainsburyplc.module.parser.connector;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class WebConnectorTest {

    @Test
    public void givenConfig_whenConnecting_returnHtml() {
        final String url = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
        WebConnector connector = new WebConnector();
        StringBuilder contents = new StringBuilder();
        Assertions.assertDoesNotThrow(() -> contents.append(connector.readWebsite(url).toString()));
        Assertions.assertNotNull(contents.toString());
        Assertions.assertTrue(contents.toString().contains("<html"));
    }

    @Test
    public void givenConfig_whenConnecting_returnException() {
        WebConnector connector = new WebConnector();
        Assertions.assertThrows(IOException.class, () -> connector.readWebsite("null"));
    }


}