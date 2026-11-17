package week_02;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class CipherUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private CipherUtil() {
    }

    public static byte[] utf8(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Giá trị không được để trống.");
        }
        return value.getBytes(StandardCharsets.UTF_8);
    }

    public static String text(byte[] value) {
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(value))
                    .toString();
        } catch (CharacterCodingException ex) {
            throw new IllegalArgumentException("Dữ liệu giải mã không phải UTF-8 hợp lệ.", ex);
        }
    }

    public static String b64(byte[] value) {
        return Base64.getEncoder().encodeToString(value);
    }

    public static byte[] fromB64(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Dữ liệu Base64 không được để trống.");
        }
        try {
            return Base64.getDecoder().decode(value.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Dữ liệu không phải Base64 hợp lệ.", ex);
        }
    }

    public static String hex(byte[] value) {
        return java.util.HexFormat.of().withUpperCase().formatHex(value);
    }

    public static byte[] fromHex(String value) {
        if (value == null || value.length() % 2 != 0) {
            throw new IllegalArgumentException("Dữ liệu HEX phải có số ký tự chẵn.");
        }
        try {
            return java.util.HexFormat.of().parseHex(value);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Dữ liệu không phải HEX hợp lệ.", ex);
        }
    }

    public static byte[] randomBytes(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("Độ dài ngẫu nhiên phải lớn hơn 0.");
        }
        byte[] value = new byte[length];
        RANDOM.nextBytes(value);
        return value;
    }

    public static byte[] deriveKey(char[] password, byte[] salt, int iterations, int keyBytes) {
        if (password == null || password.length == 0 || salt == null || salt.length == 0
                || iterations < 1 || keyBytes < 1) {
            throw new IllegalArgumentException("Tham số dẫn xuất khóa không hợp lệ.");
        }
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, Math.multiplyExact(keyBytes, 8));
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Không thể dẫn xuất khóa PBKDF2.", ex);
        } finally {
            spec.clearPassword();
        }
    }

    public static void requireKey(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Khóa không được để trống.");
        }
    }

    static byte[] requireAsciiKey(String key, int expectedBytes, String algorithm) {
        requireKey(key);
        if (key.length() != expectedBytes || !key.chars().allMatch(ch -> ch <= 0x7F)) {
            throw new IllegalArgumentException(
                    "Khóa " + algorithm + " phải đúng " + expectedBytes + " byte ASCII.");
        }
        return key.getBytes(StandardCharsets.US_ASCII);
    }
}
