package email.kleck.demo.jsainsburyplc.module.config.nio;

import email.kleck.demo.jsainsburyplc.module.config.ConfigConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.FileSystemException;

class FileReaderTest {

    @Test
    public void givenFile_whenLoading_returnProperties() {
        FileReader reader = new FileReader();
        Assertions.assertDoesNotThrow(() -> reader.openFile("empty.properties"));
    }

    @Test
    public void givenFile_whenLoadingEmptyFile_returnNoProperties() {
        FileReader reader = new FileReader();
        Assertions.assertDoesNotThrow(() -> reader.openFile("empty.properties"));
        Assertions.assertNotNull(reader.getProperties());
        Assertions.assertSame(0, reader.getProperties().size());
    }

    @Test
    public void givenFile_whenLoading_returnNonEmptyProperties() {
        FileReader reader = new FileReader();
        Assertions.assertDoesNotThrow(() -> reader.openFile(ConfigConstants.FILE_NAME));
        Assertions.assertNotNull(reader.getProperties());
        Assertions.assertFalse(reader.getProperties().isEmpty());
    }

    @Test
    public void givenInvalidFile_whenLoading_returnException() {
        FileReader reader = new FileReader();
        Assertions.assertThrows(FileSystemException.class, () -> reader.openFile("invalid-file.properties"));
    }

    @Test
    public void givenNoFile_whenGettingProperties_returnNull() {
        FileReader reader = new FileReader();
        Assertions.assertNull(reader.getProperties());
    }

}