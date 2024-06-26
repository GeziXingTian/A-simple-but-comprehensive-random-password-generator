import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CustomInputDialog extends JDialog {
    private JTextField inputField;
    private String userInput;

    public CustomInputDialog(JFrame parent, String message) {
        super(parent, "输入密码的用途", true);
        setSize(300, 200);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout());

        JLabel label = new JLabel(message);
        add(label);

        inputField = new JTextField(20);
        add(inputField);

        JButton okButton = new JButton("确定");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userInput = inputField.getText();
                dispose();
            }
        });
        getRootPane().setDefaultButton(okButton);
        add(okButton);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (inputField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(CustomInputDialog.this, "你还没有输入", "失败", JOptionPane.ERROR_MESSAGE);
                } else {
                    userInput = inputField.getText();
                    dispose();
                }
            }
        });

        setVisible(true);
    }

    public String getUserInput() {
        return userInput;
    }
}
