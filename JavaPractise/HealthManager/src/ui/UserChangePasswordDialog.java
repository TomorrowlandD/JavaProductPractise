package ui;

import service.DatabaseManager;
import model.User;

import javax.swing.*;
import java.awt.*;
// import java.util.List;

public class UserChangePasswordDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton changeButton, cancelButton;

    public UserChangePasswordDialog(Frame parent) {
        super(parent, "修改密码", true);
        initComponents();
        setLocationRelativeTo(parent);
        setResizable(false);
        pack();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("修改密码");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("用户名："), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("原密码："), gbc);
        oldPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(oldPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("新密码："), gbc);
        newPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(newPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("确认新密码："), gbc);
        confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        changeButton = new JButton("修改");
        cancelButton = new JButton("取消");
        btnPanel.add(changeButton);
        btnPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        setContentPane(panel);

        changeButton.addActionListener(e -> onChangePassword());
        cancelButton.addActionListener(e -> dispose());
    }

    private void onChangePassword() {
        String username = usernameField.getText().trim();
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写所有字段！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "两次输入的新密码不一致！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 校验用户名和原密码
        User user = DatabaseManager.authenticateUser(username, oldPassword);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "用户名或原密码错误！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean success = DatabaseManager.updateUserPassword(username, newPassword);
        if (success) {
            JOptionPane.showMessageDialog(this, "密码修改成功！请使用新密码登录。", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "密码修改失败，请重试。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
} 