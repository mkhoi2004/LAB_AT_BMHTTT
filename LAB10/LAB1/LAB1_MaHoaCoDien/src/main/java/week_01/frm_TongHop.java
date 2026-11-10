package week_01;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class frm_TongHop extends javax.swing.JFrame {

    private javax.swing.JButton btn_decrypt;
    private javax.swing.JButton btn_encrypt;
    private javax.swing.JButton btn_history;
    private javax.swing.JButton btn_open;
    private javax.swing.JButton btn_save;
    private javax.swing.JButton btn_sql;
    private javax.swing.JComboBox<String> cbo_algorithm;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl_hint;
    private javax.swing.JLabel lbl_status;
    private javax.swing.JTextArea txt_ciphertext;
    private javax.swing.JTextField txt_key;
    private javax.swing.JTextArea txt_plaintext;

    public frm_TongHop() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cbo_algorithm = new javax.swing.JComboBox<>();
        lbl_hint = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_plaintext = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        txt_key = new javax.swing.JTextField();
        JLabel jLabel5 = new javax.swing.JLabel("Ciphertext:");
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_ciphertext = new javax.swing.JTextArea();
        btn_encrypt = new javax.swing.JButton();
        btn_decrypt = new javax.swing.JButton();
        btn_open = new javax.swing.JButton();
        btn_save = new javax.swing.JButton();
        btn_sql = new javax.swing.JButton();
        btn_history = new javax.swing.JButton();
        lbl_status = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Chương trình tổng hợp - 6 thuật toán");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CHƯƠNG TRÌNH TỔNG HỢP 6 THUẬT TOÁN CỔ ĐIỂN");

        jLabel2.setText("Thuật toán:");

        cbo_algorithm.setModel(new javax.swing.DefaultComboBoxModel<>(CipherTongHop.ALGORITHMS));
        cbo_algorithm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbo_algorithmActionPerformed(evt);
            }
        });

        lbl_hint.setText("Khóa mẫu: 3");

        jLabel3.setText("Plaintext:");

        txt_plaintext.setColumns(20);
        txt_plaintext.setLineWrap(true);
        txt_plaintext.setRows(3);
        jScrollPane1.setViewportView(txt_plaintext);

        jLabel4.setText("Key:");

        txt_key.setText("3");

        txt_ciphertext.setColumns(20);
        txt_ciphertext.setLineWrap(true);
        txt_ciphertext.setRows(3);
        jScrollPane2.setViewportView(txt_ciphertext);

        btn_encrypt.setText("Encrypt");
        btn_encrypt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_encryptActionPerformed(evt);
            }
        });

        btn_decrypt.setText("Decrypt");
        btn_decrypt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_decryptActionPerformed(evt);
            }
        });

        btn_open.setText("Open File");
        btn_open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_openActionPerformed(evt);
            }
        });

        btn_save.setText("Save File");
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });

        btn_sql.setText("Kết nối SQL Server");
        btn_sql.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_sqlActionPerformed(evt);
            }
        });

        btn_history.setText("Xem lịch sử");
        btn_history.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_historyActionPerformed(evt);
            }
        });

        lbl_status.setText("Sẵn sàng.");

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        mainPanel.add(jLabel1, gbc);

        // Algorithm row
        JPanel algoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        algoPanel.add(jLabel2);
        algoPanel.add(cbo_algorithm);
        algoPanel.add(lbl_hint);
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        mainPanel.add(algoPanel, gbc);

        // Plaintext label
        gbc.gridy = 2;
        mainPanel.add(jLabel3, gbc);

        // Plaintext text area
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        mainPanel.add(jScrollPane1, gbc);

        // Key row
        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        keyPanel.add(jLabel4);
        txt_key.setPreferredSize(new Dimension(150, 28));
        keyPanel.add(txt_key);
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        mainPanel.add(keyPanel, gbc);

        // Ciphertext label
        gbc.gridy = 5;
        mainPanel.add(jLabel5, gbc);

        // Ciphertext text area
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.4;
        mainPanel.add(jScrollPane2, gbc);

        // Buttons row
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnPanel.add(btn_encrypt);
        btnPanel.add(btn_decrypt);
        btnPanel.add(btn_open);
        btnPanel.add(btn_save);
        btnPanel.add(btn_sql);
        btnPanel.add(btn_history);
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        mainPanel.add(btnPanel, gbc);

        // Status
        gbc.gridy = 8;
        mainPanel.add(lbl_status, gbc);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        pack();
        setSize(700, 480);
    }

    private void cbo_algorithmActionPerformed(java.awt.event.ActionEvent evt) {
        String algorithm = (String) cbo_algorithm.getSelectedItem();
        txt_key.setText(CipherTongHop.sampleKey(algorithm));
        lbl_hint.setText("Khóa mẫu: " + CipherTongHop.sampleKey(algorithm));
    }

    private void btn_encryptActionPerformed(java.awt.event.ActionEvent evt) {
        String algorithm = (String) cbo_algorithm.getSelectedItem();
        String input = txt_plaintext.getText();
        try {
            String result = CipherTongHop.encrypt(algorithm, input, txt_key.getText());
            txt_ciphertext.setText(result);
            String saved = HistoryDao.tryInsert(algorithm, "ENCRYPT", input, result);
            lbl_status.setText("Đã mã hóa " + algorithm + ". " + saved);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dữ liệu không hợp lệ", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btn_decryptActionPerformed(java.awt.event.ActionEvent evt) {
        String algorithm = (String) cbo_algorithm.getSelectedItem();
        String input = txt_ciphertext.getText();
        try {
            String result = CipherTongHop.decrypt(algorithm, input, txt_key.getText());
            txt_plaintext.setText(result);
            String saved = HistoryDao.tryInsert(algorithm, "DECRYPT", input, result);
            lbl_status.setText("Đã giải mã " + algorithm + ". " + saved);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dữ liệu không hợp lệ", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btn_openActionPerformed(java.awt.event.ActionEvent evt) {
        String path = FileTextIO.openText(this, txt_ciphertext);
        if (path != null) {
            lbl_status.setText("Đã mở tệp: " + path);
        }
    }

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {
        String path = FileTextIO.saveText(this, txt_ciphertext, "ciphertext.txt");
        if (path != null) {
            lbl_status.setText("Đã lưu tệp: " + path);
        }
    }

    private void btn_sqlActionPerformed(java.awt.event.ActionEvent evt) {
        new frm_KetNoiSQL().setVisible(true);
    }

    private void btn_historyActionPerformed(java.awt.event.ActionEvent evt) {
        new frm_LichSu().setVisible(true);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new frm_TongHop().setVisible(true));
    }
}
