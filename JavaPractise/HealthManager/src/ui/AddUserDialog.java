package ui;

import service.DatabaseManager;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddUserDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton addButton, cancelButton;
    private boolean userCreated = false;

    public AddUserDialog(Frame parent) {
        super(parent, "新增用户", true);
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

        JLabel titleLabel = new JLabel("新增用户");
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
        panel.add(new JLabel("初始密码："), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("确认密码："), gbc);
        confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addButton = new JButton("添加");
        cancelButton = new JButton("取消");
        btnPanel.add(addButton);
        btnPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panel.add(btnPanel, gbc);

        setContentPane(panel);

        addButton.addActionListener(e -> onAddUser());
        cancelButton.addActionListener(e -> dispose());
    }

    private void onAddUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写所有字段！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "两次输入的密码不一致！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 检查用户名是否已存在
        List<User> users = DatabaseManager.getAllUsers();
        boolean exists = users.stream().anyMatch(u -> u.getUsername().equals(username));
        if (exists) {
            JOptionPane.showMessageDialog(this, "用户名已存在，请更换用户名！", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 创建新用户，角色为USER
        boolean success = DatabaseManager.createUser(username, password, "USER");
        if (success) {
            // 自动创建档案
            boolean profileSuccess = DatabaseManager.insertUserProfileForNewUser(username);
            if (profileSuccess) {
                userCreated = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "用户创建成功，但自动创建档案失败，请联系管理员。", "部分成功", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "用户创建失败，请重试。", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isUserCreated() {
        return userCreated;
    }
} 