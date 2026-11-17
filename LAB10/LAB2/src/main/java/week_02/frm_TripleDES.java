package week_02;

public class frm_TripleDES extends CipherFormBase {

    public frm_TripleDES() {
        super("TripleDES Cipher", "Key (24 ASCII bytes)", "123456789012345678901234");
    }

    @Override
    protected String encryptText(String plaintext, String key) {
        return TripleDESCipher.encrypt(plaintext, key);
    }

    @Override
    protected String decryptText(String ciphertext, String key) {
        return TripleDESCipher.decrypt(ciphertext, key);
    }

    @Override
    protected String algorithmName() {
        return "TripleDES";
    }
}
