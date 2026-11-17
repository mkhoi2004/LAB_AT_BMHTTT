package week_02;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class frm_AES_CTR extends CipherFormBase {

    private final JTextField txt_nonce = new JTextField(AESCTRCipher.newNonce(), 22);
    private final JButton btn_new_key = new JButton("Generate Key");
    private final JButton btn_new_nonce = new JButton("Generate Nonce");

    public frm_AES_CTR() {
        super("AES-CTR Cipher", "Key (Base64, 32 bytes)", AESCTRCipher.newKey());
        optionsPanel.add(new JLabel("Nonce (Base64, 12 bytes):"));
        optionsPanel.add(txt_nonce);
        optionsPanel.add(btn_new_key);
        optionsPanel.add(btn_new_nonce);
        btn_new_key.addActionListener(event -> txt_key.setText(AESCTRCipher.newKey()));
        btn_new_nonce.addActionListener(event -> txt_nonce.setText(AESCTRCipher.newNonce()));
        relayout();
    }

    @Override
    protected String encryptText(String plaintext, String key) {
        return AESCTRCipher.encrypt(plaintext, key, txt_nonce.getText());
    }

    @Override
    protected String decryptText(String ciphertext, String key) {
        return AESCTRCipher.decrypt(ciphertext, key, txt_nonce.getText());
    }

    @Override
    protected String algorithmName() {
        return "AES-CTR";
    }

    @Override
    protected void openFileAction() {
        String content = FileTextIO.openString(this);
        if (content == null) {
            return;
        }
        try {
            String[] values = AESCTRCipher.fromFile(content);
            txt_nonce.setText(values[0]);
            txt_ciphertext.setText(values[1]);
            setStatus("Đã mở tệp ciphertext AES-CTR.", true);
        } catch (IllegalArgumentException ex) {
            setStatus(ex.getMessage(), false);
        }
    }

    @Override
    protected void saveFileAction() {
        try {
            String content = AESCTRCipher.toFile(txt_nonce.getText(), txt_ciphertext.getText());
            String path = FileTextIO.saveString(this, content, "aes-ctr-ciphertext.txt");
            if (path != null) {
                setStatus("Đã lưu ciphertext và nonce: " + path, true);
            }
        } catch (IllegalArgumentException ex) {
            setStatus(ex.getMessage(), false);
        }
    }
}
