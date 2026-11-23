package week_03;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class frm_LichSu extends javax.swing.JFrame {

    private JTable tbl_history;
    private JButton btn_refresh;
    private JLabel lbl_status;

    public frm_LichSu() {
        initComponents();
        setLocationRelativeTo(null);
        tbl_history.setModel(HistoryDao.emptyModel());
        loadData();
    }

    private void initComponents() {
        setTitle("Lịch sử thao tác (SQL Server)");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(800, 450));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header Title
        JLabel lblTitle = new JLabel("LỊCH SỬ THAO TÁC - BẢNG DBO.HISTORY", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(25, 60, 120));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Center Table
        tbl_history = new JTable();
        tbl_history.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl_history.setRowHeight(24);
        tbl_history.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(tbl_history);
        scrollPane.setPreferredSize(new Dimension(760, 280));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        lbl_status = new JLabel("Đang tải...");
        lbl_status.setFont(new Font("Segoe UI", Font.ITALIC, 13));

        btn_refresh = new JButton("Tải lại");
        btn_refresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_refresh.setPreferredSize(new Dimension(110, 32));

        bottomPanel.add(lbl_status, BorderLayout.CENTER);
        bottomPanel.add(btn_refresh, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);
        pack();

        btn_refresh.addActionListener(evt -> btn_refreshActionPerformed(evt));
    }

    private void loadData() {
        try {
            tbl_history.setModel(HistoryDao.loadLatest(50));
            lbl_status.setText("Đã tải " + tbl_history.getRowCount() + " dòng mới nhất từ dbo.History.");
        } catch (java.sql.SQLException ex) {
            lbl_status.setText("Không đọc được lịch sử: " + ex.getMessage());
        }
    }

    private void btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {
        loadData();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new frm_LichSu().setVisible(true));
    }
}
