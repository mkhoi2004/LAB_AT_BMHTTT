package week_02;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AESCTRCipher {

    private AESCTRCipher() {
    }

    public static String newKey() {
        return CipherUtil.b64(CipherUtil.randomBytes(32));
    }

    public static String newNonce() {
        return CipherUtil.b64(CipherUtil.randomBytes(12));
    }

    public static String encrypt(String plaintext, String keyBase64, String nonceBase64) {
        return CipherUtil.b64(crypt(Cipher.ENCRYPT_MODE, CipherUtil.utf8(plaintext), keyBase64, nonceBase64));
    }

    public static String decrypt(String ciphertextBase64, String keyBase64, String nonceBase64) {
        return CipherUtil.text(crypt(Cipher.DECRYPT_MODE, CipherUtil.fromB64(ciphertextBase64), keyBase64, nonceBase64));
    }

    public static String toFile(String nonceBase64, String ciphertextBase64) {
        validateKeyAndNonce(CipherUtil.b64(new byte[32]), nonceBase64);
        CipherUtil.fromB64(ciphertextBase64);
        return "NONCE=" + nonceBase64.trim() + System.lineSeparator() + ciphertextBase64.trim();
    }

    public static String[] fromFile(String content) {
        if (content == null) {
            throw new IllegalArgumentException("Tệp AES-CTR không hợp lệ.");
        }
        String[] lines = content.strip().split("\\R", -1);
        if (lines.length != 2 || !lines[0].startsWith("NONCE=")) {
            throw new IllegalArgumentException("Tệp phải có dòng NONCE=... và dòng Base64 bản mã.");
        }
        String nonce = lines[0].substring(6).trim();
        validateKeyAndNonce(CipherUtil.b64(new byte[32]), nonce);
        String ciphertext = lines[1].trim();
        CipherUtil.fromB64(ciphertext);
        return new String[]{nonce, ciphertext};
    }

    private static byte[] crypt(int mode, byte[] input, String keyBase64, String nonceBase64) {
        byte[][] values = validateKeyAndNonce(keyBase64, nonceBase64);
        byte[] counterBlock = ByteBuffer.allocate(16).put(values[1]).putInt(1).array();
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            cipher.init(mode, new SecretKeySpec(values[0], "AES"), new IvParameterSpec(counterBlock));
            return cipher.doFinal(input);
        } catch (GeneralSecurityException ex) {
            throw new IllegalArgumentException("Thao tác AES-CTR thất bại.", ex);
        }
    }

    private static byte[][] validateKeyAndNonce(String keyBase64, String nonceBase64) {
        byte[] key = CipherUtil.fromB64(keyBase64);
        byte[] nonce = CipherUtil.fromB64(nonceBase64);
        if (key.length != 32 || nonce.length != 12) {
            throw new IllegalArgumentException("AES-CTR yêu cầu khóa 32 byte và nonce 12 byte.");
        }
        return new byte[][]{Arrays.copyOf(key, key.length), Arrays.copyOf(nonce, nonce.length)};
    }
}
