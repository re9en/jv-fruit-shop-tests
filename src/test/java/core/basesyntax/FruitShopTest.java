package core.basesyntax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import db.Storage;
import db.StorageImpl;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.FruitTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.impl.DataConverterImpl;
import services.impl.FileReaderImpl;
import services.impl.FileWriterImpl;
import services.impl.ReportGeneratorImpl;
import services.impl.ShopServiceImpl;
import services.interfaces.DataConverter;
import services.interfaces.FileReader;
import services.interfaces.FileWriter;
import services.interfaces.ReportGenerator;
import services.interfaces.ShopService;
import strategy.OperationHandler;
import strategy.OperationStrategy;
import strategy.OperationStrategyImpl;
import strategy.impl.BalanceOperationHandler;
import strategy.impl.RemoveOperationHandler;
import strategy.impl.ReturnOperationHandler;
import strategy.impl.SupplyOperationHandler;

public class FruitShopTest {
    private final Map<String,Integer> inventory = new HashMap<>();
    private Storage storage = new StorageImpl();
    private Map<FruitTransaction.Operation, OperationHandler> handlers = new HashMap<>();
    private FileReader fileReader = new FileReaderImpl();
    private List<String> testFile = List.of("type,fruit,quantity",
            "b,banana,20",
            "b,apple,100",
            "s,banana,100",
            "p,banana,13",
            "r,apple,10",
            "p,apple,20",
            "p,banana,5",
            "s,banana,50");

    @BeforeEach
    void setUp() {
        storage = new StorageImpl();
        handlers = new HashMap<>();

        handlers.put(FruitTransaction.Operation.BALANCE, new BalanceOperationHandler(storage));
        handlers.put(FruitTransaction.Operation.PURCHASE, new RemoveOperationHandler(storage));
        handlers.put(FruitTransaction.Operation.RETURN, new ReturnOperationHandler(storage));
        handlers.put(FruitTransaction.Operation.SUPPLY, new SupplyOperationHandler(storage));
    }

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

    @Test
    void balanceOperationHandler_handle_isWorkable() throws IOException {
        BalanceOperationHandler handler = new BalanceOperationHandler(storage);

        FruitTransaction fruitTransaction = new FruitTransaction(
                FruitTransaction.Operation.BALANCE,
                "Banana",
                50);

        handler.handle(fruitTransaction);

        Integer actual = storage.getAllData().get("Banana");

        assertEquals(50, actual, "Storage should contain 50 bananas after balance operation");
    }

    @Test
    void removeOperationHandler_handle_isWorkable() throws IOException {
        storage.add("Banana", 60);
        RemoveOperationHandler handler = new RemoveOperationHandler(storage);

        FruitTransaction fruitTransaction = new FruitTransaction(
                FruitTransaction.Operation.PURCHASE,
                "Banana",
                50);

        handler.handle(fruitTransaction);

        Integer actual = storage.getAllData().get("Banana");

        assertEquals(10, actual, "Storage should contain 10 bananas after remove operation");
    }

    @Test
    void removeOperationHandler_handle_throwsException() throws IOException {
        storage.add("Banana", 60);
        RemoveOperationHandler handler = new RemoveOperationHandler(storage);

        FruitTransaction fruitTransaction = new FruitTransaction(
                FruitTransaction.Operation.PURCHASE,
                "Banana",
                70);
        assertThrows(RuntimeException.class, () -> handler.handle(fruitTransaction));
    }

    @Test
    void returnOperationHandler_handle_isWorkable() throws IOException {
        storage.add("Banana", 10);
        ReturnOperationHandler handler = new ReturnOperationHandler(storage);

        FruitTransaction fruitTransaction = new FruitTransaction(
                FruitTransaction.Operation.RETURN,
                "Banana",
                50);

        handler.handle(fruitTransaction);

        Integer actual = storage.getAllData().get("Banana");

        assertEquals(60, actual, "Storage should contain 60 bananas after return operation");
    }

    @Test
    void storage_set_throwsException() throws IOException {
        assertThrows(RuntimeException.class,() -> storage.set("banana", -1));
    }

    @Test
    void storage_add_throwsException() throws IOException {
        assertThrows(RuntimeException.class,() -> storage.add("banana", -1));
    }

    @Test
    void supplyOperationHandler_handle_isWorkable() throws IOException {
        storage.add("Banana", 2);
        SupplyOperationHandler handler = new SupplyOperationHandler(storage);

        FruitTransaction fruitTransaction = new FruitTransaction(
                FruitTransaction.Operation.SUPPLY,
                "Banana",
                50);

        handler.handle(fruitTransaction);

        Integer actual = storage.getAllData().get("Banana");

        assertEquals(52, actual, "Storage should contain 52 bananas after supply operation");
    }

    @Test
    void removeOperationHandler_handle_amountLessThenZero_ThrowException() throws IOException {
        storage.add("Banana", 10);
        RemoveOperationHandler handler = new RemoveOperationHandler(storage);

        FruitTransaction fruitTransaction = new FruitTransaction(
                FruitTransaction.Operation.PURCHASE,
                "Banana",
                50);

        assertThrows(RuntimeException.class, () -> {
            handler.handle(fruitTransaction);
        });
    }

    @Test
    void operationStrategyImpl_getHandler_CanReturnOperation() {

        OperationStrategy operationStrategy = new OperationStrategyImpl(handlers);

        OperationHandler actualHandler =
                operationStrategy.getHandler(FruitTransaction.Operation.BALANCE);

        assertNotNull(actualHandler);
        assertInstanceOf(BalanceOperationHandler.class, actualHandler);
    }

    @Test
    void shopService_process_isWorking() throws IOException {
        Map<FruitTransaction.Operation, OperationHandler> handlers = new HashMap<>();

        handlers.put(FruitTransaction.Operation.BALANCE, new BalanceOperationHandler(storage));
        handlers.put(FruitTransaction.Operation.PURCHASE, new RemoveOperationHandler(storage));
        handlers.put(FruitTransaction.Operation.RETURN, new ReturnOperationHandler(storage));
        handlers.put(FruitTransaction.Operation.SUPPLY, new SupplyOperationHandler(storage));

        List<FruitTransaction> fruitTransaction = new ArrayList<>();

        fruitTransaction.add(new FruitTransaction(
                FruitTransaction.Operation.SUPPLY,
                "Banana",
                50));
        fruitTransaction.add(new FruitTransaction(
                FruitTransaction.Operation.RETURN,
                "Banana",
                20));
        fruitTransaction.add(new FruitTransaction(
                FruitTransaction.Operation.SUPPLY,
                "Apple",
                50));

        OperationStrategy operationStrategy = new OperationStrategyImpl(handlers);
        ShopService shopService = new ShopServiceImpl(operationStrategy);

        shopService.process(fruitTransaction);

        Integer actual = storage.getAllData().get("Banana");
        Integer actual2 = storage.getAllData().get("Apple");
        assertEquals(70, actual);
        assertEquals(50, actual2);
    }

    @Test
    void shopService_process_nullTransactions_throwsException() {
        OperationStrategy operationStrategy = new OperationStrategyImpl(handlers);
        ShopService shopService = new ShopServiceImpl(operationStrategy);

        assertThrows(NullPointerException.class, () -> {
            shopService.process(null);
        });
    }

    @Test
    void shopService_process_unsupportedOperation_throwsException() {
        OperationStrategy emptyStrategy = new OperationStrategyImpl(new HashMap<>());
        ShopService shopService = new ShopServiceImpl(emptyStrategy);

        List<FruitTransaction> transactions = List.of(
                new FruitTransaction(FruitTransaction.Operation.SUPPLY, "banana", 10)
        );

        assertThrows(NullPointerException.class, () -> {
            shopService.process(transactions);
        });
    }

    @Test
    void reportGenerator_returnCorrectReport() {
        storage.add("Banana", 2);
        storage.add("Pineapple", 1);
        Map<String, Integer> data = storage.getAllData();
        ReportGenerator reportGenerator = new ReportGeneratorImpl();
        String actual = reportGenerator.getReport(data);

        assertTrue(actual.contains("fruit,quantity"),
                "Report should contain header");
        assertTrue(actual.contains("Banana,2"),
                "Report should contain banana data");
        assertTrue(actual.contains("Pineapple,1"),
                "Report should contain pineapple data");
    }

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

    @Test
    void fruitTransaction_equalsAndHashCode_workCorrectly() {
        FruitTransaction fruitTransactionExpected = new FruitTransaction(
                FruitTransaction.Operation.BALANCE,
                "banana",
                52);
        FruitTransaction fruitTransactionActual = new FruitTransaction(
                FruitTransaction.Operation.BALANCE,
                "banana",
                52);
        assertEquals(fruitTransactionExpected,fruitTransactionActual);

        int hashExpected = fruitTransactionExpected.hashCode();
        int hashActual = fruitTransactionActual.hashCode();

        assertEquals(hashExpected, hashActual);
    }

    @Test
    void fruitTransaction_equalsAndHashCode_notEquals() {
        FruitTransaction base = new FruitTransaction(
                FruitTransaction.Operation.BALANCE,
                "banana",
                52);

        FruitTransaction diffOperation = new FruitTransaction(FruitTransaction.Operation.RETURN,
                "banana",
                52);
        FruitTransaction diffFruit = new FruitTransaction(FruitTransaction.Operation.BALANCE,
                "apples",
                52);
        FruitTransaction diffQuantity = new FruitTransaction(FruitTransaction.Operation.BALANCE,
                "banana",
                10);

        assertNotEquals(base, diffOperation);
        assertNotEquals(base, diffFruit);
        assertNotEquals(base, diffQuantity);
    }

    @Test
    void fruitTransaction_equalsAndHashCode_specialCases() {
        FruitTransaction fruitTransaction = new FruitTransaction(
                FruitTransaction.Operation.BALANCE, "banana", 52);

        assertFalse(fruitTransaction.equals(null));
        assertFalse(fruitTransaction.equals("string"));
    }

    @Test
    void fruitTransaction_equals_sameObject_returnsTrue() {
        FruitTransaction transaction = new FruitTransaction(FruitTransaction.Operation.BALANCE,
                "banana",
                52);

        assertTrue(transaction.equals(transaction));
    }

    @Test
    void fruitTransaction_Operation_from_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> FruitTransaction.Operation.from("f"));
    }
}
