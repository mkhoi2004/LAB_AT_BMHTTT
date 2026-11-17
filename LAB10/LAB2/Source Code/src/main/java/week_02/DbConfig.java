package week_02;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

/**
 * Quản lý kết nối SQL Server cho Lab 2.
 * Tự động cấu hình sẵn theo môi trường Lab: localhost:1433, database LAB2_MaHoaHienDai / ATBMHTTT_Lab10, user lab2_user / LabUser@2026.
 */
public final class DbConfig {

    private static String server = "127.0.0.1";
    private static int port = 1433;
    private static String database = "LAB2_MaHoaHienDai";
    private static String user = "lab2_user";
    private static char[] password = "LabUser@2026".toCharArray();
    private static boolean configured = true;

    private DbConfig() {
    }

    public static synchronized void set(String newServer, int newPort, String newDatabase,
            String newUser, char[] newPassword) {
        String normalizedServer = newServer == null ? "" : newServer.trim();
        String normalizedDatabase = newDatabase == null ? "" : newDatabase.trim();
        String normalizedUser = newUser == null ? "" : newUser.trim();
        if (normalizedServer.isEmpty() || normalizedServer.contains(";") || normalizedServer.contains(",")
                || normalizedDatabase.isEmpty() || normalizedDatabase.contains(";")) {
            throw new IllegalArgumentException("Nhập đầy đủ Server và Database hợp lệ.");
        }
        if (newPort < 1 || newPort > 65535) {
            throw new IllegalArgumentException("Port phải nằm trong khoảng 1 - 65535.");
        }
        clearPassword();
        server = normalizedServer;
        port = newPort;
        database = normalizedDatabase;
        user = normalizedUser;
        password = newPassword != null ? newPassword.clone() : new char[0];
        configured = true;
    }

    public static synchronized void initDefaults() {
        clearPassword();
        server = "127.0.0.1";
        port = 1433;
        database = "LAB2_MaHoaHienDai";
        user = "lab2_user";
        password = "LabUser@2026".toCharArray();
        configured = true;
    }

    public static synchronized boolean isConfigured() {
        return configured;
    }

    public static synchronized String getServer() {
        return server;
    }

    public static synchronized int getPort() {
        return port;
    }

    public static synchronized String getDatabase() {
        return database;
    }

    public static synchronized String getUser() {
        return user;
    }

    public static synchronized Connection open() throws SQLException {
        if (!configured) {
            initDefaults();
        }
        return tryConnect(server, port, database, user, password);
    }

    private static Connection tryConnect(String srv, int prt, String db, String usr, char[] pwd) throws SQLException {
        String url = "jdbc:sqlserver://" + srv + ":" + prt;
        Properties properties = new Properties();
        properties.setProperty("databaseName", db);
        properties.setProperty("encrypt", "true");
        properties.setProperty("trustServerCertificate", "true");
        properties.setProperty("loginTimeout", "5");

        if (usr != null && !usr.isBlank()) {
            properties.setProperty("user", usr);
            char[] passwordCopy = pwd != null ? pwd.clone() : new char[0];
            try {
                properties.setProperty("password", new String(passwordCopy));
                return DriverManager.getConnection(url, properties);
            } catch (SQLException ex) {
                // Thử cổng 1433 nếu cổng 14333 thất bại
                if (prt == 14333) {
                    try {
                        String retryUrl = "jdbc:sqlserver://" + srv + ":1433";
                        Connection conn = DriverManager.getConnection(retryUrl, properties);
                        port = 1433;
                        return conn;
                    } catch (SQLException ignored) {
                    }
                }
                // Thử Windows Integrated Security nếu SQL Auth thất bại
                try {
                    Properties winProps = new Properties();
                    winProps.setProperty("databaseName", db);
                    winProps.setProperty("encrypt", "true");
                    winProps.setProperty("trustServerCertificate", "true");
                    winProps.setProperty("loginTimeout", "5");
                    winProps.setProperty("integratedSecurity", "true");
                    return DriverManager.getConnection(url, winProps);
                } catch (SQLException ignored) {
                }
                // Thử database ATBMHTTT_Lab10 nếu database LAB2_MaHoaHienDai thất bại
                if ("LAB2_MaHoaHienDai".equalsIgnoreCase(db)) {
                    try {
                        properties.setProperty("databaseName", "ATBMHTTT_Lab10");
                        Connection conn = DriverManager.getConnection(url, properties);
                        database = "ATBMHTTT_Lab10";
                        return conn;
                    } catch (SQLException ignored) {
                    }
                }
                throw ex;
            } finally {
                Arrays.fill(passwordCopy, '\0');
                properties.remove("password");
            }
        } else {
            properties.setProperty("integratedSecurity", "true");
            return DriverManager.getConnection(url, properties);
        }
    }

    public static String test() throws SQLException {
        try (Connection connection = open()) {
            DatabaseMetaData metadata = connection.getMetaData();
            return "Kết nối thành công: " + metadata.getDatabaseProductName() + " "
                    + metadata.getDatabaseProductVersion() + " - database " + connection.getCatalog();
        }
    }

    public static synchronized void clear() {
        initDefaults();
    }

    private static void clearPassword() {
        if (password != null) {
            Arrays.fill(password, '\0');
            password = new char[0];
        }
    }
}

