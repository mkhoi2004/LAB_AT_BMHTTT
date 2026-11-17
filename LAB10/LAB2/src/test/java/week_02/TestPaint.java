package week_02;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;

public class TestPaint {
    public static void main(String[] args) throws Exception {
        frm_Menu menu = new frm_Menu();
        menu.addNotify();
        menu.setSize(720, 440);
        menu.validate();
        menu.doLayout();

        BufferedImage img = new BufferedImage(720, 440, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(245, 245, 245));
        g.fillRect(0, 0, 720, 440);
        
        paintRecursively(menu.getContentPane(), g, 0, 0);
        g.dispose();

        File out = new File("C:/Users/ADMIN/Documents/GitHub/LAB_AT_BMHTTT/LAB10/LAB2/screenshots/test_menu.png");
        ImageIO.write(img, "png", out);
        System.out.println("Done: " + out.length() + " bytes");
    }

    private static void paintRecursively(Component c, Graphics2D g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create(x + c.getX(), y + c.getY(), c.getWidth(), c.getHeight());
        c.paint(g2);
        g2.dispose();
        if (c instanceof Container cont) {
            for (Component child : cont.getComponents()) {
                paintRecursively(child, g, x + c.getX(), y + c.getY());
            }
        }
    }
}
