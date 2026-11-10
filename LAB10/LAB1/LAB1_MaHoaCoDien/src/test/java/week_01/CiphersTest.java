package week_01;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CiphersTest {

    @Test
    public void testCaesar() {
        String plain = "HELLO";
        int key = 3;
        String cipher = CaesarCipher.encrypt(plain, key);
        assertEquals("KHOOR", cipher);
        assertEquals(plain, CaesarCipher.decrypt(cipher, key));
    }

    @Test
    public void testVigenere() {
        String plain = "HELLO";
        String key = "KEY";
        String cipher = VigenereCipher.encrypt(plain, key);
        assertEquals("RIJVS", cipher);
        assertEquals(plain, VigenereCipher.decrypt(cipher, key));
    }

    @Test
    public void testRailFence() {
        String plain = "HELLO WORLD";
        int rails = 3;
        String cipher = RailFenceCipher.encrypt(plain, rails);
        assertEquals("HOREL OLLWD", cipher);
        assertEquals(plain, RailFenceCipher.decrypt(cipher, rails));
    }

    @Test
    public void testPlayfair() {
        String plain = "HELLOWORLD";
        String key = "KEYWORD";
        String cipher = PlayfairCipher.encrypt(plain, key);
        assertEquals("GYIZSCOKCFBU", cipher);
        assertEquals("HELXLOWORLDX", PlayfairCipher.decrypt(cipher, key));
    }

    @Test
    public void testTransposition() {
        String plain = "BAOMAT";
        int[] key = TranspositionCipher.parseKey("2,3,1");
        String cipher = TranspositionCipher.encrypt(plain, key);
        assertEquals("AAOTBM", cipher);
        assertEquals(plain, TranspositionCipher.decrypt(cipher, key));
    }

    @Test
    public void testAutokey() {
        String plain = "HELLO";
        String key = "KEY";
        String cipher = AutokeyCipher.encrypt(plain, key);
        assertEquals("RIJSS", cipher);
        assertEquals(plain, AutokeyCipher.decrypt(cipher, key));
    }

    @Test
    public void testCipherTongHop() {
        for (String algo : CipherTongHop.ALGORITHMS) {
            String sample = CipherTongHop.sampleKey(algo);
            assertNotNull(sample);
            String input = algo.equals("Playfair") ? "HELLOWORLD" : (algo.equals("Transposition") ? "BAOMAT" : "HELLO");
            String encrypted = CipherTongHop.encrypt(algo, input, sample);
            assertNotNull(encrypted);
            String decrypted = CipherTongHop.decrypt(algo, encrypted, sample);
            assertNotNull(decrypted);
        }
    }

    @Test
    public void testAttackDemos() {
        String brute = AttackDemos.caesarBruteForce("KHOOR");
        assertTrue(brute.contains("khóa k = 3"));
        assertTrue(brute.contains("HELLO"));

        String freq = AttackDemos.frequency("PHHW PH DIWHU WKH WRJD SDUWB");
        assertTrue(freq.contains("Tổng số chữ: 23"));

        String key = AttackDemos.knownPlaintext("HELLO", "RIJVS");
        assertEquals("KEYKE", key);
    }

    @Test
    public void testFormsClasses() {
        assertNotNull(frm_Menu.class);
        assertNotNull(frm_Caesar.class);
        assertNotNull(frm_Vigenere.class);
        assertNotNull(frm_RailFence.class);
        assertNotNull(frm_Playfair.class);
        assertNotNull(frm_Transposition.class);
        assertNotNull(frm_Autokey.class);
        assertNotNull(frm_TongHop.class);
        assertNotNull(frm_KetNoiSQL.class);
        assertNotNull(frm_LichSu.class);
        assertNotNull(frm_TanCong.class);
    }
}
