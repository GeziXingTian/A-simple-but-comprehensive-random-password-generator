import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import java.io.*;
import java.util.prefs.Preferences;
import javax.swing.JCheckBox;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class PasswordHash extends JFrame {

    private static final String FIRST_RUN_MARKER_FILE = System.getProperty("user.home") + File.separator + ".passwordhashfirstrun";
    private static final String EXIT_CONFIRMATION_PREFERENCE = "exitConfirmation";
    private JComboBox<Integer> passwordLengthSelector;
    private JCheckBox lowercaseBox, uppercaseBox, numericBox, specialCharsBox;
    private JButton generateButton;
    private JTextField fileNameField;
    private JTextArea passwordTextArea;
    private JRadioButton beginningButton, endButton;
    private JLabel fileNameLabel;
    private AtomicBoolean shouldExit = new AtomicBoolean(false);
    public PasswordHash() {
        if (isFirstRun()) {
            JOptionPane.showMessageDialog(this, "这是一个随机密码生成工具，它可以选择随机生成的密码，并且自动填充且保存在桌面。\n\n\n\n\n\n\n\n\ncode by Abdesunny");
            markAsNotFirstRun();
        }
        initUI();
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Preferences prefs = Preferences.userNodeForPackage(PasswordHash.class);
                boolean rememberChoice = prefs.getBoolean(EXIT_CONFIRMATION_PREFERENCE, true);

                if (rememberChoice) {
                    int userChoice;
                    Object[] options = {"确定", "取消"};
                    JCheckBox doNotAskAgainBox = new JCheckBox("不再询问");
                    Object[] params = {doNotAskAgainBox};
                    JPanel panel = new JPanel(new BorderLayout());
                    panel.add(new JLabel("确定要退出吗？建议先检查是否成功创建并保存文本，未保存文本可能会造成密码丢失"), BorderLayout.NORTH);
                    panel.add(doNotAskAgainBox, BorderLayout.SOUTH);

                    userChoice = JOptionPane.showOptionDialog(
                            PasswordHash.this,
                            panel,
                            "code by abdesunny",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                    );

                    if (userChoice == JOptionPane.YES_OPTION) {
                        if (doNotAskAgainBox.isSelected()) {
                            prefs.putBoolean(EXIT_CONFIRMATION_PREFERENCE, false);
                        }
                        shouldExit.set(true);
                        dispose();
                    } else {
                        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    }
                } else {
                    shouldExit.set(true);
                    dispose();
                }
            }
        });
    }

    private boolean isFirstRun() {
        return !new File(FIRST_RUN_MARKER_FILE).exists();
    }
    private void markAsNotFirstRun() {
        try {
            Files.createFile(Paths.get(FIRST_RUN_MARKER_FILE));
        } catch (IOException e) {
            System.err.println("无法创建标记文件：" + e.getMessage());
        }
    }
    private void initUI() {
        setTitle("PasswordHash-code by abdesunny");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.LINE_START;
        JPanel optionsPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JLabel lengthLabel = new JLabel("密码长度（10-256位可选 | 推荐默认为10位）：");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        mainPanel.add(lengthLabel, gbc);
        Integer[] lengths = new Integer[257];
        for (int i = 0; i < lengths.length; i++) {
            lengths[i] = 10 + i * 1;
        }
        passwordLengthSelector = new JComboBox<>(lengths);
        optionsPanel.add(lengthLabel);
        optionsPanel.add(passwordLengthSelector);
        JLabel customContentLabel = new JLabel("固定内容：");
        beginningButton = new JRadioButton("开头", false);
        endButton = new JRadioButton("末尾", true);
        ButtonGroup positionGroup = new ButtonGroup();
        positionGroup.add(beginningButton);
        positionGroup.add(endButton);
        JTextField customContentField = new JTextField(10);
        JPanel customContentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customContentPanel.add(customContentLabel);
        customContentPanel.add(customContentField);
        customContentPanel.add(beginningButton);
        customContentPanel.add(endButton);
        optionsPanel.add(customContentPanel);
        JPanel checkBoxesPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        lowercaseBox = new JCheckBox("小写字母");
        uppercaseBox = new JCheckBox("大写字母");
        numericBox = new JCheckBox("数字");
        specialCharsBox = new JCheckBox("特殊字符");
        checkBoxesPanel.add(lowercaseBox);
        checkBoxesPanel.add(uppercaseBox);
        checkBoxesPanel.add(numericBox);
        checkBoxesPanel.add(specialCharsBox);
        optionsPanel.add(checkBoxesPanel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        mainPanel.add(optionsPanel, gbc);
        JPanel fileNamePanel = new JPanel();
        fileNameLabel = new JLabel("文件名称：");
        fileNameField = new JTextField(20);
        GroupLayout layout = new GroupLayout(fileNamePanel);
        fileNamePanel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(fileNameLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fileNameField))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(fileNameLabel)
                        .addComponent(fileNameField)
        );
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(fileNamePanel, gbc);
        generateButton = new JButton("生成");
        passwordTextArea = new JTextArea(1, 30);
        passwordTextArea.setEditable(false);
        passwordTextArea.setLineWrap(true);
        passwordTextArea.setWrapStyleWord(true);
        JScrollPane scrollPaneForPassword = new JScrollPane(passwordTextArea);
        JPanel buttonAndDisplayPanel = new JPanel(new BorderLayout());
        buttonAndDisplayPanel.add(generateButton, BorderLayout.PAGE_START);
        buttonAndDisplayPanel.add(scrollPaneForPassword, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(buttonAndDisplayPanel, gbc);
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int desiredPasswordLength = (int) passwordLengthSelector.getSelectedItem();
                String possibleCharacters = "";
                if (lowercaseBox.isSelected()) {
                    possibleCharacters += "abcdefghijklmnopqrstuvwxyz";
                }
                if (uppercaseBox.isSelected()) {
                    possibleCharacters += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                }
                if (numericBox.isSelected()) {
                    possibleCharacters += "0123456789";
                }
                if (specialCharsBox.isSelected()) {
                    possibleCharacters += "!@#$%^&*()_+-=[]{}|;:,.<>?";
                }
                String customContent = customContentField.getText().trim();
                boolean hasCustomContent = !customContent.isEmpty();
                int randomPasswordLength = desiredPasswordLength;
                if (hasCustomContent) {
                    int contentLength = customContent.length();
                    if (contentLength >= desiredPasswordLength) {
                        JOptionPane.showMessageDialog(null, "错误，自定义内容长度大于等于目标密码长度");
                        return;
                    } else {
                        randomPasswordLength -= contentLength;
                    }
                }
                if (randomPasswordLength < 10 || randomPasswordLength > 256 || possibleCharacters.isEmpty() || fileNameField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "错误，通常是文件名未填写或无效或（减去固定内容后）密码长度超出允许值 | Error, usually because the file name is not filled in or invalid or the password length (excluding fixed content) exceeds the allowed value");
                    return;
                }
                SecureRandom randomizer = new SecureRandom();
                StringBuilder generatedPassword = new StringBuilder(randomPasswordLength);
                for (int i = 0; i < randomPasswordLength; i++) {
                    generatedPassword.append(possibleCharacters.charAt(randomizer.nextInt(possibleCharacters.length())));
                }

                if (hasCustomContent) {
                    if (beginningButton.isSelected()) {
                        generatedPassword.insert(0, customContent);
                    } else if (endButton.isSelected()) {
                        generatedPassword.append(customContent);
                    }
                }
                passwordTextArea.setText(generatedPassword.toString());
                String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
                String filePath = desktopPath + File.separator + fileNameField.getText() + ".txt";
                try {
                    Path path = Paths.get(filePath);
                    if (!Files.exists(path.getParent())) {
                        Files.createDirectories(path.getParent());
                    }
                    Files.write(path, generatedPassword.toString().getBytes());
                    String message = "很好，密码已经成功保存了，文件名为：" + fileNameField.getText() + ".txt" +
                            " | 密码已自动填充，保存的文件通常在桌面。";
                    JOptionPane.showMessageDialog(null, message);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "错误，无法保存密码至文件内：" + ex.getMessage());
                }
            }
        });

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PasswordHash().setVisible(true));
    }
}
