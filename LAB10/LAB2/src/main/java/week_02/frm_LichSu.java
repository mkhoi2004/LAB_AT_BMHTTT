package week_02;

import java.awt.BorderLayout;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class frm_LichSu extends JFrame {

    private final JTable tbl_history = new JTable(HistoryDao.emptyModel());
    private final JLabel lbl_status = new JLabel(" ", SwingConstants.LEFT);
    private final JButton btn_refresh = new JButton("Refresh");

    public frm_LichSu() {
        super("Operation History");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        tbl_history.setAutoCreateRowSorter(true);
        add(new JScrollPane(tbl_history), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(btn_refresh, BorderLayout.EAST);
        bottom.add(lbl_status, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        btn_refresh.addActionListener(event -> refresh());
        setSize(900, 420);
        setLocationRelativeTo(null);
        refresh();
    }

    private void refresh() {
        try {
            DefaultTableModel model = HistoryDao.loadLatest(200);
            tbl_history.setModel(model);
            tbl_history.setAutoCreateRowSorter(true);
            lbl_status.setText("Đã tải " + model.getRowCount() + " bản ghi.");
        } catch (IllegalArgumentException | SQLException ex) {
            lbl_status.setText("Không tải được lịch sử: " + ex.getMessage());
        }
    }
}
