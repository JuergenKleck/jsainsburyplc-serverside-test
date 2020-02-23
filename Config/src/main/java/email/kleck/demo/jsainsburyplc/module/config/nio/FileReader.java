package email.kleck.demo.jsainsburyplc.module.config.nio;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileReader {

    public boolean openFile(String fileName) throws FileNotFoundException {
        boolean opened = false;
        Path path = Paths.get(fileName);

        if(Files.exists(path)) {

        } else {
            throw new FileNotFoundException("File " + fileName + " does not exist");
        }
        return opened;
    }

}
