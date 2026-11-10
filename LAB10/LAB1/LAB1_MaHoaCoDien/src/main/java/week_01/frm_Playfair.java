package week_01;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class frm_Playfair extends javax.swing.JFrame {

    private javax.swing.JButton btn_decrypt;
    private javax.swing.JButton btn_encrypt;
    private javax.swing.JButton btn_matrix;
    private javax.swing.JButton btn_open;
    private javax.swing.JButton btn_save;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lbl_status;
    private javax.swing.JTextArea txt_ciphertext;
    private javax.swing.JTextField txt_key;
    private javax.swing.JTextArea txt_matrix;
    private javax.swing.JTextArea txt_plaintext;

    public frm_Playfair() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_plaintext = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        txt_key = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txt_matrix = new javax.swing.JTextArea();
        btn_matrix = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_ciphertext = new javax.swing.JTextArea();
        btn_encrypt = new javax.swing.JButton();
        btn_decrypt = new javax.swing.JButton();
        btn_open = new javax.swing.JButton();
        btn_save = new javax.swing.JButton();
        lbl_status = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Playfair Cipher");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("MẬT MÃ PLAYFAIR");

        jLabel2.setText("Plaintext:");

        txt_plaintext.setColumns(20);
        txt_plaintext.setLineWrap(true);
        txt_plaintext.setRows(3);
        jScrollPane1.setViewportView(txt_plaintext);

        jLabel3.setText("Key:");

        txt_key.setText("KEYWORD");

        jLabel4.setText("Chỉ gồm chữ A-Z, ví dụ KEYWORD");

        jLabel6.setText("Key Matrix:");

        txt_matrix.setEditable(false);
        txt_matrix.setColumns(9);
        txt_matrix.setFont(new java.awt.Font("Monospaced", 1, 14));
        txt_matrix.setRows(5);
        jScrollPane3.setViewportView(txt_matrix);

        btn_matrix.setText("Generate Key Matrix");
        btn_matrix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_matrixActionPerformed(evt);
            }
        });

        jLabel5.setText("Ciphertext:");

        txt_ciphertext.setColumns(20);
        txt_ciphertext.setLineWrap(true);
        txt_ciphertext.setRows(3);
        jScrollPane2.setViewportView(txt_ciphertext);

        btn_encrypt.setText("Encrypt");
        btn_encrypt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_encryptActionPerformed(evt);
            }
        });

        btn_decrypt.setText("Decrypt");
        btn_decrypt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_decryptActionPerformed(evt);
            }
        });

        btn_open.setText("Open Ciphertext File");
        btn_open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_openActionPerformed(evt);
            }
        });

        btn_save.setText("Save Ciphertext File");
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });

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

        // Plaintext label
        gbc.gridy = 1;
        mainPanel.add(jLabel2, gbc);

        // Plaintext text area
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        mainPanel.add(jScrollPane1, gbc);

        // Key row
        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        keyPanel.add(jLabel3);
        txt_key.setPreferredSize(new Dimension(120, 28));
        keyPanel.add(txt_key);
        keyPanel.add(jLabel4);
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        mainPanel.add(keyPanel, gbc);

        // Matrix row
        JPanel matrixPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        matrixPanel.add(jLabel6);
        matrixPanel.add(jScrollPane3);
        matrixPanel.add(btn_matrix);
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(matrixPanel, gbc);

        // Ciphertext label
        gbc.gridy = 5;
        mainPanel.add(jLabel5, gbc);

        // Ciphertext text area
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        mainPanel.add(jScrollPane2, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.add(btn_encrypt);
        btnPanel.add(btn_decrypt);
        btnPanel.add(btn_open);
        btnPanel.add(btn_save);
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        mainPanel.add(btnPanel, gbc);

        // Status
        gbc.gridy = 8;
        mainPanel.add(lbl_status, gbc);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        pack();
        setSize(620, 520);
    }

    private void btn_encryptActionPerformed(java.awt.event.ActionEvent evt) {
        String plaintext = txt_plaintext.getText();
        if (plaintext.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hãy nhập Plaintext trước khi mã hóa.");
            return;
        }
        try {
            String key = txt_key.getText().trim();
            txt_ciphertext.setText(PlayfairCipher.encrypt(plaintext, key));
            lbl_status.setText("Đã mã hóa Playfair (bỏ dấu cách, J -> I, chèn X).");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dữ liệu không hợp lệ", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btn_decryptActionPerformed(java.awt.event.ActionEvent evt) {
        String ciphertext = txt_ciphertext.getText();
        if (ciphertext.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hãy nhập Ciphertext trước khi giải mã.");
            return;
        }
        try {
            String key = txt_key.getText().trim();
            txt_plaintext.setText(PlayfairCipher.decrypt(ciphertext, key));
            lbl_status.setText("Đã giải mã Playfair (còn ký tự đệm X).");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dữ liệu không hợp lệ", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btn_openActionPerformed(java.awt.event.ActionEvent evt) {
        String path = FileTextIO.openText(this, txt_ciphertext);
        if (path != null) {
            lbl_status.setText("Đã mở tệp: " + path);
        }
    }

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {
        String path = FileTextIO.saveText(this, txt_ciphertext, "playfair_ciphertext.txt");
        if (path != null) {
            lbl_status.setText("Đã lưu tệp: " + path);
        }
    }

    private void btn_matrixActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            txt_matrix.setText(PlayfairCipher.getKeyMatrix(txt_key.getText()));
            txt_matrix.setCaretPosition(0);
            lbl_status.setText("Đã tạo ma trận khóa 5x5 (I và J dùng chung ô).");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Khóa không hợp lệ", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new frm_Playfair().setVisible(true));
    }
}
