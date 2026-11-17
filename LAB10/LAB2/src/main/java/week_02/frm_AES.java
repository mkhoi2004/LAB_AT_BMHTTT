package week_02;

import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class frm_AES extends CipherFormBase {

    private final JTextField txt_username = new JTextField(10);
    private final JPasswordField txt_password = new JPasswordField(10);
    private final JTextField txt_ticket = new JTextField(14);
    private final JButton btn_register = new JButton("Register");
    private final JButton btn_login = new JButton("Login");
    private final JButton btn_sql = new JButton("SQL Connection");
    private String currentUser;

    public frm_AES() {
        super("AES-GCM Cipher", "AES encryption password", "");
        txt_ticket.setEditable(false);
        optionsPanel.add(new JLabel("Username:"));
        optionsPanel.add(txt_username);
        optionsPanel.add(new JLabel("Account password:"));
        optionsPanel.add(txt_password);
        optionsPanel.add(btn_register);
        optionsPanel.add(btn_login);
        optionsPanel.add(new JLabel("Ticket:"));
        optionsPanel.add(txt_ticket);
        optionsPanel.add(btn_sql);

        btn_register.addActionListener(event -> register());
        btn_login.addActionListener(event -> login());
        btn_sql.addActionListener(event -> new frm_KetNoiSQL().setVisible(true));
        relayout();
    }

    @Override
    protected String encryptText(String plaintext, String key) {
        return AESCipher.encrypt(plaintext, key);
    }

    @Override
    protected String decryptText(String ciphertext, String key) {
        return AESCipher.decrypt(ciphertext, key);
    }

    @Override
    protected String algorithmName() {
        return "AES-GCM";
    }

    @Override
    protected String historyUsername() {
        return currentUser;
    }

    @Override
    protected void encryptAction() {
        if (currentUser == null || txt_ticket.getText().isBlank()) {
            setStatus("Login trước khi sử dụng AES.", false);
            return;
        }
        super.encryptAction();
    }

    @Override
    protected void decryptAction() {
        if (currentUser == null || txt_ticket.getText().isBlank()) {
            setStatus("Login trước khi sử dụng AES.", false);
            return;
        }
        super.decryptAction();
    }

    private void register() {
        char[] password = txt_password.getPassword();
        try {
            String username = txt_username.getText().trim();
            UserDao.register(username, password);
            String history = HistoryDao.tryInsert("AES-GCM", "REGISTER", username, 0, 0);
            setStatus("Đăng ký thành công. " + history, true);
        } catch (IllegalArgumentException | IllegalStateException | SQLException ex) {
            setStatus(ex.getMessage(), false);
        } finally {
            Arrays.fill(password, '\0');
            txt_password.setText("");
        }
    }

    private void login() {
        char[] password = txt_password.getPassword();
        try {
            String username = txt_username.getText().trim();
            if (!UserDao.login(username, password)) {
                clearLogin();
                setStatus("Username hoặc password không chính xác.", false);
                return;
            }
            currentUser = username;
            txt_ticket.setText(UserDao.newTicket());
            String history = HistoryDao.tryInsert("AES-GCM", "LOGIN", currentUser, 0, 0);
            setStatus("Login thành công. " + history, true);
        } catch (IllegalArgumentException | IllegalStateException | SQLException ex) {
            clearLogin();
            setStatus(ex.getMessage(), false);
        } finally {
            Arrays.fill(password, '\0');
            txt_password.setText("");
        }
    }

    private void clearLogin() {
        currentUser = null;
        txt_ticket.setText("");
    }
}
