package email.kleck.demo.jsainsburyplc.module.config;

import email.kleck.demo.jsainsburyplc.module.config.nio.FileReader;

import java.nio.file.FileSystemException;
import java.util.Properties;

/**
 * Configuration main entry class
 */
public class Configuration {

    /**
     * Read the configuration from the filesystem parallel on this application
     *
     * @return properties if the configuration file has been loaded, null if something went wrong
     */
    public Properties readConfiguration() {
        Properties properties = null;

        FileReader reader = new FileReader();
        try {
            reader.openFile(ConfigConstants.FILE_NAME);
            properties = reader.getProperties();
        } catch (FileSystemException e) {
            // discard the result
        }

        return properties;
    }

}
