package services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.BaseTest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.FruitTransaction;
import org.junit.jupiter.api.Test;
import services.impl.ShopServiceImpl;
import services.interfaces.ShopService;
import strategy.OperationHandler;
import strategy.OperationStrategy;
import strategy.OperationStrategyImpl;
import strategy.impl.BalanceOperationHandler;
import strategy.impl.RemoveOperationHandler;
import strategy.impl.ReturnOperationHandler;
import strategy.impl.SupplyOperationHandler;

public class ShopServiceImplTest extends BaseTest {
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
}
