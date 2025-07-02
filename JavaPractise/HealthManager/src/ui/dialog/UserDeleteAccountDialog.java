package ui.dialog;

import service.DatabaseManager;
import model.User;
import service.SessionManager;

import javax.swing.*;
import java.awt.*;

public class UserDeleteAccountDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton deleteButton, cancelButton;

    public UserDeleteAccountDialog(Frame parent) {
        super(parent, "注销账号", true);
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

        JLabel titleLabel = new JLabel("注销账号");
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
        panel.add(new JLabel("密码："), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        deleteButton = new JButton("注销");
        cancelButton = new JButton("取消");
        btnPanel.add(deleteButton);
        btnPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        setContentPane(panel);

        deleteButton.addActionListener(e -> onDeleteAccount());
        cancelButton.addActionListener(e -> dispose());
    }

    private void onDeleteAccount() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写所有字段！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 校验用户名和密码
        User user = DatabaseManager.authenticateUser(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "用户名或密码错误！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 二次确认
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "你确定要注销账户吗？此操作不可恢复！",
            "确认注销",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        // 删除users表和user_profile档案
        boolean success = DatabaseManager.deleteUserCompletely(username);
        if (success) {
            // 如果删除的是当前登录用户，强制登出
            if (SessionManager.getCurrentUser() != null && 
                SessionManager.getCurrentUser().getUsername().equals(username)) {
                SessionManager.logout();
                // 关闭主窗口
                Window mainWindow = SwingUtilities.getWindowAncestor(this);
                if (mainWindow != null) {
                    mainWindow.dispose();
                }
            }
            JOptionPane.showMessageDialog(this, "账号已注销，感谢您的使用！", "注销成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "注销失败，请重试。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
} 