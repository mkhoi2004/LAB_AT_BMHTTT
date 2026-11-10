package week_01;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class frm_LichSu extends javax.swing.JFrame {

    private javax.swing.JButton btn_refresh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_status;
    private javax.swing.JTable tbl_history;

    public frm_LichSu() {
        initComponents();
        setLocationRelativeTo(null);
        tbl_history.setModel(HistoryDao.emptyModel());
        loadData();
    }

    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_history = new javax.swing.JTable();
        btn_refresh = new javax.swing.JButton();
        lbl_status = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lịch sử thao tác (SQL Server)");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("LỊCH SỬ THAO TÁC - BẢNG DBO.HISTORY");

        tbl_history.setModel(HistoryDao.emptyModel());
        tbl_history.setRowHeight(24);
        jScrollPane1.setViewportView(tbl_history);

        btn_refresh.setText("Tải lại");
        btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refreshActionPerformed(evt);
            }
        });

        lbl_status.setText("Đang tải...");

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(jLabel1, BorderLayout.NORTH);
        mainPanel.add(jScrollPane1, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 5));
        bottomPanel.add(btn_refresh, BorderLayout.WEST);
        bottomPanel.add(lbl_status, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        pack();
        setSize(800, 420);
    }

    /** Đọc 50 dòng mới nhất từ SQL Server và hiện lên JTable. */
    private void loadData() {
        try {
            tbl_history.setModel(HistoryDao.loadLatest(50));
            lbl_status.setText("Đã tải " + tbl_history.getRowCount() + " dòng mới nhất từ dbo.history.");
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
