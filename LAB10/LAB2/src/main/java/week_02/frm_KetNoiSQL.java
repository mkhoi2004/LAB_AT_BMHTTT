package week_02;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class frm_KetNoiSQL extends JFrame {

    private final JTextField txt_server = new JTextField("127.0.0.1", 22);
    private final JTextField txt_port = new JTextField("14333", 22);
    private final JTextField txt_database = new JTextField("LAB2_MaHoaHienDai", 22);
    private final JTextField txt_username = new JTextField("lab2_user", 22);
    private final JPasswordField txt_password = new JPasswordField(22);
    private final JButton btn_test = new JButton("Test Connection");
    private final JButton btn_history = new JButton("Open History");
    private final JLabel lbl_status = new JLabel("Nhập thông tin SQL Server để kiểm tra kết nối.");

    public frm_KetNoiSQL() {
        super("SQL Server Connection");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBorder(BorderFactory.createEmptyBorder(18, 20, 12, 20));
        addField(fields, 0, "Server", txt_server);
        addField(fields, 1, "Port", txt_port);
        addField(fields, 2, "Database", txt_database);
        addField(fields, 3, "Username", txt_username);
        addField(fields, 4, "Password", txt_password);

        JPanel buttons = new JPanel();
        buttons.add(btn_test);
        buttons.add(btn_history);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(buttons, BorderLayout.NORTH);
        lbl_status.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        bottom.add(lbl_status, BorderLayout.SOUTH);

        add(fields, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        btn_test.addActionListener(event -> testConnection());
        btn_history.addActionListener(event -> new frm_LichSu().setVisible(true));
        pack();
        setLocationRelativeTo(null);
    }

    private void testConnection() {
        char[] password = txt_password.getPassword();
        try {
            int port = Integer.parseInt(txt_port.getText().trim());
            DbConfig.set(txt_server.getText(), port, txt_database.getText(),
                    txt_username.getText(), password);
            lbl_status.setText(DbConfig.test());
        } catch (IllegalArgumentException | SQLException ex) {
            lbl_status.setText("Kết nối thất bại: " + ex.getMessage());
        } finally {
            Arrays.fill(password, '\0');
            txt_password.setText("");
        }
    }

    private void addField(JPanel panel, int row, String label, java.awt.Component field) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.insets = new Insets(6, 6, 6, 12);
        panel.add(new JLabel(label + ":"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(6, 6, 6, 6);
        panel.add(field, constraints);
    }
}
