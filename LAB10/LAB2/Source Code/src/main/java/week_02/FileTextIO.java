package week_02;

import java.awt.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JFrame;

public final class FileTextIO {

    private FileTextIO() {
    }

    public static String openString(Component parent) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        try {
            return Files.readString(chooser.getSelectedFile().toPath(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent, "Không thể đọc tệp: " + ex.getMessage(),
                    "Lỗi đọc tệp", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static String saveString(Component parent, String content, String defaultName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(defaultName));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        Path path = chooser.getSelectedFile().toPath();
        try {
            Files.writeString(path, content, StandardCharsets.UTF_8);
            return path.toString();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent, "Không thể ghi tệp: " + ex.getMessage(),
                    "Lỗi ghi tệp", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static String openText(JFrame parent, JTextArea target) {
        String content = openString(parent);
        if (content != null) {
            target.setText(content);
        }
        return content;
    }

    public static String saveText(JFrame parent, JTextArea source, String defaultName) {
        return saveString(parent, source.getText(), defaultName);
    }
}
