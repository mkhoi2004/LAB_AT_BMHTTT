package week_02;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public final class UserDao {

    private static final int ITERATIONS = 120_000;

    private UserDao() {
    }

    public static void register(String username, char[] password) throws SQLException {
        String normalizedUsername = validateUsername(username);
        validatePassword(password);
        byte[] salt = CipherUtil.randomBytes(16);
        byte[] passwordHash = CipherUtil.deriveKey(password, salt, ITERATIONS, 32);
        String sql = "INSERT INTO dbo.app_users(username, salt, password_hash) VALUES (?, ?, ?)";
        try (Connection connection = DbConfig.open();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, normalizedUsername);
            statement.setBytes(2, salt);
            statement.setBytes(3, passwordHash);
            statement.executeUpdate();
        } catch (SQLException ex) {
            if (isDuplicateUsername(ex)) {
                throw new IllegalArgumentException("Username đã tồn tại.", ex);
            }
            throw ex;
        } finally {
            Arrays.fill(passwordHash, (byte) 0);
        }
    }

    public static boolean login(String username, char[] password) throws SQLException {
        if (username == null || username.isBlank() || password == null || password.length == 0) {
            return false;
        }
        String sql = "SELECT salt, password_hash FROM dbo.app_users WHERE username = ?";
        try (Connection connection = DbConfig.open();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.trim());
            try (ResultSet results = statement.executeQuery()) {
                if (!results.next()) {
                    return false;
                }
                byte[] salt = results.getBytes("salt");
                byte[] storedHash = results.getBytes("password_hash");
                byte[] actualHash = CipherUtil.deriveKey(password, salt, ITERATIONS, 32);
                try {
                    return MessageDigest.isEqual(storedHash, actualHash);
                } finally {
                    Arrays.fill(storedHash, (byte) 0);
                    Arrays.fill(actualHash, (byte) 0);
                    Arrays.fill(salt, (byte) 0);
                }
            }
        }
    }

    public static String newTicket() {
        return CipherUtil.hex(CipherUtil.randomBytes(16));
    }

    private static String validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username không được để trống.");
        }
        String normalized = username.trim();
        if (normalized.length() > 40) {
            throw new IllegalArgumentException("Username không được dài quá 40 ký tự.");
        }
        return normalized;
    }

    private static void validatePassword(char[] password) {
        if (password == null || password.length < 8) {
            throw new IllegalArgumentException("Password phải có ít nhất 8 ký tự.");
        }
    }

    private static boolean isDuplicateUsername(SQLException exception) {
        for (SQLException current = exception; current != null; current = current.getNextException()) {
            if (current.getErrorCode() == 2601 || current.getErrorCode() == 2627) {
                return true;
            }
        }
        return false;
    }
}
