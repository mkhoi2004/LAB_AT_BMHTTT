package week_02;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AESCipher {

    private static final int ITERATIONS = 120_000;
    private static final int SALT_BYTES = 16;
    private static final int NONCE_BYTES = 12;
    private static final int TAG_BITS = 128;
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private AESCipher() {
    }

    public static String encrypt(String plaintext, String password) {
        CipherUtil.requireKey(password);
        byte[] salt = CipherUtil.randomBytes(SALT_BYTES);
        byte[] nonce = CipherUtil.randomBytes(NONCE_BYTES);
        byte[] key = CipherUtil.deriveKey(password.toCharArray(), salt, ITERATIONS, 32);
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(TAG_BITS, nonce));
            byte[] encrypted = cipher.doFinal(CipherUtil.utf8(plaintext));
            byte[] output = new byte[salt.length + nonce.length + encrypted.length];
            System.arraycopy(salt, 0, output, 0, salt.length);
            System.arraycopy(nonce, 0, output, salt.length, nonce.length);
            System.arraycopy(encrypted, 0, output, salt.length + nonce.length, encrypted.length);
            return CipherUtil.b64(output);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Không thể mã hóa AES-GCM.", ex);
        } finally {
            Arrays.fill(key, (byte) 0);
        }
    }

    public static String decrypt(String ciphertext, String password) {
        CipherUtil.requireKey(password);
        byte[] all = CipherUtil.fromB64(ciphertext);
        if (all.length < SALT_BYTES + NONCE_BYTES + TAG_BITS / 8) {
            throw new IllegalArgumentException("Bản mã AES-GCM không hợp lệ hoặc đã bị cắt.");
        }
        byte[] salt = Arrays.copyOfRange(all, 0, SALT_BYTES);
        byte[] nonce = Arrays.copyOfRange(all, SALT_BYTES, SALT_BYTES + NONCE_BYTES);
        byte[] encrypted = Arrays.copyOfRange(all, SALT_BYTES + NONCE_BYTES, all.length);
        byte[] key = CipherUtil.deriveKey(password.toCharArray(), salt, ITERATIONS, 32);
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(TAG_BITS, nonce));
            return CipherUtil.text(cipher.doFinal(encrypted));
        } catch (GeneralSecurityException ex) {
            throw new IllegalArgumentException("Giải mã AES thất bại: sai mật khẩu hoặc bản mã bị sửa.", ex);
        } finally {
            Arrays.fill(key, (byte) 0);
        }
    }
}
