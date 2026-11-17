package week_02;

public final class RC4Cipher {

    private RC4Cipher() {
    }

    public static String encrypt(String plaintext, String key) {
        byte[] keyBytes = CipherUtil.utf8(key);
        return CipherUtil.hex(apply(CipherUtil.utf8(plaintext), keyBytes));
    }

    public static String decrypt(String hexCiphertext, String key) {
        byte[] keyBytes = CipherUtil.utf8(key);
        return CipherUtil.text(apply(CipherUtil.fromHex(hexCiphertext), keyBytes));
    }

    private static byte[] apply(byte[] data, byte[] key) {
        if (key.length < 1 || key.length > 256) {
            throw new IllegalArgumentException("Khóa RC4 phải từ 1 đến 256 byte.");
        }
        int[] state = new int[256];
        for (int i = 0; i < state.length; i++) {
            state[i] = i;
        }
        int j = 0;
        for (int i = 0; i < state.length; i++) {
            j = (j + state[i] + (key[i % key.length] & 0xFF)) & 0xFF;
            int swap = state[i];
            state[i] = state[j];
            state[j] = swap;
        }
        byte[] result = new byte[data.length];
        int i = 0;
        j = 0;
        for (int position = 0; position < data.length; position++) {
            i = (i + 1) & 0xFF;
            j = (j + state[i]) & 0xFF;
            int swap = state[i];
            state[i] = state[j];
            state[j] = swap;
            int streamByte = state[(state[i] + state[j]) & 0xFF];
            result[position] = (byte) (data[position] ^ streamByte);
        }
        return result;
    }
}
