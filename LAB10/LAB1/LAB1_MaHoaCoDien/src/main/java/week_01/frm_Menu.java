package week_01;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class frm_Menu extends javax.swing.JFrame {

    private javax.swing.JButton btn_autokey;
    private javax.swing.JButton btn_caesar;
    private javax.swing.JButton btn_playfair;
    private javax.swing.JButton btn_railfence;
    private javax.swing.JButton btn_sql;
    private javax.swing.JButton btn_tancong;
    private javax.swing.JButton btn_tonghop;
    private javax.swing.JButton btn_transposition;
    private javax.swing.JButton btn_vigenere;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;

    public frm_Menu() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btn_caesar = new javax.swing.JButton();
        btn_vigenere = new javax.swing.JButton();
        btn_railfence = new javax.swing.JButton();
        btn_playfair = new javax.swing.JButton();
        btn_transposition = new javax.swing.JButton();
        btn_autokey = new javax.swing.JButton();
        btn_tonghop = new javax.swing.JButton();
        btn_tancong = new javax.swing.JButton();
        btn_sql = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LAB 1 - Mã hóa đối xứng cổ điển");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 20));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("LAB 1 - MÃ HÓA ĐỐI XỨNG CỔ ĐIỂN");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Chọn một bài để mở form tương ứng:");

        btn_caesar.setText("1.1 Caesar");
        btn_caesar.setPreferredSize(new Dimension(200, 35));
        btn_caesar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_caesarActionPerformed(evt);
            }
        });

        btn_vigenere.setText("1.2 Vigenère");
        btn_vigenere.setPreferredSize(new Dimension(200, 35));
        btn_vigenere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_vigenereActionPerformed(evt);
            }
        });

        btn_railfence.setText("1.3 Rail Fence");
        btn_railfence.setPreferredSize(new Dimension(200, 35));
        btn_railfence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_railfenceActionPerformed(evt);
            }
        });

        btn_playfair.setText("1.4 Playfair");
        btn_playfair.setPreferredSize(new Dimension(200, 35));
        btn_playfair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_playfairActionPerformed(evt);
            }
        });

        btn_transposition.setText("1.5 Transposition");
        btn_transposition.setPreferredSize(new Dimension(200, 35));
        btn_transposition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_transpositionActionPerformed(evt);
            }
        });

        btn_autokey.setText("1.6 Autokey");
        btn_autokey.setPreferredSize(new Dimension(200, 35));
        btn_autokey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_autokeyActionPerformed(evt);
            }
        });

        btn_tonghop.setText("Nâng cao 01 - Tổng hợp");
        btn_tonghop.setPreferredSize(new Dimension(200, 35));
        btn_tonghop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tonghopActionPerformed(evt);
            }
        });

        btn_tancong.setText("Nâng cao 02 - Tấn công");
        btn_tancong.setPreferredSize(new Dimension(200, 35));
        btn_tancong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tancongActionPerformed(evt);
            }
        });

        btn_sql.setText("Kết nối SQL Server");
        btn_sql.setPreferredSize(new Dimension(200, 35));
        btn_sql.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_sqlActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Mỗi form mở ra là một cửa sổ riêng; đóng form con không làm tắt chương trình.");

        JPanel buttonGrid = new JPanel(new GridLayout(3, 3, 15, 15));
        buttonGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonGrid.add(btn_caesar);
        buttonGrid.add(btn_vigenere);
        buttonGrid.add(btn_railfence);
        buttonGrid.add(btn_playfair);
        buttonGrid.add(btn_transposition);
        buttonGrid.add(btn_autokey);
        buttonGrid.add(btn_tonghop);
        buttonGrid.add(btn_tancong);
        buttonGrid.add(btn_sql);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headerPanel.add(jLabel1);
        headerPanel.add(jLabel2);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        mainPanel.add(buttonGrid, BorderLayout.CENTER);
        mainPanel.add(jLabel3, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        pack();
        setSize(720, 360);
    }

    private void btn_caesarActionPerformed(java.awt.event.ActionEvent evt) {
        new frm_Caesar().setVisible(true);
    }

    private void btn_vigenereActionPerformed(java.awt.event.ActionEvent evt) {
        new frm_Vigenere().setVisible(true);
    }

    private void btn_railfenceActionPerformed(java.awt.event.ActionEvent evt) {
        new frm_RailFence().setVisible(true);
    }

    private void btn_playfairActionPerformed(java.awt.event.ActionEvent evt) {
        new frm_Playfair().setVisible(true);
    }

    private void btn_transpositionActionPerformed(java.awt.event.ActionEvent evt) {
        new frm_Transposition().setVisible(true);
    }

    private void btn_autokeyActionPerformed(java.awt.event.ActionEvent evt) {
        new frm_Autokey().setVisible(true);
    }

    private void btn_tonghopActionPerformed(java.awt.event.ActionEvent evt) {
        new frm_TongHop().setVisible(true);
    }

    private void btn_tancongActionPerformed(java.awt.event.ActionEvent evt) {
        new frm_TanCong().setVisible(true);
    }

    private void btn_sqlActionPerformed(java.awt.event.ActionEvent evt) {
        new frm_KetNoiSQL().setVisible(true);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new frm_Menu().setVisible(true));
    }
}
