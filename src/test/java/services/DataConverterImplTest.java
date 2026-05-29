package services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.BaseTest;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import model.FruitTransaction;
import org.junit.jupiter.api.Test;
import services.impl.DataConverterImpl;
import services.interfaces.DataConverter;

public class DataConverterImplTest extends BaseTest {

    @Test
    void dataConverter_convertData_equals() throws IOException {
        List<String> inputFile = fileReader.readFile(Path.of("src/test/java/resources/data.csv"));
        DataConverter dataConverter = new DataConverterImpl();
        List<FruitTransaction> fruitTransactions =
                dataConverter.convertToFruitTransaction(inputFile);
        List<FruitTransaction> testFruitTransactions =
                dataConverter.convertToFruitTransaction(testFile);
        assertEquals(testFruitTransactions, fruitTransactions);
    }

    @Test
    void dataConverter_convertData_corruptedData() throws IOException {
        List<String> corruptedFile = List.of("type,fruit,quantity",
                "b,banana,twenty",
                "b,apple,100",
                "s,banana,one handred",
                "p,banana,13",
                "r,apple,ten",
                "p,apple,20",
                "p,banana,5",
                "s,banana,50");

        List<String> inputFile = fileReader.readFile(Path.of("src/test/java/resources/data.csv"));
        DataConverter dataConverter = new DataConverterImpl();

        assertThrows(NumberFormatException.class,
                () -> dataConverter.convertToFruitTransaction(corruptedFile));
    }
}
