package week_02;

public class frm_DES extends CipherFormBase {

    public frm_DES() {
        super("DES Cipher", "Key (8 ASCII bytes)", "12345678");
    }

    @Override
    protected String encryptText(String plaintext, String key) {
        return DESCipher.encrypt(plaintext, key);
    }

    @Override
    protected String decryptText(String ciphertext, String key) {
        return DESCipher.decrypt(ciphertext, key);
    }

    @Override
    protected String algorithmName() {
        return "DES";
    }
}
