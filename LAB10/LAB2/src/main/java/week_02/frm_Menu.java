package week_02;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class frm_Menu extends JFrame {

    public final JButton btn_des = new JButton("DES");
    public final JButton btn_tripledes = new JButton("TripleDES");
    public final JButton btn_aes = new JButton("AES (Login required)");
    public final JButton btn_rc4 = new JButton("RC4");
    public final JButton btn_twofish = new JButton("Twofish");
    public final JButton btn_aesctr = new JButton("AES-CTR");
    public final JButton btn_sql = new JButton("SQL Server Connection");
    public final JButton btn_history = new JButton("Operation History");

    public frm_Menu() {
        super("LAB 2 - Modern Symmetric Ciphers");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel heading = new JLabel("LAB 2 - Modern Symmetric Ciphers", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setBorder(BorderFactory.createEmptyBorder(22, 12, 20, 12));

        JPanel buttons = new JPanel(new GridLayout(0, 2, 12, 12));
        buttons.setBorder(BorderFactory.createEmptyBorder(12, 24, 24, 24));
        buttons.add(btn_des);
        buttons.add(btn_tripledes);
        buttons.add(btn_aes);
        buttons.add(btn_rc4);
        buttons.add(btn_twofish);
        buttons.add(btn_aesctr);
        buttons.add(btn_sql);
        buttons.add(btn_history);

        add(heading, BorderLayout.NORTH);
        add(buttons, BorderLayout.CENTER);

        btn_des.addActionListener(event -> new frm_DES().setVisible(true));
        btn_tripledes.addActionListener(event -> new frm_TripleDES().setVisible(true));
        btn_aes.addActionListener(event -> new frm_AES().setVisible(true));
        btn_rc4.addActionListener(event -> new frm_RC4().setVisible(true));
        btn_twofish.addActionListener(event -> new frm_Twofish().setVisible(true));
        btn_aesctr.addActionListener(event -> new frm_AES_CTR().setVisible(true));
        btn_sql.addActionListener(event -> new frm_KetNoiSQL().setVisible(true));
        btn_history.addActionListener(event -> new frm_LichSu().setVisible(true));

        setSize(720, 440);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new frm_Menu().setVisible(true));
    }
}
