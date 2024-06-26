import FileShredder.FileShredder;




import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UI {

    private JFrame frame;
    private JTextField lengthField;
    private JLabel resultLabel;
    private PasswordGenerator generator;



    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UI window = new UI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showWelcomeOnce() {
        String isFirstRunMarkerFilePath = "first_run_marker.txt";
        File markerFile = new File(isFirstRunMarkerFilePath);

        if (!markerFile.exists()) {
            JOptionPane.showMessageDialog(frame, "欢迎使用密码生成器v1.2E1！", "欢迎 code by abdesunny", JOptionPane.INFORMATION_MESSAGE);
            try {
                if (markerFile.createNewFile()) {
                    System.out.println("首次运行标记文件创建成功");
                } else {
                    System.out.println("首次运行标记文件创建失败");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    public UI() {
        initialize();
        generator = new PasswordGenerator();
    }
    private void savePasswordToFile(String password) {
        String directoryPath = "C:\\PasswordSaver";
        String filePath = directoryPath + "\\Password.txt";

        try {
            File dir = new File(directoryPath);
            if (!dir.exists() && !dir.mkdirs()) {
                JOptionPane.showMessageDialog(frame, "无法创建文件夹，请检查权限。", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            File file = new File(filePath);
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write(password);
                writer.write("\n");
            }
            hideFolder(directoryPath);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "保存密码时发生错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String askForPasswordPurpose() {
        CustomInputDialog dialog = new CustomInputDialog(frame, "请输入密码的用途（必填）：");
        String purpose = dialog.getUserInput();
        return purpose != null ? purpose : "";
    }

    private String formatPasswordWithPurpose(String prefix, String password) {
        return "《" + prefix + "》：" + password;
    }


    private void hideFolder(String path) {
        if (System.getProperty("os.name").contains("Windows")) {
            try {
                ProcessBuilder pb = new ProcessBuilder("attrib", "+H", path);
                Process p = pb.start();
                p.waitFor();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "隐藏文件夹失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("非Windows系统不支");
        }
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("密码生成器1.2E1|Code by abdesunny");
        frame.setBounds(100, 100, 390, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new GridBagLayout());
        frame.setResizable(false);
        frame.setMaximizedBounds(frame.getBounds());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;

        JLabel lengthLabel = new JLabel("密码长度(自定义):");
        frame.getContentPane().add(lengthLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        lengthField = new JTextField(5);
        frame.getContentPane().add(lengthField, gbc);
        lengthField.setColumns(10);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JCheckBox lowercaseBox = new JCheckBox("包含小写字母");
        lowercaseBox.setSelected(true);
        frame.getContentPane().add(lowercaseBox, gbc);

        gbc.gridx = 1;
        JCheckBox uppercaseBox = new JCheckBox("包含大写字母");
        uppercaseBox.setSelected(true);
        frame.getContentPane().add(uppercaseBox, gbc);

        gbc.gridx = 2;
        JCheckBox numbersBox = new JCheckBox("包含数字");
        numbersBox.setSelected(true);
        frame.getContentPane().add(numbersBox, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        JCheckBox specialCharsBox = new JCheckBox("包含特殊字符");
        frame.getContentPane().add(specialCharsBox, gbc);

        gbc.gridy = 3;
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        JButton generateButton = new JButton("生成密码");
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int length = Integer.parseInt(lengthField.getText());
                    if (!lowercaseBox.isSelected() && !uppercaseBox.isSelected() && !numbersBox.isSelected() && !specialCharsBox.isSelected()) {
                        lowercaseBox.setSelected(true);
                        uppercaseBox.setSelected(true);
                        numbersBox.setSelected(true);
                    }

                    String password = generator.generateCustomPassword(length,
                            lowercaseBox.isSelected(),
                            uppercaseBox.isSelected(),
                            numbersBox.isSelected(),
                            specialCharsBox.isSelected());
                    String purpose = askForPasswordPurpose();

                    String formattedPurpose = purpose.isEmpty()
                            ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            : purpose;
                    String formattedPassword = formatPasswordWithPurpose(formattedPurpose, password);

                    AutoCopy.copyTextToClipboard(password);
                    savePasswordToFile(formattedPassword);
                    JOptionPane.showMessageDialog(frame, "生成成功，密码已复制并保存", "成功", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "输入有效的数字长度", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        frame.getContentPane().add(generateButton, gbc);

        frame.setVisible(true);
        showWelcomeOnce();
        gbc.gridy = 3;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.ipadx = 1;
        JButton viewPasswordsButton = new JButton("密码查看");
        viewPasswordsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new PasswordViewer();
            }
        });
        frame.getContentPane().add(viewPasswordsButton, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JButton FixButton = new JButton("紧急粉碎");
        FixButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String filePath = "C:\\PasswordSaver\\Password.txt";
                FileShredder shredder = new FileShredder();
                shredder.shredFile(filePath);
            }
        });
        frame.getContentPane().add(FixButton, gbc);
    }

}
