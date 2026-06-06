package services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import services.impl.FileReaderImpl;
import services.interfaces.FileReader;

public class FileReaderImplTest {

    protected static final List<String> testFile = List.of("type,fruit,quantity",
            "b,banana,20",
            "b,apple,100",
            "s,banana,100",
            "p,banana,13",
            "r,apple,10",
            "p,apple,20",
            "p,banana,5",
            "s,banana,50");

    protected FileReader fileReader = new FileReaderImpl();

    @Test
    void fileReader_readFile_validCsvFile_returnsCorrectLines() throws IOException {
        List<String> inputFile = fileReader.readFile(Path.of("src/test/java/resources/data.csv"));
        assertEquals(testFile, inputFile);
    }

    @Test
    void fileReader_readFile_nonExistentPath_throwsRuntimeException() {
        assertThrows(IOException.class,
                () -> fileReader
                        .readFile(Path
                                .of("src/test/java/resources/non_existent_folder/ghost_file.csv")));
    }
}
