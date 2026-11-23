package week_03;

import java.awt.*;
import javax.swing.*;

public class frm_ECC_Signature extends javax.swing.JFrame {

    private final ECCSignature ecdsa = new ECCSignature();

    private JTextArea txt_public;
    private JTextArea txt_private;
    private JButton btn_generate;
    private JButton btn_savekeys;
    private JButton btn_loadkeys;
    private JTextArea txt_message;
    private JTextArea txt_signature;
    private JButton btn_sign;
    private JButton btn_verify;
    private JLabel lbl_result;
    private JLabel lbl_status;

    public frm_ECC_Signature() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("ECDSA Signature");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title
        JLabel lblTitle = new JLabel("CHỮ KÝ SỐ ECDSA (SHA256withECDSA)", SwingConstants.CENTER);
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

        // Row 4: Message
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblMsg = new JLabel("Message:");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblMsg, gbc);

        txt_message = new JTextArea(2, 45);
        txt_message.setLineWrap(true);
        txt_message.setWrapStyleWord(true);
        txt_message.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane spMsg = new JScrollPane(txt_message);
        spMsg.setPreferredSize(new Dimension(600, 56));
        gbc.gridx = 1;
        mainPanel.add(spMsg, gbc);

        // Row 5: Signature
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel lblSig = new JLabel("Signature:");
        lblSig.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        mainPanel.add(lblSig, gbc);

        txt_signature = new JTextArea(2, 45);
        txt_signature.setLineWrap(true);
        txt_signature.setWrapStyleWord(true);
        txt_signature.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane spSig = new JScrollPane(txt_signature);
        spSig.setPreferredSize(new Dimension(600, 56));
        gbc.gridx = 1;
        mainPanel.add(spSig, gbc);

        // Row 6: Sign/Verify buttons + Result label
        JPanel pnlSigBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btn_sign = new JButton("Sign");
        btn_sign.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_sign.setPreferredSize(new Dimension(110, 32));

        btn_verify = new JButton("Verify");
        btn_verify.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_verify.setPreferredSize(new Dimension(110, 32));

        lbl_result = new JLabel("", SwingConstants.CENTER);
        lbl_result.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl_result.setPreferredSize(new Dimension(140, 32));

        pnlSigBtns.add(btn_sign);
        pnlSigBtns.add(btn_verify);
        pnlSigBtns.add(lbl_result);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 0, 8, 0);
        mainPanel.add(pnlSigBtns, gbc);

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
        btn_sign.addActionListener(evt -> btn_signActionPerformed(evt));
        btn_verify.addActionListener(evt -> btn_verifyActionPerformed(evt));
    }

    private void showKeys() {
        txt_public.setText(ecdsa.getPublicKeyBase64());
        txt_private.setText(ecdsa.getPrivateKeyBase64());
        txt_public.setCaretPosition(0);
        txt_private.setCaretPosition(0);
    }

    private void btn_generateActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            ecdsa.generateKeys();
            showKeys();
            lbl_result.setText("");
            lbl_status.setText("Đã tạo cặp khóa ECDSA mới. Khóa riêng dùng để ký, khóa công khai để xác minh.");
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
        String path = FileTextIO.saveString(this, content, "ecdsa_keys.txt");
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
            ecdsa.loadKeys(keys[0], keys[1]);
            txt_public.setText(keys[0]);
            txt_private.setText(keys[1]);
            lbl_result.setText("");
            lbl_status.setText("Đã nạp khóa từ tệp.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Tệp khóa không hợp lệ: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btn_signActionPerformed(java.awt.event.ActionEvent evt) {
        String message = txt_message.getText();
        try {
            txt_signature.setText(ecdsa.sign(message));
            lbl_result.setText("");
            lbl_status.setText("Đã ký bằng KHÓA RIÊNG. "
                + HistoryDao.tryInsert("ECDSA", "SIGN", message.length(), true));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ký thất bại: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btn_verifyActionPerformed(java.awt.event.ActionEvent evt) {
        String message = txt_message.getText();
        try {
            boolean ok = ecdsa.verify(message, txt_signature.getText());
            lbl_result.setText(ok ? "VALID" : "INVALID");
            lbl_result.setForeground(ok ? new java.awt.Color(0, 128, 0) : java.awt.Color.RED);
            lbl_status.setText("Đã xác minh bằng KHÓA CÔNG KHAI. "
                + HistoryDao.tryInsert("ECDSA", "VERIFY", message.length(), ok));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Xác minh thất bại: " + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new frm_ECC_Signature().setVisible(true));
    }
}
