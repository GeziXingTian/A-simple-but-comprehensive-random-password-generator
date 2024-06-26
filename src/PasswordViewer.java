import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class PasswordViewer extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private File passwordFile = new File("C:\\PasswordSaver\\Password.txt");

    public PasswordViewer() {
        super("已保存的密码");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        model = new DefaultTableModel(new Object[]{"密码"}, 0);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        JButton deleteButton = new JButton("删除选中项");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length > 0) {
                    for (int row : selectedRows) {
                        model.removeRow(row);
                    }
                    savePasswordsToFile();
                } else {
                    JOptionPane.showMessageDialog(PasswordViewer.this, "先选中要删除的密码。", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        loadPasswordsFromFile();
        setVisible(true);
    }
    private void loadPasswordsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(passwordFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                model.addRow(new Object[]{line});
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "读取密码文件发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
    private void savePasswordsToFile() {
        List<String> passwords = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            passwords.add((String) model.getValueAt(i, 0));
        }
        try {
            Files.write(passwordFile.toPath(), passwords);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "更新密码文件发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PasswordViewer());
    }
}
