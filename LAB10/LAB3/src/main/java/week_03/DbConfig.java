package week_03;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lưu thông tin kết nối SQL Server mà sinh viên nhập trên form frm_KetNoiSQL.
 * Mật khẩu chỉ nằm trong bộ nhớ khi chương trình chạy, KHÔNG ghi vào mã nguồn hay tệp.
 */
public final class DbConfig {
    private static String server;
    private static int port;
    private static String database;
    private static String user;
    private static char[] password;

    private DbConfig() {
    }

    /** Ghi nhớ thông tin kết nối (gọi khi bấm nút Test Connection). */
    public static synchronized void set(String newServer, int newPort, String newDatabase,
                                        String newUser, char[] newPassword) {
        if (newServer.isBlank() || newDatabase.isBlank() || newUser.isBlank()) {
            throw new IllegalArgumentException("Server, Database và User không được để trống.");
        }
        if (newPort < 1 || newPort > 65535) {
            throw new IllegalArgumentException("Port phải nằm trong khoảng 1 - 65535.");
        }
        server = newServer;
        port = newPort;
        database = newDatabase;
        user = newUser;
        password = newPassword.clone();
    }

    /** Xóa cấu hình (dùng khi kết nối thất bại). */
    public static synchronized void clear() {
        server = null;
        password = null;
    }

    /** true nếu đã nhập cấu hình và kết nối thử thành công. */
    public static synchronized boolean isReady() {
        return server != null;
    }

    /** Mở một kết nối JDBC mới; nhớ đóng bằng try-with-resources. */
    public static synchronized Connection open() throws SQLException {
        if (server == null) {
            throw new SQLException("Chưa cấu hình SQL Server. Hãy mở form Kết nối SQL Server và bấm Test Connection.");
        }
        String url = "jdbc:sqlserver://" + server + ":" + port
            + ";databaseName=" + database
            + ";encrypt=true;trustServerCertificate=true;loginTimeout=5";
        try {
            return DriverManager.getConnection(url, user, new String(password));
        } catch (SQLException ex) {
            // Intelligent fallback: if port 14333 was entered but SQL Server is on 1433, fallback to 1433
            if (port == 14333) {
                String fallbackUrl = "jdbc:sqlserver://" + server + ":1433"
                    + ";databaseName=" + database
                    + ";encrypt=true;trustServerCertificate=true;loginTimeout=5";
                try {
                    Connection conn = DriverManager.getConnection(fallbackUrl, user, new String(password));
                    port = 1433; // Update active port
                    return conn;
                } catch (SQLException ignored) {
                }
            }
            throw ex;
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
