package db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class StorageTest {

    protected Storage storage = new StorageImpl();

    @Test
    void storage_set_returnRightAmount() {
        final Map<String,Integer> inventoryTest = new HashMap<>();
        inventoryTest.put("banana", 10);
        storage.set("banana", 10);
        assertEquals(storage.getAllData(), inventoryTest);
    }

    @Test
    void storage_set_negativeAmount_throwsException() {
        assertThrows(RuntimeException.class,() -> storage.set("banana", -1));
    }

    @Test
    void storage_add_returnRightAmount() {
        final Map<String,Integer> inventoryTest = new HashMap<>();
        inventoryTest.put("banana", 11);
        storage.add("banana", 11);
        assertEquals(storage.getAllData(), inventoryTest);
    }

    @Test
    void storage_add_negativeAmount_throwsException() {
        assertThrows(RuntimeException.class,() -> storage.add("banana", -1));
    }

    @Test
    void storage_remove_returnRightAmount() {

        final Map<String,Integer> inventoryTest = new HashMap<>();
        inventoryTest.put("banana", 11);

        storage.set("banana", 20);
        storage.remove("banana", 9);

        assertEquals(storage.getAllData(), inventoryTest);
    }

    @Test
    void storage_remove_negativeAmount_throwsException() {
        storage.set("banana", 20);
        assertThrows(RuntimeException.class,() ->
                storage.remove("banana", 21));
    }
}
