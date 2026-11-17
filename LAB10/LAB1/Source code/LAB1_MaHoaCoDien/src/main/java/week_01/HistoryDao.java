package week_01;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

/**
 * Ghi và đọc bảng dbo.history trong database.
 */
public final class HistoryDao {

    private static volatile boolean tableChecked = false;

    private HistoryDao() {
    }

    /** Tự động đảm bảo bảng dbo.history tồn tại trước khi thao tác. */
    private static void ensureTableExists() {
        if (tableChecked) {
            return;
        }
        String sql = "IF OBJECT_ID(N'dbo.history', N'U') IS NULL "
                + "BEGIN "
                + "  CREATE TABLE dbo.history ( "
                + "    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY, "
                + "    created_at DATETIME2(0) NOT NULL DEFAULT SYSDATETIME(), "
                + "    algorithm NVARCHAR(50) NOT NULL, "
                + "    operation VARCHAR(10) NOT NULL, "
                + "    input_text NVARCHAR(MAX) NOT NULL, "
                + "    result_text NVARCHAR(MAX) NOT NULL, "
                + "    CONSTRAINT CK_lab1_operation CHECK (operation IN ('ENCRYPT','DECRYPT')) "
                + "  ); "
                + "END;";
        try (Connection con = DbConfig.open(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
            tableChecked = true;
        } catch (Exception ignored) {
        }
    }

    /** Thêm 1 dòng lịch sử. operation chỉ nhận ENCRYPT hoặc DECRYPT (ràng buộc CHECK trong SQL). */
    public static void insert(String algorithm, String operation, String input, String result) throws SQLException {
        ensureTableExists();
        String sql = "INSERT INTO dbo.history(algorithm, operation, input_text, result_text) VALUES (?, ?, ?, ?)";
        try (Connection con = DbConfig.open(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, algorithm);
            ps.setString(2, operation);
            ps.setString(3, input);
            ps.setString(4, result);
            ps.executeUpdate();
        }
    }

    /** Thử ghi lịch sử, không làm dừng chương trình; trả về câu thông báo để hiện trên form. */
    public static String tryInsert(String algorithm, String operation, String input, String result) {
        try {
            insert(algorithm, operation, input, result);
            return "Đã lưu lịch sử vào SQL Server.";
        } catch (SQLException ex) {
            return "Lỗi lưu lịch sử: " + ex.getMessage();
        }
    }

    /** Bảng rỗng có sẵn tên cột (dùng khi mở form). */
    public static DefaultTableModel emptyModel() {
        return new DefaultTableModel(new Object[]{"ID", "Thời gian", "Thuật toán", "Thao tác", "Đầu vào", "Kết quả"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /** Đọc tối đa 'top' dòng mới nhất, trả về model để gán cho JTable. */
    public static DefaultTableModel loadLatest(int top) throws SQLException {
        ensureTableExists();
        DefaultTableModel model = emptyModel();
        String sql = "SELECT TOP (?) id, created_at, algorithm, operation, input_text, result_text "
                + "FROM dbo.history ORDER BY id DESC";
        try (Connection con = DbConfig.open(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, top);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getLong(1), rs.getTimestamp(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6)});
                }
            }
        }
        return model;
    }
}
