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
     * @throws FileSystemException if the configuration could not be found or read
     */
    public Properties readConfiguration() throws FileSystemException {
        FileReader reader = new FileReader();
        reader.openFile(ConfigConstants.FILE_NAME);
        return reader.getProperties();
    }

}
