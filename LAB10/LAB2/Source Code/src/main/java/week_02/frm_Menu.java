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

    public final JButton btn_des = new JButton("2.1 DES");
    public final JButton btn_tripledes = new JButton("2.2 TripleDES");
    public final JButton btn_aes = new JButton("2.3 AES + Login");
    public final JButton btn_rc4 = new JButton("2.4 RC4");
    public final JButton btn_twofish = new JButton("2.5 Twofish");
    public final JButton btn_aesctr = new JButton("2.6 Nâng cao - AES-CTR");
    public final JButton btn_sql = new JButton("Kết nối SQL Server");
    public final JButton btn_history = new JButton("Xem lịch sử");

    public frm_Menu() {
        super("LAB 2 - Mã hóa đối xứng hiện đại");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel heading = new JLabel("LAB 2 - MÃ HÓA ĐỐI XỨNG HIỆN ĐẠI", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setBorder(BorderFactory.createEmptyBorder(18, 12, 10, 12));

        JLabel subHeading = new JLabel("Chọn một bài để mở form tương ứng:", SwingConstants.CENTER);
        subHeading.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subHeading.setBorder(BorderFactory.createEmptyBorder(0, 12, 10, 12));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(heading, BorderLayout.NORTH);
        topPanel.add(subHeading, BorderLayout.SOUTH);

        JPanel buttons = new JPanel(new GridLayout(0, 2, 12, 12));
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 24, 16, 24));
        buttons.add(btn_des);
        buttons.add(btn_tripledes);
        buttons.add(btn_aes);
        buttons.add(btn_rc4);
        buttons.add(btn_twofish);
        buttons.add(btn_aesctr);
        buttons.add(btn_sql);
        buttons.add(btn_history);

        JLabel noteLabel = new JLabel("Bài nâng cao 02 (AES-CBC) viết bằng Python: chạy tệp aes_cbc_gui.py.", SwingConstants.CENTER);
        noteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        noteLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 14, 12));

        add(topPanel, BorderLayout.NORTH);
        add(buttons, BorderLayout.CENTER);
        add(noteLabel, BorderLayout.SOUTH);

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
