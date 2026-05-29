package services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import services.impl.FileWriterImpl;
import services.interfaces.FileWriter;

public class FileWriterImplTest {

    @Test
    void fileWriter_write_isWorkable() throws IOException {
        String path = "test_report.csv";
        String expectedData = "fruit,quantity"
                + System.lineSeparator()
                + "banana,10";
        FileWriter fileWriter = new FileWriterImpl();
        fileWriter.write(path,expectedData);
        Path filePath = Path.of(path);

        assertTrue(Files.exists(filePath),
                "File should be created");

        String actualData = Files.readString(filePath);
        assertEquals(expectedData, actualData,
                "File content should match expected data");

        Files.deleteIfExists(filePath);
    }

    @Test
    void fileWriter_write_throws_RuntimeException() {
        String path = "";
        String expectedData = "fruit,quantity" + System.lineSeparator() + "banana" + 10;
        FileWriter fileWriter = new FileWriterImpl();
        assertThrows(RuntimeException.class,() -> fileWriter.write(path, expectedData),
                "Check your data and path");
    }
}
