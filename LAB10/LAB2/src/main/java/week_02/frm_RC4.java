package week_02;

public class frm_RC4 extends CipherFormBase {

    public frm_RC4() {
        super("RC4 Cipher", "Key", "lab2-key");
    }

    @Override
    protected String encryptText(String plaintext, String key) {
        return RC4Cipher.encrypt(plaintext, key);
    }

    @Override
    protected String decryptText(String ciphertext, String key) {
        return RC4Cipher.decrypt(ciphertext, key);
    }

    @Override
    protected String algorithmName() {
        return "RC4";
    }
}
