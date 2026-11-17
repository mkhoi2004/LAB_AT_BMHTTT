package week_02;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CipherTest {

    private static final String SAMPLE = "Xin chào Lab 2 - Mã hóa hiện đại!";

    @Test
    void desCbcRoundTripsAndUsesFreshIv() {
        String first = DESCipher.encrypt(SAMPLE, "12345678");
        String second = DESCipher.encrypt(SAMPLE, "12345678");

        assertNotEquals(first, second);
        assertEquals(SAMPLE, DESCipher.decrypt(first, "12345678"));
        assertEquals(SAMPLE, DESCipher.decrypt(second, "12345678"));
    }

    @Test
    void desRejectsWrongKeyLengthAndMalformedCiphertext() {
        assertThrows(IllegalArgumentException.class, () -> DESCipher.encrypt(SAMPLE, "1234"));
        assertThrows(IllegalArgumentException.class, () -> DESCipher.decrypt("not base64", "12345678"));
        assertThrows(IllegalArgumentException.class,
                () -> DESCipher.decrypt(CipherUtil.b64(new byte[8]), "12345678"));
    }

    @Test
    void tripleDesRoundTripsAndRejectsTwentyThreeByteKey() {
        String key = "123456789012345678901234";
        String ciphertext = TripleDESCipher.encrypt(SAMPLE, key);

        assertEquals(SAMPLE, TripleDESCipher.decrypt(ciphertext, key));
        assertThrows(IllegalArgumentException.class,
                () -> TripleDESCipher.encrypt(SAMPLE, "12345678901234567890123"));
    }

    @Test
    void aesGcmRoundTripsAndRejectsModifiedTag() {
        String key = "MatKhauAES@2026";
        String ciphertext = AESCipher.encrypt(SAMPLE, key);
        byte[] encoded = Base64.getDecoder().decode(ciphertext);
        encoded[encoded.length - 1] ^= 1;

        assertEquals(SAMPLE, AESCipher.decrypt(ciphertext, key));
        assertThrows(IllegalArgumentException.class,
                () -> AESCipher.decrypt(Base64.getEncoder().encodeToString(encoded), key));
    }

    @Test
    void rc4MatchesRequiredHexVector() {
        assertEquals("BBF316E8D940AF0AD3", RC4Cipher.encrypt("Plaintext", "Key"));
        assertEquals("Plaintext", RC4Cipher.decrypt("BBF316E8D940AF0AD3", "Key"));
    }

    @Test
    void twofishRoundTripsAndRestoresIvAndCiphertextFromFile() {
        String iv = TwofishCipher.newIv();
        String ciphertext = TwofishCipher.encrypt(SAMPLE, "Twofish@Lab2", iv);
        String[] restored = TwofishCipher.fromFile(TwofishCipher.toFile(iv, ciphertext));

        assertTrue(iv.matches("[0-9A-F]{32}"));
        assertEquals(iv, restored[0]);
        assertEquals(ciphertext, restored[1]);
        assertEquals(SAMPLE, TwofishCipher.decrypt(restored[1], "Twofish@Lab2", restored[0]));
    }

    @Test
    void aesCtrPreservesUtf8LengthForNormalAndLongMessages() {
        String key = AESCTRCipher.newKey();
        String nonce = AESCTRCipher.newNonce();
        String longPlaintext = SAMPLE.repeat(40);

        assertCtrRoundTrip(SAMPLE, key, nonce);
        assertCtrRoundTrip(longPlaintext, key, nonce);
    }

    @Test
    void aesCtrRejectsInvalidKeyAndNonceLengths() {
        assertThrows(IllegalArgumentException.class,
                () -> AESCTRCipher.encrypt(SAMPLE, Base64.getEncoder().encodeToString(new byte[31]),
                        Base64.getEncoder().encodeToString(new byte[12])));
        assertThrows(IllegalArgumentException.class,
                () -> AESCTRCipher.encrypt(SAMPLE, Base64.getEncoder().encodeToString(new byte[32]),
                        Base64.getEncoder().encodeToString(new byte[11])));
    }

    private static void assertCtrRoundTrip(String plaintext, String key, String nonce) {
        String ciphertext = AESCTRCipher.encrypt(plaintext, key, nonce);
        byte[] ciphertextBytes = Base64.getDecoder().decode(ciphertext);

        assertEquals(plaintext.getBytes(StandardCharsets.UTF_8).length, ciphertextBytes.length);
        assertEquals(plaintext, AESCTRCipher.decrypt(ciphertext, key, nonce));
    }
}
