package week_02;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class TripleDESCipher {

    private static final String TRANSFORMATION = "DESede/CBC/PKCS5Padding";

    private TripleDESCipher() {
    }

    public static String encrypt(String plaintext, String key) {
        byte[] keyBytes = CipherUtil.requireAsciiKey(key, 24, "DESede");
        byte[] iv = CipherUtil.randomBytes(8);
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "DESede"), new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(CipherUtil.utf8(plaintext));
            byte[] output = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, output, 0, iv.length);
            System.arraycopy(encrypted, 0, output, iv.length, encrypted.length);
            return CipherUtil.b64(output);
        } catch (GeneralSecurityException ex) {
            throw new IllegalArgumentException("Không thể mã hóa TripleDES.", ex);
        }
    }

    public static String decrypt(String ciphertext, String key) {
        byte[] all = CipherUtil.fromB64(ciphertext);
        byte[] keyBytes = CipherUtil.requireAsciiKey(key, 24, "DESede");
        if (all.length < 16 || (all.length - 8) % 8 != 0) {
            throw new IllegalArgumentException("Bản mã TripleDES không hợp lệ hoặc đã bị cắt.");
        }
        byte[] iv = Arrays.copyOfRange(all, 0, 8);
        byte[] encrypted = Arrays.copyOfRange(all, 8, all.length);
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "DESede"), new IvParameterSpec(iv));
            return CipherUtil.text(cipher.doFinal(encrypted));
        } catch (GeneralSecurityException ex) {
            throw new IllegalArgumentException("Giải mã TripleDES thất bại; kiểm tra khóa hoặc bản mã.", ex);
        }
    }
}
