package week_03;

import java.awt.*;
import javax.swing.*;

public class frm_Menu extends javax.swing.JFrame {

    private JButton btn_rsa;
    private JButton btn_ecc;
    private JButton btn_rsaaes;
    private JButton btn_signature;
    private JButton btn_sql;
    private JButton btn_history;

    public frm_Menu() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("LAB 3 - Mã hóa khóa công khai");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        // Title
        JLabel lblTitle = new JLabel("LAB 3 - MÃ HÓA KHÓA CÔNG KHAI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(25, 60, 120));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitle);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Subtitle
        JLabel lblSub = new JLabel("Chọn một bài để mở form tương ứng (bài 3.1 chạy RSADemo.java bằng Run File):", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(70, 70, 70));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblSub);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Buttons Grid
        JPanel gridPanel = new JPanel(new GridLayout(3, 2, 20, 15));
        gridPanel.setMaximumSize(new Dimension(500, 150));

        btn_rsa = new JButton("3.2 RSA (Swing)");
        btn_rsa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_rsa.setPreferredSize(new Dimension(220, 38));

        btn_ecc = new JButton("3.3 ECC");
        btn_ecc.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_ecc.setPreferredSize(new Dimension(220, 38));

        btn_rsaaes = new JButton("3.4 RSA + AES");
        btn_rsaaes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_rsaaes.setPreferredSize(new Dimension(220, 38));

        btn_signature = new JButton("3.5 Chữ ký ECDSA");
        btn_signature.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_signature.setPreferredSize(new Dimension(220, 38));

        btn_sql = new JButton("Kết nối SQL Server");
        btn_sql.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_sql.setPreferredSize(new Dimension(220, 38));

        btn_history = new JButton("Xem lịch sử");
        btn_history.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_history.setPreferredSize(new Dimension(220, 38));

        gridPanel.add(btn_rsa);
        gridPanel.add(btn_ecc);
        gridPanel.add(btn_rsaaes);
        gridPanel.add(btn_signature);
        gridPanel.add(btn_sql);
        gridPanel.add(btn_history);

        gridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(gridPanel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Footer info
        JLabel lblFooter = new JLabel("Bảo mật hệ thống thông tin - Khoa CNTT", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblFooter.setForeground(Color.GRAY);
        lblFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblFooter);

        add(mainPanel);
        pack();

        btn_rsa.addActionListener(evt -> new frm_RSA().setVisible(true));
        btn_ecc.addActionListener(evt -> new frm_ECC().setVisible(true));
        btn_rsaaes.addActionListener(evt -> new frm_RSA_AES().setVisible(true));
        btn_signature.addActionListener(evt -> new frm_ECC_Signature().setVisible(true));
        btn_sql.addActionListener(evt -> new frm_KetNoiSQL().setVisible(true));
        btn_history.addActionListener(evt -> new frm_LichSu().setVisible(true));
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        java.awt.EventQueue.invokeLater(() -> new frm_Menu().setVisible(true));
    }
}
