package ui;

import service.DatabaseManager;

import javax.swing.*;
import java.awt.*;

public class ResetPasswordDialog extends JDialog {
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton resetButton, cancelButton;
    private String username;

    public ResetPasswordDialog(Frame parent, String username) {
        super(parent, "重置密码 - " + username, true);
        this.username = username;
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

        JLabel titleLabel = new JLabel("重置密码");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("新密码："), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("确认密码："), gbc);
        confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        resetButton = new JButton("重置");
        cancelButton = new JButton("取消");
        btnPanel.add(resetButton);
        btnPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        setContentPane(panel);

        resetButton.addActionListener(e -> onReset());
        cancelButton.addActionListener(e -> dispose());
    }

    private void onReset() {
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写所有字段！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "两次输入的密码不一致！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean success = DatabaseManager.updateUserPassword(username, password);
        if (success) {
            JOptionPane.showMessageDialog(this, "密码重置成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "密码重置失败，请重试。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
} 