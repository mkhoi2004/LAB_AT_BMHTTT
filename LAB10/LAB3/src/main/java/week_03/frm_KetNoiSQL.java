package week_03;

import java.awt.*;
import javax.swing.*;

public class frm_KetNoiSQL extends javax.swing.JFrame {

    private JTextField txt_server;
    private JTextField txt_port;
    private JTextField txt_database;
    private JTextField txt_user;
    private JPasswordField txt_password;
    private JButton btn_test;
    private JButton btn_history;
    private JLabel lbl_status;

    public frm_KetNoiSQL() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Kết nối Microsoft SQL Server");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTitle = new JLabel("KẾT NỐI MICROSOFT SQL SERVER", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(25, 60, 120));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainPanel.add(lblTitle, gbc);

        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridwidth = 1;

        // Server
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblServer = new JLabel("Server:");
        lblServer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblServer, gbc);

        gbc.gridx = 1;
        txt_server = new JTextField("127.0.0.1", 20);
        txt_server.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(txt_server, gbc);

        // Port
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblPort = new JLabel("Port:");
        lblPort.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblPort, gbc);

        gbc.gridx = 1;
        txt_port = new JTextField("14333", 20);
        txt_port.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(txt_port, gbc);

        // Database
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblDb = new JLabel("Database:");
        lblDb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblDb, gbc);

        gbc.gridx = 1;
        txt_database = new JTextField("LAB3_MaHoaKhoaCongKhai", 20);
        txt_database.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(txt_database, gbc);

        // User
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblUser = new JLabel("User:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblUser, gbc);

        gbc.gridx = 1;
        txt_user = new JTextField("lab3_user", 20);
        txt_user.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(txt_user, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblPass, gbc);

        gbc.gridx = 1;
        txt_password = new JPasswordField(20);
        txt_password.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(txt_password, gbc);

        // Buttons panel
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btn_test = new JButton("Test Connection");
        btn_test.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_test.setPreferredSize(new Dimension(150, 32));

        btn_history = new JButton("Xem lịch sử");
        btn_history.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_history.setPreferredSize(new Dimension(130, 32));

        pnlButtons.add(btn_test);
        pnlButtons.add(btn_history);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 10, 0);
        mainPanel.add(pnlButtons, gbc);

        // Status
        lbl_status = new JLabel("Chưa kết nối.", SwingConstants.CENTER);
        lbl_status.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lbl_status.setPreferredSize(new Dimension(450, 40));
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(lbl_status, gbc);

        add(mainPanel);
        pack();

        btn_test.addActionListener(evt -> btn_testActionPerformed(evt));
        btn_history.addActionListener(evt -> btn_historyActionPerformed(evt));
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
