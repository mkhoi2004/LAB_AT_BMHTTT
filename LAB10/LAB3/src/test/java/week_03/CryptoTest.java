package week_03;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import javax.swing.table.DefaultTableModel;

public class CryptoTest {

    @BeforeAll
    public static void setupDatabase() {
        try {
            DbConfig.set("127.0.0.1", 1433, "LAB3_MaHoaKhoaCongKhai", "lab3_user", "LabUser@2026".toCharArray());
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testCryptoSupport() throws Exception {
        byte[] data = "Hello CryptoSupport".getBytes();
        String b64 = CryptoSupport.b64(data);
        assertArrayEquals(data, CryptoSupport.unb64(b64));

        byte[] key = CryptoSupport.randomBytes(32);
        byte[] nonce = CryptoSupport.randomBytes(12);
        byte[] enc = CryptoSupport.gcmEncrypt(key, nonce, data);
        byte[] dec = CryptoSupport.gcmDecrypt(key, nonce, enc);
        assertArrayEquals(data, dec);

        String keyFile = CryptoSupport.toKeyFile("PUBKEY123", "PRIKEY456");
        String[] parsed = CryptoSupport.fromKeyFile(keyFile);
        assertEquals("PUBKEY123", parsed[0]);
        assertEquals("PRIKEY456", parsed[1]);
    }

    @Test
    public void testRSACipher2048And3072() throws Exception {
        RSACipher rsa = new RSACipher();
        rsa.generateKeys(2048);
        assertEquals(2048, rsa.getBits());
        assertEquals(190, rsa.maxPlaintextBytes());

        String plain = "HELLO RSA 2048";
        String cipher = rsa.encrypt(plain);
        assertNotNull(cipher);
        assertEquals(plain, rsa.decrypt(cipher));

        // Test reload keys
        String pub = rsa.getPublicKeyBase64();
        String pri = rsa.getPrivateKeyBase64();
        RSACipher rsaReload = new RSACipher();
        rsaReload.loadKeys(pub, pri);
        assertEquals(plain, rsaReload.decrypt(cipher));

        // Test 3072
        rsa.generateKeys(3072);
        assertEquals(3072, rsa.getBits());
        assertEquals(318, rsa.maxPlaintextBytes());
        String plain3072 = "HELLO RSA 3072";
        String cipher3072 = rsa.encrypt(plain3072);
        assertEquals(plain3072, rsa.decrypt(cipher3072));
    }

    @Test
    public void testECCCipher() throws Exception {
        ECCCipher ecc1 = new ECCCipher();
        ecc1.generateKeys();
        assertNotNull(ecc1.getPublicKeyBase64());
        assertNotNull(ecc1.getPrivateKeyBase64());

        String plain = "HELLO ECC";
        String cipher = ecc1.encrypt(plain);
        assertNotNull(cipher);
        assertEquals(plain, ecc1.decrypt(cipher));

        // Decrypt with different key should fail
        ECCCipher ecc2 = new ECCCipher();
        ecc2.generateKeys();
        assertThrows(Exception.class, () -> ecc2.decrypt(cipher));
    }

    @Test
    public void testRSA_AES_HybridCipher() throws Exception {
        RSA_AES_Cipher hybrid = new RSA_AES_Cipher();
        hybrid.generateKeys();

        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            longText.append("An toàn bảo mật hệ thống thông tin - Mật mã lai RSA kết hợp AES-GCM. ");
        }
        assertTrue(longText.length() > 1000);

        String cipher = hybrid.encrypt(longText.toString());
        assertNotNull(cipher);
        String dec = hybrid.decrypt(cipher);
        assertEquals(longText.toString(), dec);

        // Tamper test
        char[] chars = cipher.toCharArray();
        int mid = chars.length / 2;
        chars[mid] = chars[mid] == 'A' ? 'B' : 'A';
        String tampered = new String(chars);
        assertThrows(Exception.class, () -> hybrid.decrypt(tampered));
    }

    @Test
    public void testECCSignature() throws Exception {
        ECCSignature sig = new ECCSignature();
        sig.generateKeys();

        String msgA = "THONG DIEP A";
        String signature = sig.sign(msgA);
        assertTrue(sig.verify(msgA, signature));

        String msgB = "THONG DIEP B";
        assertFalse(sig.verify(msgB, signature));

        // Different public key should fail
        ECCSignature sig2 = new ECCSignature();
        sig2.generateKeys();
        assertFalse(sig2.verify(msgA, signature));
    }

    @Test
    public void testDatabaseConnectionAndHistory() {
        try (Connection conn = DbConfig.open()) {
            assertNotNull(conn);
            String ins = HistoryDao.tryInsert("TEST", "ENCRYPT", 100, true);
            assertTrue(ins.contains("Đã lưu"));

            DefaultTableModel model = HistoryDao.loadLatest(10);
            assertTrue(model.getRowCount() > 0);
        } catch (Exception ex) {
            System.out.println("SQL connection note: " + ex.getMessage());
        }
    }
}
