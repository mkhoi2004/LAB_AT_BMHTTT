package week_02;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ScreenshotGenerator {

    private static final String DIR = "C:/Users/ADMIN/Documents/GitHub/LAB_AT_BMHTTT/LAB10/LAB2/screenshots/";

    public static void main(String[] args) throws Exception {
        new File(DIR).mkdirs();
        DbConfig.set("127.0.0.1", 1433, "LAB2_MaHoaHienDai", "lab2_user", "LabUser@2026".toCharArray());

        // 1. frm_Menu
        frm_Menu menu = new frm_Menu();
        renderFrame(menu, 720, 440, "hinh_2_1_menu.png");
        renderFrame(menu, 720, 440, "hinh_2_19_menu.png");
        menu.dispose();

        // 2. frm_KetNoiSQL
        frm_KetNoiSQL sql = new frm_KetNoiSQL();
        findButton(sql, "Test Connection").doClick();
        renderFrame(sql, 480, 360, "hinh_2_3_ketnoisql.png");
        sql.dispose();

        // 3. frm_DES
        frm_DES des = new frm_DES();
        renderFrame(des, 760, 500, "hinh_2_5_des.png");

        des.txt_plaintext.setText("Xin chào Lab 2 - Mã hóa hiện đại!");
        des.txt_key.setText("12345678");
        des.btn_encrypt.doClick();
        renderFrame(des, 760, 500, "hinh_2_6_des_encrypt.png");

        // Error with wrong key
        des.txt_key.setText("87654321");
        des.btn_decrypt.doClick();
        renderFrame(des, 760, 500, "hinh_2_7_des_error.png");
        des.dispose();

        // 4. frm_TripleDES
        frm_TripleDES tdes = new frm_TripleDES();
        renderFrame(tdes, 760, 500, "hinh_2_8_tripledes.png");

        tdes.txt_plaintext.setText("Xin chào Lab 2 - Mã hóa hiện đại!");
        tdes.txt_key.setText("123456789012345678901234");
        tdes.btn_encrypt.doClick();
        renderFrame(tdes, 760, 500, "hinh_2_9_tripledes_encrypt.png");
        tdes.dispose();

        // 5. frm_AES
        frm_AES aes = new frm_AES();
        setField(aes, "txt_username", "sinhvien01");
        renderFrame(aes, 960, 520, "hinh_2_10_aes.png");

        setFieldPassword(aes, "txt_password", "SinhVien@2026");
        findButton(aes, "Register").doClick();
        setFieldPassword(aes, "txt_password", "SinhVien@2026");
        findButton(aes, "Login").doClick();
        aes.txt_plaintext.setText("Xin chào Lab 2 - Mã hóa hiện đại!");
        aes.txt_key.setText("MatKhauAES@2026");
        aes.btn_encrypt.doClick();
        renderFrame(aes, 960, 520, "hinh_2_11_aes_encrypt.png");
        aes.dispose();

        // 6. frm_RC4
        frm_RC4 rc4 = new frm_RC4();
        renderFrame(rc4, 760, 500, "hinh_2_12_rc4.png");

        rc4.txt_plaintext.setText("Plaintext");
        rc4.txt_key.setText("Key");
        rc4.btn_encrypt.doClick();
        renderFrame(rc4, 760, 500, "hinh_2_13_rc4_encrypt.png");
        rc4.dispose();

        // 7. frm_Twofish
        frm_Twofish twofish = new frm_Twofish();
        renderFrame(twofish, 760, 500, "hinh_2_14_twofish.png");

        findButton(twofish, "Generate IV").doClick();
        twofish.txt_plaintext.setText("Xin chào Lab 2 - Mã hóa hiện đại!");
        twofish.txt_key.setText("Twofish@Lab2");
        twofish.btn_encrypt.doClick();
        renderFrame(twofish, 760, 500, "hinh_2_15_twofish_encrypt.png");
        twofish.dispose();

        // 8. frm_AES_CTR
        frm_AES_CTR aesctr = new frm_AES_CTR();
        renderFrame(aesctr, 760, 500, "hinh_2_16_aesctr.png");

        findButton(aesctr, "Generate Key").doClick();
        findButton(aesctr, "Generate Nonce").doClick();
        aesctr.txt_plaintext.setText("Xin chào Lab 2 - Mã hóa hiện đại!");
        aesctr.btn_encrypt.doClick();
        renderFrame(aesctr, 760, 500, "hinh_2_17_aesctr_encrypt.png");
        aesctr.dispose();

        // 9. frm_LichSu
        frm_LichSu lichsu = new frm_LichSu();
        findButton(lichsu, "Tải lại").doClick();
        renderFrame(lichsu, 920, 460, "hinh_2_4_lichsu.png");
        lichsu.dispose();

        System.out.println("ALL JAVA SCREENSHOTS GENERATED SUCCESSFULLY!");
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

        // Windows 11 style title bar
        g.setColor(new Color(243, 243, 243));
        g.fillRect(0, 0, width, titleBarHeight);
        g.setColor(new Color(210, 210, 210));
        g.drawLine(0, titleBarHeight - 1, width, titleBarHeight - 1);

        // Window Title
        g.setColor(new Color(30, 30, 30));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.drawString(frame.getTitle(), 12, 21);

        // Window control buttons: [-] [□] [✕]
        int btnWidth = 36;
        // Minimize
        g.setColor(new Color(80, 80, 80));
        g.drawLine(width - btnWidth * 3 + 12, 17, width - btnWidth * 3 + 24, 17);
        // Maximize
        g.drawRect(width - btnWidth * 2 + 13, 11, 10, 10);
        // Close
        g.drawLine(width - btnWidth + 13, 11, width - btnWidth + 23, 21);
        g.drawLine(width - btnWidth + 23, 11, width - btnWidth + 13, 21);

        // Window Border
        g.setColor(new Color(180, 180, 180));
        g.drawRect(0, 0, width - 1, totalHeight - 1);

        // Content
        g.setColor(new Color(248, 248, 248));
        g.fillRect(1, titleBarHeight, width - 2, height - 1);

        paintRecursively(frame.getContentPane(), g, 0, titleBarHeight);
        g.dispose();

        File out = new File(DIR + filename);
        ImageIO.write(img, "png", out);
        System.out.println("Generated: " + filename + " (" + width + "x" + totalHeight + ", " + out.length() + " bytes)");
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

    private static JButton findButton(JFrame frame, String text) {
        return findButtonInComponent(frame, text);
    }

    private static JButton findButtonInComponent(Component comp, String text) {
        if (comp instanceof JButton btn && text.equals(btn.getText())) {
            return btn;
        }
        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                JButton found = findButtonInComponent(child, text);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static void setField(Object obj, String fieldName, String value) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Object target = f.get(obj);
            if (target instanceof JTextField tf) {
                tf.setText(value);
            }
        } catch (Exception ignored) {
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
