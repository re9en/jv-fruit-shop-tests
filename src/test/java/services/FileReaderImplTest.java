package services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.BaseTest;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

public class FileReaderImplTest extends BaseTest {

    @Test
    void fileReader_readFile_equals() throws IOException {
        List<String> inputFile = fileReader.readFile(Path.of("src/test/java/resources/data.csv"));
        assertEquals(testFile, inputFile);
    }

    @Test
    void fileReader_readFile_throwsException() throws IOException {
        assertThrows(IOException.class,
                () -> fileReader
                        .readFile(Path
                                .of("src/test/java/resources/non_existent_folder/ghost_file.csv")));
    }
}
