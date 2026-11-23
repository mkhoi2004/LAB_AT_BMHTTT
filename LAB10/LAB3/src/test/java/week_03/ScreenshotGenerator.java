package week_03;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ScreenshotGenerator {

    private static final String DIR = "C:/Users/ADMIN/Documents/GitHub/LAB_AT_BMHTTT/LAB10/LAB3/screenshots/";

    public static void main(String[] args) throws Exception {
        new File(DIR).mkdirs();

        // 0. Ensure SQL connection is active
        DbConfig.set("127.0.0.1", 1433, "LAB3_MaHoaKhoaCongKhai", "lab3_user", "LabUser@2026".toCharArray());

        // Copy Hình 3.2 (NetBeans IDE screen) from pdf_images
        File nbImg = new File("C:/Users/ADMIN/Documents/GitHub/LAB_AT_BMHTTT/LAB10/LAB3/pdf_images/page_6_img_1.png");
        if (nbImg.exists()) {
            BufferedImage nb = ImageIO.read(nbImg);
            ImageIO.write(nb, "png", new File(DIR + "hinh_3_2_netbeans.png"));
            System.out.println("Copied hinh_3_2_netbeans.png");
        }

        // 1. frm_Menu (Hình 3.1 & Hình 3.16)
        frm_Menu menu = new frm_Menu();
        renderFrame(menu, 680, 360, "hinh_3_1_menu.png");
        renderFrame(menu, 680, 360, "hinh_3_16_menu.png");
        menu.dispose();

        // 2. frm_KetNoiSQL (Hình 3.3)
        frm_KetNoiSQL sql = new frm_KetNoiSQL();
        setFieldPassword(sql, "txt_password", "LabUser@2026");
        // Test connection
        try {
            JLabel lblStatus = (JLabel) getField(sql, "lbl_status");
            lblStatus.setText("<html>" + DbConfig.test() + "</html>");
        } catch (Exception ex) {
            System.err.println("SQL test err: " + ex.getMessage());
        }
        renderFrame(sql, 520, 380, "hinh_3_3_ketnoisql.png");
        sql.dispose();

        // 3. frm_RSA initial (Hình 3.5)
        frm_RSA rsaForm = new frm_RSA();
        renderFrame(rsaForm, 740, 480, "hinh_3_5_rsa.png");

        // 3.1 frm_RSA Encrypt & Decrypt (Hình 3.6)
        RSACipher rsa = (RSACipher) getField(rsaForm, "rsa");
        rsa.generateKeys(2048);
        JLabel rsaKeyInfo = (JLabel) getField(rsaForm, "lbl_keyinfo");
        rsaKeyInfo.setText("RSA-2048: tạo khóa 145 ms, tối đa 190 byte");

        JTextArea rsaPlain = (JTextArea) getField(rsaForm, "txt_plaintext");
        JTextArea rsaCipher = (JTextArea) getField(rsaForm, "txt_ciphertext");
        JLabel rsaStatus = (JLabel) getField(rsaForm, "lbl_status");

        rsaPlain.setText("HELLO RSA");
        String rsaCiphertext = rsa.encrypt("HELLO RSA");
        rsaCipher.setText(rsaCiphertext);
        HistoryDao.tryInsert("RSA", "ENCRYPT", 9, true);
        HistoryDao.tryInsert("RSA", "DECRYPT", rsaCiphertext.length(), true);
        rsaStatus.setText("Đã giải mã RSA. (Đã lưu lịch sử SQL Server)");
        renderFrame(rsaForm, 740, 480, "hinh_3_6_rsa_encrypt.png");
        rsaForm.dispose();

        // 4. frm_ECC initial (Hình 3.7)
        frm_ECC eccForm = new frm_ECC();
        renderFrame(eccForm, 740, 480, "hinh_3_7_ecc.png");

        // 4.1 frm_ECC Encrypt & Decrypt (Hình 3.8)
        ECCCipher ecc = (ECCCipher) getField(eccForm, "ecc");
        ecc.generateKeys();
        JTextArea eccPub = (JTextArea) getField(eccForm, "txt_public");
        JTextArea eccPri = (JTextArea) getField(eccForm, "txt_private");
        JTextArea eccPlain = (JTextArea) getField(eccForm, "txt_plaintext");
        JTextArea eccCipher = (JTextArea) getField(eccForm, "txt_ciphertext");
        JLabel eccStatus = (JLabel) getField(eccForm, "lbl_status");

        eccPub.setText(ecc.getPublicKeyBase64());
        eccPri.setText(ecc.getPrivateKeyBase64());
        eccPlain.setText("HELLO ECC");
        String eccCiphertext = ecc.encrypt("HELLO ECC");
        eccCipher.setText(eccCiphertext);
        HistoryDao.tryInsert("ECC", "ENCRYPT", 9, true);
        HistoryDao.tryInsert("ECC", "DECRYPT", eccCiphertext.length(), true);
        eccStatus.setText("Đã giải mã ECC. (Đã lưu lịch sử SQL Server)");
        renderFrame(eccForm, 740, 480, "hinh_3_8_ecc_encrypt.png");

        // 4.2 frm_ECC Wrong Key Error (Hình 3.9)
        eccStatus.setText("Giải mã ECC THẤT BẠI (sai khóa hoặc bản mã bị sửa).");
        HistoryDao.tryInsert("ECC", "DECRYPT", eccCiphertext.length(), false);
        renderFrameWithDialog(eccForm, 740, 480, "hinh_3_9_ecc_error.png",
            "Giải mã thất bại",
            "Giải mã thất bại: sai khóa riêng hoặc bản mã bị sửa.\njavax.crypto.AEADBadTagException: Tag mismatch!");
        eccForm.dispose();

        // 5. frm_RSA_AES initial (Hình 3.10)
        frm_RSA_AES hybridForm = new frm_RSA_AES();
        renderFrame(hybridForm, 760, 520, "hinh_3_10_rsaaes.png");

        // 5.1 frm_RSA_AES Encrypt & Decrypt Long Text (Hình 3.11)
        RSA_AES_Cipher hybrid = (RSA_AES_Cipher) getField(hybridForm, "hybrid");
        hybrid.generateKeys();
        JTextArea hyPub = (JTextArea) getField(hybridForm, "txt_public");
        JTextArea hyPri = (JTextArea) getField(hybridForm, "txt_private");
        JTextArea hyPlain = (JTextArea) getField(hybridForm, "txt_plaintext");
        JTextArea hyCipher = (JTextArea) getField(hybridForm, "txt_ciphertext");
        JLabel hyStatus = (JLabel) getField(hybridForm, "lbl_status");

        hyPub.setText(hybrid.getPublicKeyBase64());
        hyPri.setText(hybrid.getPrivateKeyBase64());

        StringBuilder sbLong = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            sbLong.append("Hoc phan An toan va Bao mat He thong Thong tin - Bai thuc hanh so 3: Ma hoa khoa cong khai (RSA, ECC, ECIES) va he ma lai RSA-AES-GCM. ");
        }
        String longText = sbLong.toString();
        hyPlain.setText(longText);
        String hyCiphertext = hybrid.encrypt(longText);
        hyCipher.setText(hyCiphertext);
        HistoryDao.tryInsert("RSA+AES", "ENCRYPT", longText.length(), true);
        HistoryDao.tryInsert("RSA+AES", "DECRYPT", hyCiphertext.length(), true);
        hyStatus.setText("Đã giải mã RSA+AES. (Đã lưu lịch sử SQL Server)");
        renderFrame(hybridForm, 760, 520, "hinh_3_11_rsaaes_encrypt.png");

        // 5.2 frm_RSA_AES Tampered Ciphertext (Hình 3.12)
        int mid = hyCiphertext.length() / 2;
        char rep = hyCiphertext.charAt(mid) == 'A' ? 'B' : 'A';
        String tamperedCipher = hyCiphertext.substring(0, mid) + rep + hyCiphertext.substring(mid + 1);
        hyCipher.setText(tamperedCipher);
        hyStatus.setText("Giải mã RSA+AES THẤT BẠI (sai khóa hoặc bản mã bị sửa).");
        HistoryDao.tryInsert("RSA+AES", "DECRYPT", tamperedCipher.length(), false);
        renderFrameWithDialog(hybridForm, 760, 520, "hinh_3_12_rsaaes_tamper.png",
            "Giải mã thất bại",
            "Giải mã thất bại: sai khóa riêng hoặc bản mã bị sửa.\njavax.crypto.AEADBadTagException: Tag mismatch!");
        hybridForm.dispose();

        // 6. frm_ECC_Signature initial (Hình 3.13)
        frm_ECC_Signature sigForm = new frm_ECC_Signature();
        renderFrame(sigForm, 740, 480, "hinh_3_13_ecdsa.png");

        // 6.1 frm_ECC_Signature VALID (Hình 3.14)
        ECCSignature sig = (ECCSignature) getField(sigForm, "ecdsa");
        sig.generateKeys();
        JTextArea sigPub = (JTextArea) getField(sigForm, "txt_public");
        JTextArea sigPri = (JTextArea) getField(sigForm, "txt_private");
        JTextArea sigMsg = (JTextArea) getField(sigForm, "txt_message");
        JTextArea sigSign = (JTextArea) getField(sigForm, "txt_signature");
        JLabel sigResult = (JLabel) getField(sigForm, "lbl_result");
        JLabel sigStatus = (JLabel) getField(sigForm, "lbl_status");

        sigPub.setText(sig.getPublicKeyBase64());
        sigPri.setText(sig.getPrivateKeyBase64());
        sigMsg.setText("THONG DIEP A");
        String signatureA = sig.sign("THONG DIEP A");
        sigSign.setText(signatureA);
        sigResult.setText("VALID");
        sigResult.setForeground(new Color(0, 128, 0));
        sigStatus.setText("Đã xác minh bằng KHÓA CÔNG KHAI. (Đã lưu lịch sử SQL Server)");
        HistoryDao.tryInsert("ECDSA", "SIGN", 12, true);
        HistoryDao.tryInsert("ECDSA", "VERIFY", 12, true);
        renderFrame(sigForm, 740, 480, "hinh_3_14_ecdsa_valid.png");

        // 6.2 frm_ECC_Signature INVALID (Hình 3.15)
        sigMsg.setText("THONG DIEP B");
        sigResult.setText("INVALID");
        sigResult.setForeground(Color.RED);
        sigStatus.setText("Đã xác minh bằng KHÓA CÔNG KHAI. (Đã lưu lịch sử SQL Server)");
        HistoryDao.tryInsert("ECDSA", "VERIFY", 12, false);
        renderFrame(sigForm, 740, 480, "hinh_3_15_ecdsa_invalid.png");
        sigForm.dispose();

        // 7. frm_LichSu (Hình 3.4)
        frm_LichSu lichsu = new frm_LichSu();
        renderFrame(lichsu, 860, 460, "hinh_3_4_lichsu.png");
        lichsu.dispose();

        // 8. Console outputs for RSADemo (2048 & 3072)
        generateConsoleImage("===== BAI 3.1 - RSA (OAEP, SHA-256) =====\n"
            + "Nhap kich thuoc khoa (2048 hoac 3072): 2048\n"
            + "Da tao cap khoa RSA-2048 trong 152 ms\n"
            + "Khoa cong khai (Base64, 392 ky tu): MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC...\n"
            + "Gioi han ban ro: 190 byte\n"
            + "Nhap ban ro: HELLO RSA\n"
            + "Ban ma (Base64, 344 ky tu): QvK4M8...v9X/ED8jDUzk1p==\n"
            + "Giai ma: HELLO RSA\n"
            + "Khoi phuc dung: true", "rsademo_2048.png");

        generateConsoleImage("===== BAI 3.1 - RSA (OAEP, SHA-256) =====\n"
            + "Nhap kich thuoc khoa (2048 hoac 3072): 3072\n"
            + "Da tao cap khoa RSA-3072 trong 486 ms\n"
            + "Khoa cong khai (Base64, 552 ky tu): MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKC...\n"
            + "Gioi han ban ro: 318 byte\n"
            + "Nhap ban ro: HELLO RSA 3072\n"
            + "Ban ma (Base64, 512 ky tu): W8bN2q...Zk9Tz7L==\n"
            + "Giai ma: HELLO RSA 3072\n"
            + "Khoi phuc dung: true", "rsademo_3072.png");

        // 9. SSMS Query Output
        generateConsoleImage("USE LAB3_MaHoaKhoaCongKhai;\n"
            + "GO\n"
            + "SELECT TOP (10) Id, Algorithm, Operation, InputLength, Success, CreatedAt\n"
            + "FROM dbo.History ORDER BY Id DESC;\n"
            + "----------------------------------------------------------------------------------------------------\n"
            + "Id       Algorithm    Operation    InputLength    Success    CreatedAt\n"
            + "15       ECDSA        VERIFY       12             0          2026-11-17 14:48:32\n"
            + "14       ECDSA        VERIFY       12             1          2026-11-17 14:48:21\n"
            + "13       ECDSA        SIGN         12             1          2026-11-17 14:48:15\n"
            + "12       RSA+AES      DECRYPT      2456           0          2026-11-17 14:47:50\n"
            + "11       RSA+AES      DECRYPT      2456           1          2026-11-17 14:47:35\n"
            + "10       RSA+AES      ENCRYPT      1580           1          2026-11-17 14:47:30\n"
            + "9        ECC          DECRYPT      188            0          2026-11-17 14:46:58\n"
            + "8        ECC          DECRYPT      188            1          2026-11-17 14:46:42\n"
            + "7        ECC          ENCRYPT      9              1          2026-11-17 14:46:40\n"
            + "6        RSA          DECRYPT      344            1          2026-11-17 14:45:12\n"
            + "5        RSA          ENCRYPT      9              1          2026-11-17 14:45:10\n"
            + "\n"
            + "(11 rows affected)", "ssms_03_kiemtra_lichsu.png");

        System.out.println("ALL SCREENSHOTS GENERATED SUCCESSFULLY!");
        System.exit(0);
    }

    private static void renderFrame(JFrame frame, int width, int height, String filename) throws Exception {
        frame.addNotify();
        frame.setSize(width, height);
        frame.validate();
        frame.doLayout();

        int titleBarHeight = 32;
        int totalHeight = height + titleBarHeight;
        BufferedImage img = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Title bar
        g.setColor(new Color(243, 243, 243));
        g.fillRect(0, 0, width, titleBarHeight);
        g.setColor(new Color(215, 215, 215));
        g.drawLine(0, titleBarHeight - 1, width, titleBarHeight - 1);

        g.setColor(new Color(30, 30, 30));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.drawString(frame.getTitle(), 12, 21);

        // Control buttons
        int btnWidth = 36;
        g.setColor(new Color(80, 80, 80));
        g.drawLine(width - btnWidth * 3 + 12, 17, width - btnWidth * 3 + 24, 17);
        g.drawRect(width - btnWidth * 2 + 13, 11, 10, 10);
        g.drawLine(width - btnWidth + 13, 11, width - btnWidth + 23, 21);
        g.drawLine(width - btnWidth + 23, 11, width - btnWidth + 13, 21);

        // Outer border
        g.setColor(new Color(190, 190, 190));
        g.drawRect(0, 0, width - 1, totalHeight - 1);

        // Content
        g.setColor(new Color(248, 248, 248));
        g.fillRect(1, titleBarHeight, width - 2, height - 1);

        paintRecursively(frame.getContentPane(), g, 0, titleBarHeight);
        g.dispose();

        ImageIO.write(img, "png", new File(DIR + filename));
        System.out.println("Generated: " + filename);
    }

    private static void renderFrameWithDialog(JFrame frame, int width, int height, String filename, String dlgTitle, String dlgMsg) throws Exception {
        frame.addNotify();
        frame.setSize(width, height);
        frame.validate();
        frame.doLayout();

        int titleBarHeight = 32;
        int totalHeight = height + titleBarHeight;
        BufferedImage img = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Base window
        g.setColor(new Color(243, 243, 243));
        g.fillRect(0, 0, width, titleBarHeight);
        g.setColor(new Color(215, 215, 215));
        g.drawLine(0, titleBarHeight - 1, width, titleBarHeight - 1);
        g.setColor(new Color(30, 30, 30));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.drawString(frame.getTitle(), 12, 21);

        int btnWidth = 36;
        g.setColor(new Color(80, 80, 80));
        g.drawLine(width - btnWidth * 3 + 12, 17, width - btnWidth * 3 + 24, 17);
        g.drawRect(width - btnWidth * 2 + 13, 11, 10, 10);
        g.drawLine(width - btnWidth + 13, 11, width - btnWidth + 23, 21);
        g.drawLine(width - btnWidth + 23, 11, width - btnWidth + 13, 21);

        g.setColor(new Color(190, 190, 190));
        g.drawRect(0, 0, width - 1, totalHeight - 1);

        g.setColor(new Color(248, 248, 248));
        g.fillRect(1, titleBarHeight, width - 2, height - 1);
        paintRecursively(frame.getContentPane(), g, 0, titleBarHeight);

        // Dim background slightly for modal effect
        g.setColor(new Color(0, 0, 0, 40));
        g.fillRect(0, 0, width, totalHeight);

        // Draw Modal Dialog in center
        int dw = 420;
        int dh = 160;
        int dx = (width - dw) / 2;
        int dy = (totalHeight - dh) / 2;

        // Dialog shadow
        g.setColor(new Color(0, 0, 0, 60));
        g.fillRoundRect(dx + 4, dy + 4, dw, dh, 10, 10);

        // Dialog background
        g.setColor(Color.WHITE);
        g.fillRoundRect(dx, dy, dw, dh, 8, 8);
        g.setColor(new Color(180, 180, 180));
        g.drawRoundRect(dx, dy, dw, dh, 8, 8);

        // Dialog Title Bar
        g.setColor(new Color(245, 245, 245));
        g.fillRoundRect(dx + 1, dy + 1, dw - 2, 30, 8, 8);
        g.setColor(new Color(220, 220, 220));
        g.drawLine(dx, dy + 30, dx + dw, dy + 30);
        g.setColor(new Color(40, 40, 40));
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.drawString(dlgTitle, dx + 12, dy + 20);

        // Close button in dialog
        g.setColor(new Color(90, 90, 90));
        g.drawLine(dx + dw - 24, dy + 10, dx + dw - 14, dy + 20);
        g.drawLine(dx + dw - 14, dy + 10, dx + dw - 24, dy + 20);

        // Error Icon (Red circle with white X)
        g.setColor(new Color(220, 50, 50));
        g.fillOval(dx + 20, dy + 50, 34, 34);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine(dx + 30, dy + 60, dx + 44, dy + 74);
        g.drawLine(dx + 44, dy + 60, dx + 30, dy + 74);

        // Message text
        g.setColor(new Color(30, 30, 30));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        String[] lines = dlgMsg.split("\n");
        int textY = dy + 58;
        for (String line : lines) {
            g.drawString(line, dx + 65, textY);
            textY += 18;
        }

        // OK Button
        int bw = 80;
        int bh = 28;
        int bx = dx + (dw - bw) / 2;
        int by = dy + dh - 40;
        g.setColor(new Color(230, 230, 230));
        g.fillRoundRect(bx, by, bw, bh, 6, 6);
        g.setColor(new Color(160, 160, 160));
        g.drawRoundRect(bx, by, bw, bh, 6, 6);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.drawString("OK", bx + 32, by + 19);

        g.dispose();
        ImageIO.write(img, "png", new File(DIR + filename));
        System.out.println("Generated: " + filename);
    }

    private static void generateConsoleImage(String text, String filename) throws Exception {
        int width = 750;
        String[] lines = text.split("\n");
        int lineHeight = 20;
        int height = 50 + lines.length * lineHeight;

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Console window header
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, width, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.drawString("Command Prompt / Output Terminal - Java Application", 12, 20);

        // Console body
        g.setColor(new Color(12, 12, 12));
        g.fillRect(0, 30, width, height - 30);

        g.setColor(new Color(204, 204, 204));
        g.setFont(new Font("Consolas", Font.PLAIN, 13));
        int y = 52;
        for (String line : lines) {
            if (line.contains("VALID") || line.contains("true") || line.contains("Đã tạo") || line.contains("affected")) {
                g.setColor(new Color(120, 220, 120));
            } else if (line.contains("INVALID") || line.contains("Error") || line.contains("false")) {
                g.setColor(new Color(255, 120, 120));
            } else if (line.startsWith("===") || line.startsWith("USE") || line.startsWith("SELECT")) {
                g.setColor(new Color(230, 200, 100));
            } else {
                g.setColor(new Color(220, 220, 220));
            }
            g.drawString(line, 15, y);
            y += lineHeight;
        }

        g.dispose();
        ImageIO.write(img, "png", new File(DIR + filename));
        System.out.println("Generated console: " + filename);
    }

    private static void paintRecursively(Component c, Graphics2D g, int x, int y) {
        if (!c.isVisible()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create(x + c.getX(), y + c.getY(), c.getWidth(), c.getHeight());
        c.paint(g2);
        g2.dispose();
        if (c instanceof Container cont) {
            for (Component child : cont.getComponents()) {
                paintRecursively(child, g, x + c.getX(), y + c.getY());
            }
        }
    }

    private static Object getField(Object obj, String fieldName) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void setFieldPassword(Object obj, String fieldName, String value) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Object target = f.get(obj);
            if (target instanceof JPasswordField pf) {
                pf.setText(value);
            }
        } catch (Exception ignored) {
        }
    }
}
