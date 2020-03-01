package email.kleck.demo.jsainsburyplc.module.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Executable;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ConfigurationTest {

    @Test
    public void givenConfiguration_whenLoaded_thenReturnProperties() {
        Configuration config = new Configuration();
        AtomicReference<Properties> properties = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> properties.set(config.readConfiguration()));

        Assertions.assertNotNull(properties.get());
        assertNotEquals(0, properties.get().size());
    }


}