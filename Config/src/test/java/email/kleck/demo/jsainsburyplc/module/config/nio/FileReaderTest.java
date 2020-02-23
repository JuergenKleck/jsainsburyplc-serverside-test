package email.kleck.demo.jsainsburyplc.module.config.nio;

import email.kleck.demo.jsainsburyplc.module.config.ConfigConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

class FileReaderTest {

    @Test
    public void givenFile_whenLoading_returnProperties() throws Throwable {
        FileReader reader = new FileReader();
        boolean fileOpened = reader.openFile(ConfigConstants.FILE_NAME);
        Assertions.assertTrue(fileOpened);
    }

    @Test
    public void givenInvalidFile_whenLoading_returnException() {

        FileReader reader = new FileReader();
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            reader.openFile("invalid-file.properties");
        });

    }

}