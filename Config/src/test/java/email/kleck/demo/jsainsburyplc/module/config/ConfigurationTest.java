package email.kleck.demo.jsainsburyplc.module.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {


    @Test
    public void givenConfiguration_whenLoaded_thenReturnProperties() {
        Configuration config = new Configuration();
        Properties properties = config.readConfiguration();

        Assertions.assertNotNull(properties);
        assertNotEquals(0, properties.size());
    }


}