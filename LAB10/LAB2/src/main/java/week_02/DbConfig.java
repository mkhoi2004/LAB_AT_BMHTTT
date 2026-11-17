package week_02;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

public final class DbConfig {

    private static String server = "127.0.0.1";
    private static int port = 14333;
    private static String database = "LAB2_MaHoaHienDai";
    private static String user = "";
    private static char[] password = new char[0];
    private static boolean configured;

    private DbConfig() {
    }

    public static synchronized void set(String newServer, int newPort, String newDatabase,
            String newUser, char[] newPassword) {
        String normalizedServer = newServer == null ? "" : newServer.trim();
        String normalizedDatabase = newDatabase == null ? "" : newDatabase.trim();
        String normalizedUser = newUser == null ? "" : newUser.trim();
        if (normalizedServer.isEmpty() || normalizedServer.contains(";") || normalizedServer.contains(",")
                || normalizedDatabase.isEmpty() || normalizedDatabase.contains(";")
                || normalizedUser.isEmpty() || newPassword == null || newPassword.length == 0) {
            throw new IllegalArgumentException("Nhập đầy đủ Server, Database, Username và Password hợp lệ.");
        }
        if (newPort < 1 || newPort > 65535) {
            throw new IllegalArgumentException("Port phải nằm trong khoảng 1 - 65535.");
        }
        clearPassword();
        server = normalizedServer;
        port = newPort;
        database = normalizedDatabase;
        user = normalizedUser;
        password = newPassword.clone();
        configured = true;
    }

    public static synchronized Connection open() throws SQLException {
        if (!configured) {
            throw new SQLException("Chưa cấu hình kết nối SQL Server. Hãy nhập thông tin trên form kết nối.");
        }
        String url = "jdbc:sqlserver://" + server + ":" + port;
        Properties properties = new Properties();
        properties.setProperty("databaseName", database);
        properties.setProperty("encrypt", "true");
        properties.setProperty("trustServerCertificate", "true");
        properties.setProperty("loginTimeout", "5");
        properties.setProperty("user", user);
        char[] passwordCopy = password.clone();
        try {
            properties.setProperty("password", new String(passwordCopy));
            return DriverManager.getConnection(url, properties);
        } finally {
            Arrays.fill(passwordCopy, '\0');
            properties.remove("password");
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
        clearPassword();
        server = "127.0.0.1";
        port = 14333;
        database = "LAB2_MaHoaHienDai";
        user = "";
        configured = false;
    }

    private static void clearPassword() {
        Arrays.fill(password, '\0');
        password = new char[0];
    }
}
