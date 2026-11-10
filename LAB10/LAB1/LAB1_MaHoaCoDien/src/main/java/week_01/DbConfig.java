package week_01;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lưu thông tin kết nối SQL Server mà sinh viên nhập trên form frm_KetNoiSQL.
 * Mặc định tự động cấu hình sẵn theo môi trường Lab: localhost:1433, database ATBMHTTT_Lab10.
 */
public final class DbConfig {

    private static String server = "localhost";
    private static int port = 1433;
    private static String database = "ATBMHTTT_Lab10";
    private static String user = "lab1_user";
    private static char[] password = "LabUser@2026".toCharArray();

    private DbConfig() {
    }

    /** Ghi nhớ thông tin kết nối (gọi khi bấm nút Test Connection). */
    public static synchronized void set(String newServer, int newPort, String newDatabase,
            String newUser, char[] newPassword) {
        if (newServer == null || newServer.isBlank() || newDatabase == null || newDatabase.isBlank()) {
            throw new IllegalArgumentException("Server và Database không được để trống.");
        }
        if (newPort < 1 || newPort > 65535) {
            throw new IllegalArgumentException("Port phải nằm trong khoảng 1 - 65535.");
        }
        server = newServer.trim();
        port = newPort;
        database = newDatabase.trim();
        user = newUser != null ? newUser.trim() : "";
        password = newPassword != null ? newPassword.clone() : new char[0];
    }

    /** Đặt lại cấu hình mặc định của môi trường lab. */
    public static synchronized void initDefaults() {
        server = "localhost";
        port = 1433;
        database = "ATBMHTTT_Lab10";
        user = "lab1_user";
        password = "LabUser@2026".toCharArray();
    }

    /** Xóa cấu hình. */
    public static synchronized void clear() {
        initDefaults();
    }

    /** true nếu đã có cấu hình kết nối. */
    public static synchronized boolean isReady() {
        return server != null && !server.isBlank();
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

    /** Mở một kết nối JDBC mới; tự động thử SQL Auth rồi fallback sang Integrated Security. */
    public static synchronized Connection open() throws SQLException {
        if (server == null || server.isBlank()) {
            initDefaults();
        }
        String baseUrl = "jdbc:sqlserver://" + server + ":" + port
                + ";databaseName=" + database
                + ";encrypt=true;trustServerCertificate=true;loginTimeout=5";

        if (user != null && !user.isBlank()) {
            try {
                return DriverManager.getConnection(baseUrl, user, new String(password != null ? password : new char[0]));
            } catch (SQLException ex) {
                // Thử kết nối bằng Windows Integrated Security nếu tài khoản SQL Auth thất bại
                try {
                    return DriverManager.getConnection(baseUrl + ";integratedSecurity=true");
                } catch (SQLException ex2) {
                    throw ex;
                }
            }
        } else {
            return DriverManager.getConnection(baseUrl + ";integratedSecurity=true");
        }
    }

    /** Kết nối thử; trả về chuỗi mô tả để hiện lên form. */
    public static String test() throws SQLException {
        try (Connection con = open()) {
            DatabaseMetaData md = con.getMetaData();
            return "Kết nối thành công: " + md.getDatabaseProductName() + " "
                    + md.getDatabaseProductVersion() + " - database " + con.getCatalog();
        }
    }
}
