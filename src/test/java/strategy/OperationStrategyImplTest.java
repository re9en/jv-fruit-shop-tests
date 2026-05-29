package strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.BaseTest;
import java.io.IOException;
import model.FruitTransaction;
import org.junit.jupiter.api.Test;
import strategy.impl.BalanceOperationHandler;
import strategy.impl.RemoveOperationHandler;
import strategy.impl.ReturnOperationHandler;
import strategy.impl.SupplyOperationHandler;

public class OperationStrategyImplTest extends BaseTest {

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
}
