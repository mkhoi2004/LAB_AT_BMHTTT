package week_01;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class frm_TanCong extends javax.swing.JFrame {

    private javax.swing.JButton btn_bruteforce;
    private javax.swing.JButton btn_frequency;
    private javax.swing.JButton btn_known;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl_status;
    private javax.swing.JTextArea txt_cipher;
    private javax.swing.JTextField txt_known_cipher;
    private javax.swing.JTextField txt_known_plain;
    private javax.swing.JTextArea txt_result;

    public frm_TanCong() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_cipher = new javax.swing.JTextArea();
        btn_bruteforce = new javax.swing.JButton();
        btn_frequency = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txt_known_plain = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txt_known_cipher = new javax.swing.JTextField();
        btn_known = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_result = new javax.swing.JTextArea();
        lbl_status = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Phân tích mật mã cổ điển");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PHÂN TÍCH (TẤN CÔNG) MẬT MÃ CỔ ĐIỂN");

        jLabel2.setText("Ciphertext:");

        txt_cipher.setColumns(20);
        txt_cipher.setLineWrap(true);
        txt_cipher.setRows(2);
        jScrollPane1.setViewportView(txt_cipher);

        btn_bruteforce.setText("Brute Force Caesar");
        btn_bruteforce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_bruteforceActionPerformed(evt);
            }
        });

        btn_frequency.setText("Frequency Analysis");
        btn_frequency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_frequencyActionPerformed(evt);
            }
        });

        jLabel3.setText("Bản rõ đã biết:");
        txt_known_plain.setText("HELLO");
        txt_known_plain.setPreferredSize(new Dimension(100, 26));

        jLabel4.setText("Bản mã:");
        txt_known_cipher.setText("RIJVS");
        txt_known_cipher.setPreferredSize(new Dimension(100, 26));

        btn_known.setText("Known Plaintext (Vigenere)");
        btn_known.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_knownActionPerformed(evt);
            }
        });

        jLabel5.setText("Kết quả:");

        txt_result.setEditable(false);
        txt_result.setColumns(20);
        txt_result.setFont(new java.awt.Font("Monospaced", 0, 13));
        txt_result.setRows(8);
        jScrollPane2.setViewportView(txt_result);

        lbl_status.setText("Sẵn sàng.");

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        mainPanel.add(jLabel1, gbc);

        // Ciphertext label
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        mainPanel.add(jLabel2, gbc);

        // Ciphertext textarea
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        mainPanel.add(jScrollPane1, gbc);

        // Buttons row 1 (Brute force & frequency)
        JPanel btnPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel1.add(btn_bruteforce);
        btnPanel1.add(btn_frequency);
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        mainPanel.add(btnPanel1, gbc);

        // Known Plaintext row
        JPanel knownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        knownPanel.add(jLabel3);
        knownPanel.add(txt_known_plain);
        knownPanel.add(jLabel4);
        knownPanel.add(txt_known_cipher);
        knownPanel.add(btn_known);
        gbc.gridy = 4;
        mainPanel.add(knownPanel, gbc);

        // Result label
        gbc.gridy = 5;
        mainPanel.add(jLabel5, gbc);

        // Result textarea
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.8;
        mainPanel.add(jScrollPane2, gbc);

        // Status
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        mainPanel.add(lbl_status, gbc);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        pack();
        setSize(680, 560);
    }

    private void btn_bruteforceActionPerformed(java.awt.event.ActionEvent evt) {
        String cipher = txt_cipher.getText().trim();
        if (cipher.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hãy nhập Ciphertext.");
            return;
        }
        txt_result.setText(AttackDemos.caesarBruteForce(cipher));
        txt_result.setCaretPosition(0);
        lbl_status.setText("Đã thử đủ 26 khóa Caesar.");
    }

    private void btn_frequencyActionPerformed(java.awt.event.ActionEvent evt) {
        String cipher = txt_cipher.getText().trim();
        if (cipher.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hãy nhập Ciphertext.");
            return;
        }
        txt_result.setText(AttackDemos.frequency(cipher));
        txt_result.setCaretPosition(0);
        lbl_status.setText("Đã thống kê tần suất chữ cái.");
    }

    private void btn_knownActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            String key = AttackDemos.knownPlaintext(txt_known_plain.getText(), txt_known_cipher.getText());
            txt_result.setText("Khóa Vigenère suy ra từng vị trí: " + key
                    + "\nNếu khóa ngắn hơn bản rõ, khóa sẽ lặp lại (KEY KEY ...).");
            lbl_status.setText("Khóa suy ra: " + key);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dữ liệu không hợp lệ", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new frm_TanCong().setVisible(true));
    }
}
