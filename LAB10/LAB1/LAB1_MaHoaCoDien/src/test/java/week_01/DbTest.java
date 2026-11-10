package week_01;

import org.junit.jupiter.api.Test;
import javax.swing.table.DefaultTableModel;
import static org.junit.jupiter.api.Assertions.*;

public class DbTest {

    @Test
    public void testSqlDatabaseOperations() {
        // Setup DbConfig for ATBMHTTT_Lab10 on localhost:1433
        DbConfig.set("localhost", 1433, "ATBMHTTT_Lab10", "lab1_user", "LabUser@2026".toCharArray());

        // Test connection
        try {
            String testResult = DbConfig.test();
            System.out.println("SQL Connection Test: " + testResult);
            assertTrue(testResult.startsWith("Kết nối thành công"));

            // Insert a test record
            HistoryDao.insert("Caesar", "ENCRYPT", "HELLO", "KHOOR");

            // Load records
            DefaultTableModel model = HistoryDao.loadLatest(10);
            assertTrue(model.getRowCount() > 0);
            System.out.println("Loaded rows from dbo.history: " + model.getRowCount());

            // Insert remaining 5 ciphers to populate history table
            HistoryDao.insert("Vigenere", "ENCRYPT", "HELLO", "RIJVS");
            HistoryDao.insert("Rail Fence", "ENCRYPT", "HELLO WORLD", "HOREL OLLWD");
            HistoryDao.insert("Playfair", "ENCRYPT", "HELLOWORLD", "GYIZSCOKCFBU");
            HistoryDao.insert("Transposition", "ENCRYPT", "BAOMAT", "AAOTBM");
            HistoryDao.insert("Autokey", "ENCRYPT", "HELLO", "RIJSS");

            DefaultTableModel updatedModel = HistoryDao.loadLatest(10);
            assertTrue(updatedModel.getRowCount() >= 6);
            System.out.println("Updated rows count: " + updatedModel.getRowCount());

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Database test failed: " + ex.getMessage());
        }
    }
}
