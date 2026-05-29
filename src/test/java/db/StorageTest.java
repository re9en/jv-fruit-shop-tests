package db;

import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.BaseTest;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class StorageTest extends BaseTest {

    @Test
    void storage_set_throwsException() throws IOException {
        assertThrows(RuntimeException.class,() -> storage.set("banana", -1));
    }

    @Test
    void storage_add_throwsException() throws IOException {
        assertThrows(RuntimeException.class,() -> storage.add("banana", -1));
    }
}
