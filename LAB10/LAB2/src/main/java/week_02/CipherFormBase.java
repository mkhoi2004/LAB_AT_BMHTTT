package week_02;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.nio.charset.StandardCharsets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

abstract class CipherFormBase extends JFrame {

    protected final JTextArea txt_plaintext = new JTextArea(12, 36);
    protected final JTextArea txt_ciphertext = new JTextArea(12, 36);
    protected final JTextField txt_key = new JTextField(32);
    protected final JButton btn_encrypt = new JButton("Encrypt");
    protected final JButton btn_decrypt = new JButton("Decrypt");
    protected final JButton btn_open = new JButton("Open File");
    protected final JButton btn_save = new JButton("Save File");
    protected final JButton btn_clear = new JButton("Clear");
    protected final JLabel lbl_status = new JLabel("Ready.");
    protected final JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

    CipherFormBase(String title, String keyLabel, String keyDefault) {
        super(title);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel heading = new JLabel(title, SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setBorder(BorderFactory.createEmptyBorder(12, 8, 8, 8));

        txt_plaintext.setLineWrap(true);
        txt_plaintext.setWrapStyleWord(true);
        txt_ciphertext.setLineWrap(true);
        txt_ciphertext.setWrapStyleWord(true);

        JPanel textAreas = new JPanel(new GridLayout(1, 2, 12, 0));
        textAreas.add(createTextPanel("Plaintext", txt_plaintext));
        textAreas.add(createTextPanel("Ciphertext", txt_ciphertext));

        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        keyPanel.add(new JLabel(keyLabel));
        txt_key.setText(keyDefault);
        txt_key.setPreferredSize(new Dimension(380, txt_key.getPreferredSize().height));
        keyPanel.add(txt_key);

        JPanel controls = new JPanel(new BorderLayout(4, 4));
        controls.add(keyPanel, BorderLayout.NORTH);
        controls.add(optionsPanel, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        buttons.add(btn_encrypt);
        buttons.add(btn_decrypt);
        buttons.add(btn_open);
        buttons.add(btn_save);
        buttons.add(btn_clear);

        lbl_status.setForeground(new Color(45, 80, 120));
        lbl_status.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(lbl_status, BorderLayout.SOUTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(4, 14, 8, 14));
        content.add(textAreas, BorderLayout.CENTER);
        content.add(controls, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(heading, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        btn_encrypt.addActionListener(event -> encryptAction());
        btn_decrypt.addActionListener(event -> decryptAction());
        btn_open.addActionListener(event -> openFileAction());
        btn_save.addActionListener(event -> saveFileAction());
        btn_clear.addActionListener(event -> clearText());

        setMinimumSize(new Dimension(760, 500));
        relayout();
    }

    protected abstract String encryptText(String plaintext, String key);

    protected abstract String decryptText(String ciphertext, String key);

    protected abstract String algorithmName();

    protected String historyUsername() {
        return null;
    }

    protected void encryptAction() {
        String plaintext = txt_plaintext.getText();
        if (plaintext.isEmpty()) {
            setStatus("Nhập plaintext trước khi mã hóa.", false);
            return;
        }
        try {
            String ciphertext = encryptText(plaintext, txt_key.getText());
            txt_ciphertext.setText(ciphertext);
            String history = HistoryDao.tryInsert(algorithmName(), "ENCRYPT", historyUsername(),
                    plaintext.getBytes(StandardCharsets.UTF_8).length,
                    ciphertext.getBytes(StandardCharsets.UTF_8).length);
            setStatus("Đã mã hóa. " + history, true);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            setStatus(ex.getMessage(), false);
        }
    }

    protected void decryptAction() {
        String ciphertext = txt_ciphertext.getText();
        if (ciphertext.isEmpty()) {
            setStatus("Nhập ciphertext trước khi giải mã.", false);
            return;
        }
        try {
            String plaintext = decryptText(ciphertext, txt_key.getText());
            txt_plaintext.setText(plaintext);
            String history = HistoryDao.tryInsert(algorithmName(), "DECRYPT", historyUsername(),
                    ciphertext.getBytes(StandardCharsets.UTF_8).length,
                    plaintext.getBytes(StandardCharsets.UTF_8).length);
            setStatus("Đã giải mã. " + history, true);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            setStatus(ex.getMessage(), false);
        }
    }

    protected void openFileAction() {
        FileTextIO.openText(this, txt_plaintext);
    }

    protected void saveFileAction() {
        String path = FileTextIO.saveText(this, txt_ciphertext, "ciphertext.txt");
        if (path != null) {
            setStatus("Đã lưu tệp: " + path, true);
        }
    }

    protected final void setStatus(String message, boolean success) {
        lbl_status.setForeground(success ? new Color(35, 115, 65) : new Color(170, 45, 45));
        lbl_status.setText(message == null || message.isBlank() ? "Đã xảy ra lỗi." : message);
    }

    protected final void relayout() {
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createTextPanel(String label, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        JLabel title = new JLabel(label);
        title.setFont(title.getFont().deriveFont(Font.BOLD));
        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private void clearText() {
        txt_plaintext.setText("");
        txt_ciphertext.setText("");
        setStatus("Ready.", true);
    }
}
