package week_02;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HistoryDaoTest {

    @Test
    void insertRejectsNullOperationAsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> HistoryDao.insert("AES", null, null, 0, 0));
    }
}
