package week_03;

import java.awt.*;
import javax.swing.*;

public class frm_ECC extends javax.swing.JFrame {

    private final ECCCipher ecc = new ECCCipher();

    private JTextArea txt_public;
    private JTextArea txt_private;
    private JButton btn_generate;
    private JButton btn_savekeys;
    private JButton btn_loadkeys;
    private JTextArea txt_plaintext;
    private JTextArea txt_ciphertext;
    private JButton btn_encrypt;
    private JButton btn_decrypt;
    private JLabel lbl_status;

    public frm_ECC() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("ECC Cipher");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel lblTitle = new JLabel("MẬT MÃ ĐƯỜNG CONG ELLIPTIC (ECC - secp256r1)", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(25, 60, 120));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(lblTitle, gbc);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 1;

        // Row 1: Public key
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblPub = new JLabel("Public key:");
        lblPub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblPub, gbc);

        txt_public = new JTextArea(2, 45);
        txt_public.setLineWrap(true);
        txt_public.setWrapStyleWord(true);
        txt_public.setEditable(false);
        txt_public.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt_public.setBackground(new Color(245, 245, 245));
        JScrollPane spPub = new JScrollPane(txt_public);
        spPub.setPreferredSize(new Dimension(600, 48));
        gbc.gridx = 1;
        mainPanel.add(spPub, gbc);

        // Row 2: Private key
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblPri = new JLabel("Private key:");
        lblPri.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblPri, gbc);

        txt_private = new JTextArea(2, 45);
        txt_private.setLineWrap(true);
        txt_private.setWrapStyleWord(true);
        txt_private.setEditable(false);
        txt_private.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt_private.setBackground(new Color(245, 245, 245));
        JScrollPane spPri = new JScrollPane(txt_private);
        spPri.setPreferredSize(new Dimension(600, 48));
        gbc.gridx = 1;
        mainPanel.add(spPri, gbc);

        // Row 3: Key action buttons
        JPanel pnlKeyBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btn_generate = new JButton("Generate Keys");
        btn_generate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_generate.setPreferredSize(new Dimension(140, 30));

        btn_savekeys = new JButton("Save Keys");
        btn_savekeys.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn_savekeys.setPreferredSize(new Dimension(120, 30));

        btn_loadkeys = new JButton("Load Keys");
        btn_loadkeys.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn_loadkeys.setPreferredSize(new Dimension(120, 30));

        pnlKeyBtns.add(btn_generate);
        pnlKeyBtns.add(btn_savekeys);
        pnlKeyBtns.add(btn_loadkeys);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 0, 8, 0);
        mainPanel.add(pnlKeyBtns, gbc);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridwidth = 1;

        // Row 4: Plaintext
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblPlain = new JLabel("Plaintext:");
        lblPlain.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblPlain, gbc);

        txt_plaintext = new JTextArea(2, 45);
        txt_plaintext.setLineWrap(true);
        txt_plaintext.setWrapStyleWord(true);
        txt_plaintext.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane spPlain = new JScrollPane(txt_plaintext);
        spPlain.setPreferredSize(new Dimension(600, 56));
        gbc.gridx = 1;
        mainPanel.add(spPlain, gbc);

        // Row 5: Ciphertext
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lblCipher = new JLabel("Ciphertext:");
        lblCipher.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblCipher, gbc);

        txt_ciphertext = new JTextArea(3, 45);
        txt_ciphertext.setLineWrap(true);
        txt_ciphertext.setWrapStyleWord(true);
        txt_ciphertext.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane spCipher = new JScrollPane(txt_ciphertext);
        spCipher.setPreferredSize(new Dimension(600, 80));
        gbc.gridx = 1;
        mainPanel.add(spCipher, gbc);

        // Row 6: Cipher action buttons
        JPanel pnlCipherBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btn_encrypt = new JButton("Encrypt");
        btn_encrypt.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_encrypt.setPreferredSize(new Dimension(110, 32));

        btn_decrypt = new JButton("Decrypt");
        btn_decrypt.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_decrypt.setPreferredSize(new Dimension(110, 32));

        pnlCipherBtns.add(btn_encrypt);
        pnlCipherBtns.add(btn_decrypt);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 8, 0);
        mainPanel.add(pnlCipherBtns, gbc);

        // Row 7: Status
        lbl_status = new JLabel("Bấm Generate Keys hoặc Load Keys.", SwingConstants.CENTER);
        lbl_status.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lbl_status.setPreferredSize(new Dimension(600, 36));
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(lbl_status, gbc);

        add(mainPanel);
        pack();

        btn_generate.addActionListener(evt -> btn_generateActionPerformed(evt));
        btn_savekeys.addActionListener(evt -> btn_savekeysActionPerformed(evt));
        btn_loadkeys.addActionListener(evt -> btn_loadkeysActionPerformed(evt));
        btn_encrypt.addActionListener(evt -> btn_encryptActionPerformed(evt));
        btn_decrypt.addActionListener(evt -> btn_decryptActionPerformed(evt));
    }

    private void showKeys() {
        txt_public.setText(ecc.getPublicKeyBase64());
        txt_private.setText(ecc.getPrivateKeyBase64());
        txt_public.setCaretPosition(0);
        txt_private.setCaretPosition(0);
    }

    private void btn_generateActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            ecc.generateKeys();
            showKeys();
            lbl_status.setText("Đã tạo cặp khóa mới. Bản mã/chữ ký cũ sẽ không dùng được với khóa này.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không tạo được khóa: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btn_savekeysActionPerformed(java.awt.event.ActionEvent evt) {
        if (txt_public.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có khóa để lưu.");
            return;
        }
        String content = CryptoSupport.toKeyFile(txt_public.getText(), txt_private.getText());
        String path = FileTextIO.saveString(this, content, "ecc_keys.txt");
        if (path != null) {
            lbl_status.setText("Đã lưu khóa vào: " + path + " (giữ bí mật dòng PRIVATE).");
        }
    }

    private void btn_loadkeysActionPerformed(java.awt.event.ActionEvent evt) {
        String content = FileTextIO.openString(this);
        if (content == null) {
            return;
        }
        try {
            String[] keys = CryptoSupport.fromKeyFile(content);
            ecc.loadKeys(keys[0], keys[1]);
            txt_public.setText(keys[0]);
            txt_private.setText(keys[1]);
            lbl_status.setText("Đã nạp khóa từ tệp.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Tệp khóa không hợp lệ: " + ex.getMessage(),
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
            String ciphertext = ecc.encrypt(plaintext);
            txt_ciphertext.setText(ciphertext);
            txt_ciphertext.setCaretPosition(0);
            lbl_status.setText("Đã mã hóa ECC (" + ciphertext.length() + " ký tự Base64). "
                + HistoryDao.tryInsert("ECC", "ENCRYPT", plaintext.length(), true));
        } catch (Exception ex) {
            HistoryDao.tryInsert("ECC", "ENCRYPT", plaintext.length(), false);
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
            String plaintext = ecc.decrypt(ciphertext);
            txt_plaintext.setText(plaintext);
            txt_plaintext.setCaretPosition(0);
            lbl_status.setText("Đã giải mã ECC. "
                + HistoryDao.tryInsert("ECC", "DECRYPT", ciphertext.length(), true));
        } catch (Exception ex) {
            HistoryDao.tryInsert("ECC", "DECRYPT", ciphertext.length(), false);
            lbl_status.setText("Giải mã ECC THẤT BẠI (sai khóa hoặc bản mã bị sửa).");
            JOptionPane.showMessageDialog(this, "Giải mã thất bại: sai khóa riêng hoặc bản mã bị sửa.\n" + ex,
                "Giải mã thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new frm_ECC().setVisible(true));
    }
}
