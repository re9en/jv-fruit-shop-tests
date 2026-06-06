package services;

import static org.junit.jupiter.api.Assertions.assertTrue;

import db.Storage;
import db.StorageImpl;
import java.util.Map;
import org.junit.jupiter.api.Test;
import services.impl.ReportGeneratorImpl;
import services.interfaces.ReportGenerator;

public class ReportGeneratorImplTest {

    protected Storage storage = new StorageImpl();

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
}
