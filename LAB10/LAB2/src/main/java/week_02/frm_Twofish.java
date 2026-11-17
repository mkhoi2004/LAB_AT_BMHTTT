package week_02;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class frm_Twofish extends CipherFormBase {

    private final JTextField txt_iv = new JTextField(TwofishCipher.newIv(), 34);
    private final JButton btn_new_iv = new JButton("New IV");

    public frm_Twofish() {
        super("Twofish Cipher", "Key", "lab2-twofish-key");
        optionsPanel.add(new JLabel("IV (32 HEX chars):"));
        optionsPanel.add(txt_iv);
        optionsPanel.add(btn_new_iv);
        btn_new_iv.addActionListener(event -> txt_iv.setText(TwofishCipher.newIv()));
        relayout();
    }

    @Override
    protected String encryptText(String plaintext, String key) {
        return TwofishCipher.encrypt(plaintext, key, txt_iv.getText());
    }

    @Override
    protected String decryptText(String ciphertext, String key) {
        return TwofishCipher.decrypt(ciphertext, key, txt_iv.getText());
    }

    @Override
    protected String algorithmName() {
        return "Twofish";
    }

    @Override
    protected void openFileAction() {
        String content = FileTextIO.openString(this);
        if (content == null) {
            return;
        }
        try {
            String[] values = TwofishCipher.fromFile(content);
            txt_iv.setText(values[0]);
            txt_ciphertext.setText(values[1]);
            setStatus("Đã mở tệp ciphertext Twofish.", true);
        } catch (IllegalArgumentException ex) {
            setStatus(ex.getMessage(), false);
        }
    }

    @Override
    protected void saveFileAction() {
        try {
            String content = TwofishCipher.toFile(txt_iv.getText(), txt_ciphertext.getText());
            String path = FileTextIO.saveString(this, content, "twofish-ciphertext.txt");
            if (path != null) {
                setStatus("Đã lưu ciphertext và IV: " + path, true);
            }
        } catch (IllegalArgumentException ex) {
            setStatus(ex.getMessage(), false);
        }
    }
}
