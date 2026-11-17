package week_02;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import javax.swing.table.DefaultTableModel;

public final class HistoryDao {

    private static final Set<String> OPERATIONS = Set.of("REGISTER", "LOGIN", "ENCRYPT", "DECRYPT");
    private static final String[] COLUMNS = {
        "ID", "Thời gian (UTC)", "Thuật toán", "Thao tác", "Username", "Độ dài đầu vào", "Độ dài đầu ra"
    };

    private HistoryDao() {
    }

    public static void insert(String algorithm, String operation, String username,
            int inputLength, int outputLength) throws SQLException {
        if (algorithm == null || algorithm.isBlank() || operation == null || !OPERATIONS.contains(operation)
                || inputLength < 0 || outputLength < 0) {
            throw new IllegalArgumentException("Thông tin lịch sử không hợp lệ.");
        }
        String sql = "INSERT INTO dbo.crypto_history"
                + "(algorithm, operation, app_username, input_length, output_length) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DbConfig.open();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, algorithm);
            statement.setString(2, operation);
            statement.setString(3, username);
            statement.setInt(4, inputLength);
            statement.setInt(5, outputLength);
            statement.executeUpdate();
        }
    }

    public static String tryInsert(String algorithm, String operation, String username,
            int inputLength, int outputLength) {
        try {
            insert(algorithm, operation, username, inputLength, outputLength);
            return "Đã lưu lịch sử thao tác vào SQL Server.";
        } catch (SQLException ex) {
            return "Không lưu được lịch sử SQL: " + ex.getMessage();
        }
    }

    public static DefaultTableModel emptyModel() {
        return new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static DefaultTableModel loadLatest(int top) throws SQLException {
        if (top < 1) {
            throw new IllegalArgumentException("Số dòng lịch sử phải lớn hơn 0.");
        }
        DefaultTableModel model = emptyModel();
        String sql = "SELECT TOP (?) history_id, created_at, algorithm, operation, app_username, "
                + "input_length, output_length FROM dbo.crypto_history ORDER BY history_id DESC";
        try (Connection connection = DbConfig.open();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, top);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    model.addRow(new Object[]{
                        results.getLong("history_id"),
                        results.getTimestamp("created_at"),
                        results.getString("algorithm"),
                        results.getString("operation"),
                        results.getString("app_username"),
                        results.getInt("input_length"),
                        results.getInt("output_length")
                    });
                }
            }
        }
        return model;
    }
}
