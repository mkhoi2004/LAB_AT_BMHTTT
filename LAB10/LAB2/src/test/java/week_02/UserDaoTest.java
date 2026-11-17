package week_02;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDaoTest {

    @Test
    void registrationRejectsInvalidUsernamesAndShortPasswordsBeforeDatabaseAccess() {
        assertThrows(IllegalArgumentException.class, () -> UserDao.register(" ", "validPass1".toCharArray()));
        assertThrows(IllegalArgumentException.class, () -> UserDao.register("validUser", "short".toCharArray()));
    }

    @Test
    void newTicketIsThirtyTwoUppercaseHexCharacters() {
        String ticket = UserDao.newTicket();

        assertTrue(ticket.matches("[0-9A-F]{32}"));
        assertNotEquals(ticket, UserDao.newTicket());
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "LAB2_SQL_PASSWORD", matches = ".+")
    void sqlRegistrationLoginAndHistoryStoreMetadataOnly() throws SQLException {
        String server = envOrDefault("LAB2_SQL_SERVER", "127.0.0.1");
        int port = Integer.parseInt(envOrDefault("LAB2_SQL_PORT", "14333"));
        String database = envOrDefault("LAB2_SQL_DATABASE", "LAB2_MaHoaHienDai");
        String user = envOrDefault("LAB2_SQL_USER", "lab2_user");
        DbConfig.set(server, port, database, user, System.getenv("LAB2_SQL_PASSWORD").toCharArray());

        String username = "test_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        char[] password = "IntegrationPass@2026".toCharArray();
        UserDao.register(username, password);
        assertTrue(UserDao.login(username, password));
        assertFalse(UserDao.login(username, "WrongPassword@2026".toCharArray()));
        assertThrows(IllegalArgumentException.class, () -> UserDao.register(username, password));
        HistoryDao.insert("AES", "REGISTER", username, 0, 0);
        HistoryDao.insert("AES", "LOGIN", username, 0, 0);
        HistoryDao.insert("AES", "ENCRYPT", username, 12, 64);
        HistoryDao.insert("AES", "DECRYPT", username, 64, 12);

        try (Connection connection = DbConfig.open()) {
            DatabaseMetaData metadata = connection.getMetaData();
            assertFalse(hasColumn(metadata, "crypto_history", "plaintext"));
            assertFalse(hasColumn(metadata, "crypto_history", "ciphertext"));
            assertFalse(hasColumn(metadata, "crypto_history", "key"));
        }
    }

    private static String envOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static boolean hasColumn(DatabaseMetaData metadata, String table, String column) throws SQLException {
        try (ResultSet columns = metadata.getColumns(null, "dbo", table, null)) {
            while (columns.next()) {
                if (column.equals(columns.getString("COLUMN_NAME").toLowerCase(Locale.ROOT))) {
                    return true;
                }
            }
        }
        return false;
    }
}
