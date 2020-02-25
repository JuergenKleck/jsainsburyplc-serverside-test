package email.kleck.demo.jsainsburyplc.module.config.nio;

import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;

/**
 * File reader for accessing the filesystem
 */
public final class FileReader {

    private Properties properties;

    /**
     * Open a properties file and read the contents into the local variable
     * @param fileName the name of the file to load
     * @throws FileSystemException in case any failure occurred
     */
    public void openFile(String fileName) throws FileSystemException {
        Path path = Paths.get(fileName);

        if (Files.exists(path)) {
            try {
                readFile(path);
            } catch (IOException e) {
                throw new FileSystemException("Error while processing file: " + fileName);
            }
        } else {
            throw new NoSuchFileException("File " + fileName + " does not exist");
        }
    }

    /**
     * Internal method to read the properties
     * @param path
     * @throws IOException
     */
    private void readFile(Path path) throws IOException {
        properties = new Properties();
        properties.load(Files.newInputStream(path));
    }

    /**
     * Getter for the properties
     * @return
     */
    public Properties getProperties() {
        return properties;
    }
}
