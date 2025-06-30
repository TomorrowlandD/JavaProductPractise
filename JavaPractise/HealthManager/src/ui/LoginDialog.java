package ui;

import service.SessionManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 登录对话框
 * 系统启动时显示，验证用户身份
 */
public class LoginDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, cancelButton, registerButton, changePasswordButton, deleteAccountButton;
    private boolean loginSuccess = false;
    
    public LoginDialog() {
        initComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initComponents() {
        setTitle("健康管理系统 - 用户登录");
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        // 创建组件
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("登录");
        cancelButton = new JButton("取消");
        registerButton = new JButton("注册");
        changePasswordButton = new JButton("修改密码");
        deleteAccountButton = new JButton("注销账号");
        // 设置字体
        Font chineseFont = getChineseFont(Font.PLAIN, 14);
        usernameField.setFont(chineseFont);
        passwordField.setFont(chineseFont);
        loginButton.setFont(chineseFont);
        cancelButton.setFont(chineseFont);
        registerButton.setFont(chineseFont);
        changePasswordButton.setFont(chineseFont);
        deleteAccountButton.setFont(chineseFont);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        // 标题面板
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("健康管理系统", JLabel.CENTER);
        titleLabel.setFont(getChineseFont(Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 122, 183));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        // 中央输入面板
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        // 用户名
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(usernameField, gbc);
        // 密码
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(passwordField, gbc);
        add(centerPanel, BorderLayout.CENTER);
        // 底部按钮面板
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(loginButton);
        btnPanel.add(cancelButton);
        btnPanel.add(registerButton);
        btnPanel.add(changePasswordButton);
        btnPanel.add(deleteAccountButton);
        add(btnPanel, BorderLayout.SOUTH);
        // 设置窗口大小和位置
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupEventHandlers() {
        // 登录按钮事件
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // 取消按钮事件
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginSuccess = false;
                dispose();
            }
        });
        
        // 注册按钮事件
        registerButton.addActionListener(e -> onRegister());
        
        // 修改密码按钮事件
        changePasswordButton.addActionListener(e -> onChangePassword());
        
        // 注销账号按钮事件
        deleteAccountButton.addActionListener(e -> onDeleteAccount());
        
        // 回车键登录
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        
        // 默认焦点设置
        SwingUtilities.invokeLater(() -> {
            if (usernameField.getText().trim().isEmpty()) {
                usernameField.requestFocus();
            } else {
                passwordField.requestFocus();
            }
        });
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "请输入用户名和密码", 
                "登录错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (SessionManager.login(username, password)) {
            // 根据用户角色显示不同的欢迎信息
            String welcomeMessage;
            if (SessionManager.isAdmin()) {
                welcomeMessage = "欢迎超级管理员 " + username + "！\n您拥有管理所有用户数据的权限。";
            } else {
                welcomeMessage = "欢迎用户 " + username + "！\n您可以管理自己的健康数据。";
            }
            
            JOptionPane.showMessageDialog(this, 
                welcomeMessage, 
                "登录成功", JOptionPane.INFORMATION_MESSAGE);
            
            loginSuccess = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "用户名或密码错误，请重试", 
                "登录失败", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
    
    private void onRegister() {
        RegisterDialog dialog = new RegisterDialog((Frame) this.getOwner());
        dialog.setVisible(true);
    }
    
    private void onChangePassword() {
        UserChangePasswordDialog dialog = new UserChangePasswordDialog((Frame) this.getOwner());
        dialog.setVisible(true);
    }
    
    private void onDeleteAccount() {
        UserDeleteAccountDialog dialog = new UserDeleteAccountDialog((Frame) this.getOwner());
        dialog.setVisible(true);
    }
    
    /**
     * 获取支持中文的字体
     */
    private Font getChineseFont(int style, int size) {
        String[] fontNames = {
            "微软雅黑", "Microsoft YaHei", "SimSun", "宋体", 
            "SimHei", "黑体", "KaiTi", "楷体"
        };
        
        for (String fontName : fontNames) {
            try {
                Font font = new Font(fontName, style, size);
                if (font.canDisplay('中') && font.canDisplay('文')) {
                    return font;
                }
            } catch (Exception e) {
                // 继续尝试下一个字体
            }
        }
        
        return new Font(Font.SANS_SERIF, style, size);
    }
    
    /**
     * 检查登录是否成功
     */
    public boolean isLoginSuccess() {
        return loginSuccess;
    }
    
    /**
     * 显示登录对话框并返回登录结果
     */
    public static boolean showLoginDialog() {
        LoginDialog dialog = new LoginDialog();
        dialog.setVisible(true);
        return dialog.isLoginSuccess();
    }
} 