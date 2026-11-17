package week_02;

import java.awt.Window;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormTest {

    @Test
    void menuButtonsOpenTheirMatchingForms() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            frm_Menu menu = new frm_Menu();
            JButton[] buttons = {
                menu.btn_des, menu.btn_tripledes, menu.btn_aes, menu.btn_rc4,
                menu.btn_twofish, menu.btn_aesctr, menu.btn_sql, menu.btn_history
            };
            Class<?>[] forms = {
                frm_DES.class, frm_TripleDES.class, frm_AES.class, frm_RC4.class,
                frm_Twofish.class, frm_AES_CTR.class, frm_KetNoiSQL.class, frm_LichSu.class
            };

            for (int i = 0; i < buttons.length; i++) {
                buttons[i].doClick();
                Class<?> expected = forms[i];
                assertTrue(Arrays.stream(Window.getWindows())
                        .anyMatch(window -> expected.isInstance(window) && window.isVisible()),
                        expected.getSimpleName() + " should open from its menu button");
            }
            Arrays.stream(Window.getWindows()).forEach(Window::dispose);
        });
    }

    @Test
    void nonMenuFormsDisposeAndMenuExits() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            javax.swing.JFrame[] forms = {
                new frm_Menu(), new frm_DES(), new frm_TripleDES(), new frm_AES(), new frm_RC4(),
                new frm_Twofish(), new frm_AES_CTR(), new frm_KetNoiSQL(), new frm_LichSu()
            };
            assertEquals(javax.swing.JFrame.EXIT_ON_CLOSE, forms[0].getDefaultCloseOperation());
            for (int i = 1; i < forms.length; i++) {
                assertEquals(javax.swing.JFrame.DISPOSE_ON_CLOSE, forms[i].getDefaultCloseOperation(),
                        forms[i].getClass().getSimpleName());
            }
            Arrays.stream(forms).forEach(javax.swing.JFrame::dispose);
        });
    }

    @Test
    void aesEncryptIsBlockedUntilLogin() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            frm_AES form = new frm_AES();
            form.txt_plaintext.setText("Xin chào Lab 2 - Mã hóa hiện đại!");
            form.btn_encrypt.doClick();

            assertTrue(form.lbl_status.getText().contains("Login"));
            assertEquals("", form.txt_ciphertext.getText());
            form.dispose();
        });
    }

    @Test
    void historyModelIsReadOnly() {
        assertFalse(HistoryDao.emptyModel().isCellEditable(0, 0));
    }
}
