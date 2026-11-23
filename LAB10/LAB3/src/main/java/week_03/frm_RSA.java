package week_03;

import java.awt.*;
import javax.swing.*;

public class frm_RSA extends javax.swing.JFrame {

    private final RSACipher rsa = new RSACipher();

    private JComboBox<String> cbo_bits;
    private JButton btn_generate;
    private JLabel lbl_keyinfo;
    private JTextArea txt_plaintext;
    private JTextArea txt_ciphertext;
    private JButton btn_encrypt;
    private JButton btn_decrypt;
    private JLabel lbl_status;

    public frm_RSA() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("RSA Cipher");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Title
        JLabel lblTitle = new JLabel("MẬT MÃ RSA (OAEP - SHA-256)", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(25, 60, 120));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 12, 0);
        mainPanel.add(lblTitle, gbc);

        // Row 1: Key Size & Generate Keys
        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblKeySize = new JLabel("Key size:");
        lblKeySize.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblKeySize, gbc);

        JPanel pnlKeyGen = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        cbo_bits = new JComboBox<>(new String[] {"2048", "3072"});
        cbo_bits.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbo_bits.setPreferredSize(new Dimension(100, 28));

        btn_generate = new JButton("Generate Keys");
        btn_generate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_generate.setPreferredSize(new Dimension(140, 30));

        lbl_keyinfo = new JLabel("Chưa có khóa.");
        lbl_keyinfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        pnlKeyGen.add(cbo_bits);
        pnlKeyGen.add(btn_generate);
        pnlKeyGen.add(lbl_keyinfo);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(pnlKeyGen, gbc);

        // Row 2: Plaintext
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblPlain = new JLabel("Plaintext:");
        lblPlain.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblPlain, gbc);

        txt_plaintext = new JTextArea(3, 45);
        txt_plaintext.setLineWrap(true);
        txt_plaintext.setWrapStyleWord(true);
        txt_plaintext.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane spPlain = new JScrollPane(txt_plaintext);
        spPlain.setPreferredSize(new Dimension(600, 65));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(spPlain, gbc);

        // Row 3: Ciphertext
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblCipher = new JLabel("Ciphertext:");
        lblCipher.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblCipher, gbc);

        txt_ciphertext = new JTextArea(5, 45);
        txt_ciphertext.setLineWrap(true);
        txt_ciphertext.setWrapStyleWord(true);
        txt_ciphertext.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane spCipher = new JScrollPane(txt_ciphertext);
        spCipher.setPreferredSize(new Dimension(600, 110));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        mainPanel.add(spCipher, gbc);

        // Row 4: Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btn_encrypt = new JButton("Encrypt");
        btn_encrypt.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_encrypt.setPreferredSize(new Dimension(110, 32));

        btn_decrypt = new JButton("Decrypt");
        btn_decrypt.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_decrypt.setPreferredSize(new Dimension(110, 32));

        pnlButtons.add(btn_encrypt);
        pnlButtons.add(btn_decrypt);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(12, 0, 8, 0);
        mainPanel.add(pnlButtons, gbc);

        // Row 5: Status
        lbl_status = new JLabel("Bấm Generate Keys để tạo cặp khóa.", SwingConstants.CENTER);
        lbl_status.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lbl_status.setPreferredSize(new Dimension(600, 36));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(lbl_status, gbc);

        add(mainPanel);
        pack();

        btn_generate.addActionListener(evt -> btn_generateActionPerformed(evt));
        btn_encrypt.addActionListener(evt -> btn_encryptActionPerformed(evt));
        btn_decrypt.addActionListener(evt -> btn_decryptActionPerformed(evt));
    }

    private void btn_generateActionPerformed(java.awt.event.ActionEvent evt) {
        int bits = Integer.parseInt((String) cbo_bits.getSelectedItem());
        try {
            long start = System.currentTimeMillis();
            rsa.generateKeys(bits);
            long ms = System.currentTimeMillis() - start;
            lbl_keyinfo.setText("RSA-" + bits + ": tạo khóa " + ms + " ms, tối đa " + rsa.maxPlaintextBytes() + " byte");
            lbl_status.setText("Đã tạo cặp khóa. Khóa công khai dùng để mã hóa, khóa riêng để giải mã.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tạo được khóa: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btn_encryptActionPerformed(java.awt.event.ActionEvent evt) {
        String plaintext = txt_plaintext.getText();
        if (plaintext.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hãy nhập Plaintext trước khi mã hóa.");
            return;
        }
        try {
            String ciphertext = rsa.encrypt(plaintext);
            txt_ciphertext.setText(ciphertext);
            txt_ciphertext.setCaretPosition(0);
            lbl_status.setText("Đã mã hóa RSA (" + ciphertext.length() + " ký tự Base64). "
                + HistoryDao.tryInsert("RSA", "ENCRYPT", plaintext.length(), true));
        } catch (Exception ex) {
            HistoryDao.tryInsert("RSA", "ENCRYPT", plaintext.length(), false);
            JOptionPane.showMessageDialog(this, "Mã hóa thất bại: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btn_decryptActionPerformed(java.awt.event.ActionEvent evt) {
        String ciphertext = txt_ciphertext.getText();
        if (ciphertext.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hãy nhập Ciphertext trước khi giải mã.");
            return;
        }
        try {
            String plaintext = rsa.decrypt(ciphertext);
            txt_plaintext.setText(plaintext);
            txt_plaintext.setCaretPosition(0);
            lbl_status.setText("Đã giải mã RSA. "
                + HistoryDao.tryInsert("RSA", "DECRYPT", ciphertext.length(), true));
        } catch (Exception ex) {
            HistoryDao.tryInsert("RSA", "DECRYPT", ciphertext.length(), false);
            lbl_status.setText("Giải mã RSA THẤT BẠI (sai khóa hoặc bản mã bị sửa).");
            JOptionPane.showMessageDialog(this, "Giải mã thất bại: sai khóa riêng hoặc bản mã bị sửa.\n" + ex,
                "Giải mã thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new frm_RSA().setVisible(true));
    }
}
