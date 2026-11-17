package week_01;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class frm_KetNoiSQL extends javax.swing.JFrame {

    private javax.swing.JButton btn_history;
    private javax.swing.JButton btn_test;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel lbl_status;
    private javax.swing.JTextField txt_database;
    private javax.swing.JPasswordField txt_password;
    private javax.swing.JTextField txt_port;
    private javax.swing.JTextField txt_server;
    private javax.swing.JTextField txt_user;

    public frm_KetNoiSQL() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt_server = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txt_port = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_database = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txt_user = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txt_password = new javax.swing.JPasswordField();
        btn_test = new javax.swing.JButton();
        btn_history = new javax.swing.JButton();
        lbl_status = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Kết nối Microsoft SQL Server");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("KẾT NỐI MICROSOFT SQL SERVER");

        jLabel2.setText("Server:");
        txt_server.setText(DbConfig.getServer() != null ? DbConfig.getServer() : "localhost");

        jLabel3.setText("Port:");
        txt_port.setText(DbConfig.getPort() > 0 ? String.valueOf(DbConfig.getPort()) : "1433");

        jLabel4.setText("Database:");
        txt_database.setText(DbConfig.getDatabase() != null ? DbConfig.getDatabase() : "ATBMHTTT_Lab10");

        jLabel5.setText("User:");
        txt_user.setText(DbConfig.getUser() != null ? DbConfig.getUser() : "lab1_user");

        jLabel6.setText("Password:");
        txt_password.setText("LabUser@2026");

        btn_test.setText("Test Connection");
        btn_test.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_testActionPerformed(evt);
            }
        });

        btn_history.setText("Xem lịch sử");
        btn_history.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_historyActionPerformed(evt);
            }
        });

        lbl_status.setText(DbConfig.isReady() ? "<html>Đã cấu hình SQL Server.</html>" : "Chưa kết nối.");

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(jLabel1, gbc);

        // Server
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formPanel.add(jLabel2, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(txt_server, gbc);

        // Port
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formPanel.add(jLabel3, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(txt_port, gbc);

        // Database
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formPanel.add(jLabel4, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(txt_database, gbc);

        // User
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formPanel.add(jLabel5, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(txt_user, gbc);

        // Password
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formPanel.add(jLabel6, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(txt_password, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.add(btn_test);
        btnPanel.add(btn_history);
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        formPanel.add(btnPanel, gbc);

        // Status
        gbc.gridy = 7;
        formPanel.add(lbl_status, gbc);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(formPanel, BorderLayout.CENTER);
        pack();
        setSize(500, 380);
    }

    private void btn_testActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int port = Integer.parseInt(txt_port.getText().trim());
            DbConfig.set(txt_server.getText().trim(), port, txt_database.getText().trim(),
                    txt_user.getText().trim(), txt_password.getPassword());
            lbl_status.setText("<html>" + DbConfig.test() + "</html>");
        } catch (Exception ex) {
            DbConfig.clear();
            lbl_status.setText("<html>Kết nối thất bại: " + ex.getMessage() + "</html>");
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Không kết nối được SQL Server", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btn_historyActionPerformed(java.awt.event.ActionEvent evt) {
        new frm_LichSu().setVisible(true);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new frm_KetNoiSQL().setVisible(true));
    }
}
