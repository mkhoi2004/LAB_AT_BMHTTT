package week_02;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Security;
import java.util.Arrays;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class TwofishCipher {

    private static final String TRANSFORMATION = "Twofish/CBC/PKCS7Padding";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private TwofishCipher() {
    }

    public static String newIv() {
        return CipherUtil.hex(CipherUtil.randomBytes(16));
    }

    public static String encrypt(String plaintext, String key, String ivHex) {
        return CipherUtil.b64(crypt(Cipher.ENCRYPT_MODE, CipherUtil.utf8(plaintext), key, ivHex));
    }

    public static String decrypt(String ciphertext, String key, String ivHex) {
        return CipherUtil.text(crypt(Cipher.DECRYPT_MODE, CipherUtil.fromB64(ciphertext), key, ivHex));
    }

    public static String toFile(String ivHex, String ciphertext) {
        validateIv(ivHex);
        CipherUtil.fromB64(ciphertext);
        return "IV=" + ivHex.toUpperCase(java.util.Locale.ROOT) + System.lineSeparator() + ciphertext;
    }

    public static String[] fromFile(String content) {
        if (content == null) {
            throw new IllegalArgumentException("Tệp Twofish không hợp lệ.");
        }
        String[] lines = content.strip().split("\\R", -1);
        if (lines.length != 2 || !lines[0].startsWith("IV=")) {
            throw new IllegalArgumentException("Tệp phải có dòng IV=... và dòng Base64 bản mã.");
        }
        String iv = lines[0].substring(3).trim().toUpperCase(java.util.Locale.ROOT);
        validateIv(iv);
        String ciphertext = lines[1].trim();
        CipherUtil.fromB64(ciphertext);
        return new String[]{iv, ciphertext};
    }

    private static byte[] crypt(int mode, byte[] input, String key, String ivHex) {
        CipherUtil.requireKey(key);
        byte[] iv = validateIv(ivHex);
        try {
            byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(CipherUtil.utf8(key));
            Cipher cipher = Cipher.getInstance(TRANSFORMATION, BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(mode, new SecretKeySpec(keyBytes, "Twofish"), new IvParameterSpec(iv));
            return cipher.doFinal(input);
        } catch (GeneralSecurityException ex) {
            throw new IllegalArgumentException("Thao tác Twofish thất bại; kiểm tra khóa, IV hoặc bản mã.", ex);
        }
    }

    private static byte[] validateIv(String ivHex) {
        byte[] iv = CipherUtil.fromHex(ivHex);
        if (iv.length != 16) {
            throw new IllegalArgumentException("IV Twofish phải đúng 16 byte = 32 ký tự HEX.");
        }
        return Arrays.copyOf(iv, iv.length);
    }
}
