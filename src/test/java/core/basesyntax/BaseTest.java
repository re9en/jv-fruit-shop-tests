package core.basesyntax;

import db.Storage;
import db.StorageImpl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.FruitTransaction;
import org.junit.jupiter.api.BeforeEach;
import services.impl.FileReaderImpl;
import services.interfaces.FileReader;
import strategy.OperationHandler;
import strategy.impl.BalanceOperationHandler;
import strategy.impl.RemoveOperationHandler;
import strategy.impl.ReturnOperationHandler;
import strategy.impl.SupplyOperationHandler;

public class BaseTest {
    protected static final List<String> testFile = List.of("type,fruit,quantity",
            "b,banana,20",
            "b,apple,100",
            "s,banana,100",
            "p,banana,13",
            "r,apple,10",
            "p,apple,20",
            "p,banana,5",
            "s,banana,50");

    protected final Map<String,Integer> inventory = new HashMap<>();
    protected Storage storage = new StorageImpl();
    protected Map<FruitTransaction.Operation, OperationHandler> handlers = new HashMap<>();
    protected FileReader fileReader = new FileReaderImpl();

    @BeforeEach
    void setUp() {
        storage = new StorageImpl();
        handlers = new HashMap<>();

        handlers.put(FruitTransaction.Operation.BALANCE, new BalanceOperationHandler(storage));
        handlers.put(FruitTransaction.Operation.PURCHASE, new RemoveOperationHandler(storage));
        handlers.put(FruitTransaction.Operation.RETURN, new ReturnOperationHandler(storage));
        handlers.put(FruitTransaction.Operation.SUPPLY, new SupplyOperationHandler(storage));
    }
}
