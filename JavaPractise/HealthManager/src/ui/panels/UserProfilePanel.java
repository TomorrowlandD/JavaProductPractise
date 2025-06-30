package ui.panels;

import model.UserProfile;
import ui.AddUserDialog;
import ui.ResetPasswordDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 用户档案界面面板
 * 提供用户基本信息输入、健康数据计算和目标管理功能
 */
public class UserProfilePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // 基本信息组件
    private JTextField nameField;
    private JSpinner ageSpinner;
    private JRadioButton maleRadio, femaleRadio;
    private JTextField phoneField;
    
    // 身体数据组件
    private JTextField heightField;
    private JTextField weightField;
    private JLabel bmiLabel;
    private JLabel categoryLabel;
    private JLabel idealWeightLabel;
    
    // 健康目标组件
    private JTextField targetWeightField;
    private JComboBox<String> fitnessGoalBox;
    private JTextField targetDateField;
    private JProgressBar progressBar;
    private JLabel progressLabel;
    private JTextArea healthNotesArea;
    
    // 健康状况复选框组件
    private JCheckBox noHealthIssuesBox;
    private JCheckBox hypertensionBox;
    private JCheckBox diabetesBox;
    private JCheckBox heartDiseaseBox;
    private JCheckBox jointProblemsBox;
    private JCheckBox allergiesBox;
    private JCheckBox chronicDiseaseBox;
    private JTextField otherHealthField;
    
    // 操作按钮
    private JButton saveButton;
    private JButton calculateButton;
    private JButton clearButton;
    private JButton reportButton;
    private JButton resetPasswordButton;
    
    // 状态信息
    private JLabel lastUpdatedLabel;
    private JLabel createdLabel;
    
    // 当前用户档案对象
    private UserProfile currentProfile;
    
    // 1. 添加多用户相关字段
    private JComboBox<UserProfile> userComboBox;
    private java.util.List<UserProfile> userList;
    private JButton addButton, deleteButton;
    
    @SuppressWarnings("this-escape")
    public UserProfilePanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDefaults();
        // 初始化用户下拉框
        refreshUserComboBox();
    }
    
    /**
     * 初始化所有UI组件
     */
    private void initializeComponents() {
        // 基本信息组件
        nameField = new JTextField(15);
        ageSpinner = new JSpinner(new SpinnerNumberModel(25, UserProfile.MIN_AGE, UserProfile.MAX_AGE, 1));
        maleRadio = new JRadioButton("男", true);
        femaleRadio = new JRadioButton("女");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        phoneField = new JTextField(15);
        
        // 身体数据组件
        heightField = new JTextField(8);
        weightField = new JTextField(8);
        bmiLabel = new JLabel("BMI: --");
        categoryLabel = new JLabel("健康状态: --");
        idealWeightLabel = new JLabel("理想体重: --");
        
        // 健康目标组件
        targetWeightField = new JTextField(8);
        String[] fitnessGoals = {"减脂塑形", "增肌健身", "维持体重", "增重增肌", "运动康复"};
        fitnessGoalBox = new JComboBox<>(fitnessGoals);
        targetDateField = new JTextField(10);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressLabel = new JLabel("进度: 0%");
        healthNotesArea = new JTextArea(2, 20);
        healthNotesArea.setLineWrap(true);
        healthNotesArea.setWrapStyleWord(true);
        healthNotesArea.setToolTipText("可填写健康状况备注，如疾病史、运动限制等");
        
        // 健康状况复选框组件
        noHealthIssuesBox = new JCheckBox("无特殊疾病史");
        hypertensionBox = new JCheckBox("高血压");
        diabetesBox = new JCheckBox("糖尿病");
        heartDiseaseBox = new JCheckBox("心脏病");
        jointProblemsBox = new JCheckBox("关节问题");
        allergiesBox = new JCheckBox("过敏");
        chronicDiseaseBox = new JCheckBox("慢性疾病");
        otherHealthField = new JTextField(10);
        otherHealthField.setToolTipText("可自定义输入其它健康状况");
        
        // 操作按钮
        saveButton = new JButton("保存信息");
        calculateButton = new JButton("重新计算");
        clearButton = new JButton("清空");
        reportButton = new JButton("查看报告");
        resetPasswordButton = new JButton("重置密码");
        
        // 状态信息
        lastUpdatedLabel = new JLabel("最后更新: --");
        createdLabel = new JLabel("创建: --");
        
        // 1. 添加多用户相关字段
        addButton = new JButton("新增用户");
        deleteButton = new JButton("删除用户");
        
        // 设置组件样式
        setupComponentStyles();
    }
    
    /**
     * 获取支持中文的字体
     * 按优先级尝试不同的中文字体，确保中文能正确显示
     * 
     * @param style 字体样式 (Font.PLAIN, Font.BOLD, Font.ITALIC)
     * @param size 字体大小
     * @return 支持中文的字体
     */
    private Font getChineseFont(int style, int size) {
        // 按优先级尝试不同的中文字体
        String[] fontNames = {
            "微软雅黑", "Microsoft YaHei", "SimSun", "宋体", 
            "SimHei", "黑体", "KaiTi", "楷体", "FangSong", "仿宋"
        };
        
        for (String fontName : fontNames) {
            try {
                Font font = new Font(fontName, style, size);
                if (font.canDisplay('中') && font.canDisplay('文')) {
                    return font;
                }
            } catch (Exception e) {
                // 忽略字体创建失败的情况，继续尝试下一个
            }
        }
        
        // 如果所有中文字体都不可用，使用系统默认字体
        return new Font(Font.SANS_SERIF, style, size);
    }
    
    /**
     * 设置组件样式
     */
    private void setupComponentStyles() {
        // 设置字体
        Font defaultFont = getChineseFont(Font.PLAIN, 12);
        Font labelFont = getChineseFont(Font.BOLD, 12);
        
        // BMI显示标签样式
        bmiLabel.setFont(labelFont);
        categoryLabel.setFont(labelFont);
        idealWeightLabel.setFont(getChineseFont(Font.PLAIN, 11));
        
        // 按钮样式
        saveButton.setFont(defaultFont);
        calculateButton.setFont(defaultFont);
        clearButton.setFont(defaultFont);
        reportButton.setFont(defaultFont);
        resetPasswordButton.setFont(defaultFont);
        
        // 进度条样式
        progressBar.setFont(getChineseFont(Font.PLAIN, 10));
        progressLabel.setFont(defaultFont);
        
        // 设置提示文本
        nameField.setToolTipText("请输入您的姓名");
        heightField.setToolTipText("请输入身高（cm）");
        weightField.setToolTipText("请输入体重（kg）");
        targetWeightField.setToolTipText("请输入目标体重（kg）");
        healthNotesArea.setToolTipText("可填写健康状况备注，如疾病史、运动限制等");
    }
    
    /**
     * 设置界面布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("选择用户:"));
        userComboBox = new JComboBox<>();
        topPanel.add(userComboBox);
        topPanel.add(addButton);
        topPanel.add(deleteButton);
        topPanel.add(resetPasswordButton);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // 标题
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(new JLabel("个人档案", JLabel.CENTER), gbc);
        
        // 基本信息面板
        gbc.gridy++; gbc.gridwidth = 2;
        mainPanel.add(createBasicInfoPanel(), gbc);
        
        // 身体数据面板
        gbc.gridy++; gbc.gridwidth = 2;
        mainPanel.add(createBodyDataPanel(), gbc);
        
        // 健康目标面板
        gbc.gridy++; gbc.gridwidth = 2;
        mainPanel.add(createHealthGoalPanel(), gbc);
        
        // 健康状况复选框面板
        gbc.gridy++; gbc.gridwidth = 2;
        mainPanel.add(createHealthStatusPanel(), gbc);
        
        // 健康备注面板
        gbc.gridy++; gbc.gridwidth = 2;
        mainPanel.add(createHealthNotesPanel(), gbc);
        
        // 操作按钮面板
        gbc.gridy++; gbc.gridwidth = 2;
        mainPanel.add(createButtonPanel(), gbc);
        
        // 状态信息面板
        gbc.gridy++; gbc.gridwidth = 2;
        mainPanel.add(createStatusPanel(), gbc);
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(null);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * 创建基本信息面板
     */
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("基本信息"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        
        // 第一行：姓名和性别
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("姓名:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("性别:"), gbc);
        gbc.gridx = 3;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        panel.add(genderPanel, gbc);
        
        // 第二行：年龄和电话
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("年龄:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel agePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        agePanel.add(ageSpinner);
        agePanel.add(new JLabel("岁"));
        panel.add(agePanel, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("电话:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(phoneField, gbc);
        
        return panel;
    }
    
    /**
     * 创建身体数据面板
     */
    private JPanel createBodyDataPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("身体数据"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        
        // 第一行：身高和BMI
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("身高:"), gbc);
        gbc.gridx = 1;
        JPanel heightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        heightPanel.add(heightField);
        heightPanel.add(new JLabel("cm"));
        panel.add(heightPanel, gbc);
        
        gbc.gridx = 2;
        panel.add(bmiLabel, gbc);
        
        // 第二行：体重和健康状态
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("体重:"), gbc);
        gbc.gridx = 1;
        JPanel weightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        weightPanel.add(weightField);
        weightPanel.add(new JLabel("kg"));
        panel.add(weightPanel, gbc);
        
        gbc.gridx = 2;
        panel.add(categoryLabel, gbc);
        
        // 第三行：理想体重
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        panel.add(idealWeightLabel, gbc);
        
        return panel;
    }
    
    /**
     * 创建健康目标面板
     */
    private JPanel createHealthGoalPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("健康目标"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 第一行：目标体重和健身目标
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("目标体重:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel targetWeightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        targetWeightPanel.add(targetWeightField);
        targetWeightPanel.add(new JLabel("kg"));
        panel.add(targetWeightPanel, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("健身目标:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(fitnessGoalBox, gbc);
        
        // 第二行：目标日期
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("目标日期:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(targetDateField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        JButton autoDateButton = new JButton("自动计算");
        autoDateButton.setFont(getChineseFont(Font.PLAIN, 10));
        autoDateButton.addActionListener(e -> calculateSuggestedTargetDate());
        panel.add(autoDateButton, gbc);
        
        // 第三行：进度条
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(new JLabel("完成进度:"), BorderLayout.WEST);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(progressLabel, BorderLayout.EAST);
        panel.add(progressPanel, gbc);
        
        return panel;
    }
    
    /**
     * 创建健康状况复选框面板
     */
    private JPanel createHealthStatusPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("健康状况"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("健康状况:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.add(noHealthIssuesBox);
        statusPanel.add(hypertensionBox);
        statusPanel.add(diabetesBox);
        statusPanel.add(heartDiseaseBox);
        statusPanel.add(jointProblemsBox);
        statusPanel.add(allergiesBox);
        statusPanel.add(chronicDiseaseBox);
        statusPanel.add(new JLabel("其它:"));
        statusPanel.add(otherHealthField);
        panel.add(statusPanel, gbc);
        
        return panel;
    }
    
    /**
     * 创建健康备注面板
     */
    private JPanel createHealthNotesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("健康备注"));
        panel.add(new JLabel("备注:"), BorderLayout.WEST);
        JScrollPane scrollPane = new JScrollPane(healthNotesArea);
        scrollPane.setPreferredSize(new Dimension(0, 50));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * 创建操作按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("数据管理"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(saveButton, gbc);
        gbc.gridx = 1;
        panel.add(calculateButton, gbc);
        gbc.gridx = 2;
        panel.add(clearButton, gbc);
        gbc.gridx = 3;
        panel.add(reportButton, gbc);
        
        return panel;
    }
    
    /**
     * 创建状态信息面板
     */
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(lastUpdatedLabel);
        panel.add(new JLabel(" | "));
        panel.add(createdLabel);
        return panel;
    }
    
    /**
     * 设置事件处理
     */
    private void setupEventHandlers() {
        // 实时BMI计算
        heightField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateAndCalculate();
            }
        });
        
        weightField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validateAndCalculate();
            }
        });
        
        targetWeightField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateProgressDisplay();
            }
        });
        
        // 健身目标改变时重新验证目标体重的一致性
        fitnessGoalBox.addActionListener(e -> {
            // 重新验证目标体重
            validateAndCalculate();
        });
        
        // 健康状况复选框逻辑
        setupHealthStatusLogic();
        
        // 按钮事件
        saveButton.addActionListener(e -> saveUserProfile());
        calculateButton.addActionListener(e -> calculateAll());
        clearButton.addActionListener(e -> clearAll(true));
        reportButton.addActionListener(e -> showReport());
        resetPasswordButton.addActionListener(e -> onResetPassword());
        
        // 1. 添加多用户相关事件
        userComboBox.addActionListener(e -> {
            UserProfile selected = (UserProfile) userComboBox.getSelectedItem();
            if (selected != null) {
                setProfileToUI(selected);
            } else {
                clearAll(false);
            }
        });
        addButton.addActionListener(e -> onAddUser());
        deleteButton.addActionListener(e -> {
            UserProfile selected = (UserProfile) userComboBox.getSelectedItem();
            if (selected != null) {
                // 检查是否删除当前登录用户
                String currentUsername = service.SessionManager.getCurrentUser() != null ? 
                    service.SessionManager.getCurrentUser().getUsername() : null;
                if (selected.getName().equals(currentUsername)) {
                    JOptionPane.showMessageDialog(this, 
                        "不能删除当前登录的用户！请先切换到其他用户或退出登录。", 
                        "删除失败", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int result = JOptionPane.showConfirmDialog(this, 
                    "确定要删除该用户吗？\n注意：删除用户将同时删除该用户的所有数据（档案、记录、计划等）", 
                    "确认删除", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    // 使用新的完全删除方法，确保同时删除users表和user_profile表中的数据
                    boolean success = service.DatabaseManager.deleteUserCompletelyById(selected.getId());
                    if (success) {
                        JOptionPane.showMessageDialog(this, "用户删除成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                        refreshUserComboBox();
                        clearAll(false);
                    } else {
                        JOptionPane.showMessageDialog(this, "用户删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
    
    private void onAddUser() {
        AddUserDialog dialog = new AddUserDialog((Frame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        if (dialog.isUserCreated()) {
            refreshUserComboBox();
            JOptionPane.showMessageDialog(this, "新用户创建成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * 设置健康状况复选框逻辑
     */
    private void setupHealthStatusLogic() {
        // "无特殊疾病史"复选框的特殊逻辑
        noHealthIssuesBox.addActionListener(e -> {
            if (noHealthIssuesBox.isSelected()) {
                // 选中"无特殊疾病史"时，取消其他所有选项并禁用
                hypertensionBox.setSelected(false);
                diabetesBox.setSelected(false);
                heartDiseaseBox.setSelected(false);
                jointProblemsBox.setSelected(false);
                allergiesBox.setSelected(false);
                chronicDiseaseBox.setSelected(false);
                hypertensionBox.setEnabled(false);
                diabetesBox.setEnabled(false);
                heartDiseaseBox.setEnabled(false);
                jointProblemsBox.setEnabled(false);
                allergiesBox.setEnabled(false);
                chronicDiseaseBox.setEnabled(false);
                otherHealthField.setText("");
                otherHealthField.setEnabled(false);
            } else {
                // 取消"无特殊疾病史"时，重新启用其他选项
                hypertensionBox.setEnabled(true);
                diabetesBox.setEnabled(true);
                heartDiseaseBox.setEnabled(true);
                jointProblemsBox.setEnabled(true);
                allergiesBox.setEnabled(true);
                chronicDiseaseBox.setEnabled(true);
                otherHealthField.setEnabled(true);
            }
        });
        
        // 其他健康状况复选框的逻辑
        JCheckBox[] healthIssueBoxes = {
            hypertensionBox, diabetesBox, heartDiseaseBox, 
            jointProblemsBox, allergiesBox, chronicDiseaseBox
        };
        
        for (JCheckBox box : healthIssueBoxes) {
            box.addActionListener(e -> {
                if (box.isSelected()) {
                    // 选中任何健康问题时，自动取消"无特殊疾病史"
                    noHealthIssuesBox.setSelected(false);
                    hypertensionBox.setEnabled(true);
                    diabetesBox.setEnabled(true);
                    heartDiseaseBox.setEnabled(true);
                    jointProblemsBox.setEnabled(true);
                    allergiesBox.setEnabled(true);
                    chronicDiseaseBox.setEnabled(true);
                    otherHealthField.setEnabled(true);
                }
            });
        }
        // "其它"输入框联动逻辑
        otherHealthField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (!otherHealthField.getText().trim().isEmpty()) {
                    noHealthIssuesBox.setSelected(false);
                    hypertensionBox.setEnabled(true);
                    diabetesBox.setEnabled(true);
                    heartDiseaseBox.setEnabled(true);
                    jointProblemsBox.setEnabled(true);
                    allergiesBox.setEnabled(true);
                    chronicDiseaseBox.setEnabled(true);
                    otherHealthField.setEnabled(true);
                }
            }
        });
    }
    
    /**
     * 设置默认值
     */
    private void setupDefaults() {
        // 设置默认目标日期为3个月后
        LocalDate defaultTarget = LocalDate.now().plusMonths(3);
        targetDateField.setText(defaultTarget.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // 设置默认健康备注
        healthNotesArea.setText("无特殊备注");
        
        // 设置默认健康状况为"无特殊疾病史"
        noHealthIssuesBox.setSelected(true);
        // 触发事件处理器来禁用其他选项
        noHealthIssuesBox.getActionListeners()[0].actionPerformed(null);
        
        updateStatusLabels();
    }
    
    /**
     * 验证输入并计算BMI
     */
    private void validateAndCalculate() {
        // 验证身高
        validateFieldValue(heightField, "身高", (text) -> {
            try {
                double height = Double.parseDouble(text);
                return UserProfile.validateHeight(height);
            } catch (NumberFormatException e) {
                return new UserProfile.ValidationResult(false, "请输入有效的数字");
            }
        });
        
        // 验证体重
        validateFieldValue(weightField, "体重", (text) -> {
            try {
                double weight = Double.parseDouble(text);
                return UserProfile.validateWeight(weight);
            } catch (NumberFormatException e) {
                return new UserProfile.ValidationResult(false, "请输入有效的数字");
            }
        });
        
        // 验证目标体重
        validateFieldValue(targetWeightField, "目标体重", (text) -> {
            try {
                if (text.isEmpty()) {
                    return new UserProfile.ValidationResult(true, "目标体重为可选项");
                }
                double targetWeight = Double.parseDouble(text);
                String currentWeightText = weightField.getText().trim();
                if (!currentWeightText.isEmpty()) {
                    try {
                        double currentWeight = Double.parseDouble(currentWeightText);
                        
                        // 首先验证目标体重本身
                        UserProfile.ValidationResult targetResult = UserProfile.validateTargetWeight(currentWeight, targetWeight);
                        if (!targetResult.isValid()) {
                            return targetResult;
                        }
                        
                        // 然后验证与健身目标的一致性
                        String fitnessGoal = (String) fitnessGoalBox.getSelectedItem();
                        UserProfile.ValidationResult consistencyResult = UserProfile.validateGoalConsistency(currentWeight, targetWeight, fitnessGoal);
                        if (!consistencyResult.isValid()) {
                            return consistencyResult;
                        }
                        
                        return new UserProfile.ValidationResult(true, "目标体重设置合理且与健身目标一致");
                    } catch (NumberFormatException e) {
                        return UserProfile.validateWeight(targetWeight);
                    }
                }
                return UserProfile.validateWeight(targetWeight);
            } catch (NumberFormatException e) {
                return new UserProfile.ValidationResult(false, "请输入有效的数字");
            }
        });
        
        // 如果身高和体重都有效，计算BMI
        try {
            String heightText = heightField.getText().trim();
            String weightText = weightField.getText().trim();
            
            if (!heightText.isEmpty() && !weightText.isEmpty()) {
                double height = Double.parseDouble(heightText);
                double weight = Double.parseDouble(weightText);
                
                UserProfile.ValidationResult heightResult = UserProfile.validateHeight(height);
                UserProfile.ValidationResult weightResult = UserProfile.validateWeight(weight);
                
                if (heightResult.isValid() && weightResult.isValid()) {
                    // 计算BMI
                    double bmi = weight / ((height / 100) * (height / 100));
                    String category = getBMICategory(bmi);
                    Color categoryColor = getBMIColor(bmi);
                    
                    bmiLabel.setText(String.format("BMI: %.1f", bmi));
                    categoryLabel.setText(category);
                    categoryLabel.setForeground(categoryColor);
                    
                    // 计算理想体重范围
                    double minIdeal = 18.5 * (height / 100) * (height / 100);
                    double maxIdeal = 24.0 * (height / 100) * (height / 100);
                    idealWeightLabel.setText(String.format("理想体重: %.0f-%.0f kg", minIdeal, maxIdeal));
                    
                    updateProgressDisplay();
                } else {
                    // 清空BMI显示
                    bmiLabel.setText("BMI: --");
                    categoryLabel.setText("健康状态: --");
                    idealWeightLabel.setText("理想体重: --");
                }
            }
        } catch (NumberFormatException e) {
            // 格式错误已经在上面处理了
        }
    }
    
    /**
     * 验证单个字段的值
     */
    private void validateFieldValue(JTextField field, String fieldName, FieldValidator validator) {
        String text = field.getText().trim();
        
        if (text.isEmpty()) {
            field.setBackground(Color.WHITE);
            field.setToolTipText("请输入" + fieldName);
            return;
        }
        
        UserProfile.ValidationResult result = validator.validate(text);
        if (result.isValid()) {
            field.setBackground(Color.WHITE);
            field.setToolTipText(result.getMessage());
        } else {
            field.setBackground(new Color(255, 220, 220));
            field.setToolTipText("错误: " + result.getMessage());
        }
    }
    
    /**
     * 字段验证器接口
     */
    private interface FieldValidator {
        UserProfile.ValidationResult validate(String text);
    }
    
    /**
     * 获取BMI分类
     */
    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "偏瘦";
        if (bmi < 24.0) return "正常";
        if (bmi < 28.0) return "超重";
        return "肥胖";
    }
    
    /**
     * 获取BMI对应的颜色
     */
    private Color getBMIColor(double bmi) {
        if (bmi < 18.5 || bmi >= 28.0) return Color.RED;
        if (bmi < 20.0 || bmi >= 25.0) return Color.ORANGE;
        return new Color(0, 150, 0);
    }
    
    /**
     * 更新进度显示
     */
    private void updateProgressDisplay() {
        try {
            String weightText = weightField.getText().trim();
            String targetText = targetWeightField.getText().trim();
            
            if (!weightText.isEmpty() && !targetText.isEmpty()) {
                double currentWeight = Double.parseDouble(weightText);
                double targetWeight = Double.parseDouble(targetText);
                
                // 假设起始体重比当前重3kg（简化计算）
                double startWeight = currentWeight + (currentWeight > targetWeight ? 3 : -3);
                
                if (startWeight != targetWeight) {
                    double progress = Math.abs((startWeight - currentWeight) / (startWeight - targetWeight)) * 100;
                    progress = Math.max(0, Math.min(100, progress));
                    
                    progressBar.setValue((int) progress);
                    progressLabel.setText(String.format("%.0f%% 完成", progress));
                    
                    double diff = currentWeight - targetWeight;
                    if (Math.abs(diff) < 0.5) {
                        progressLabel.setText("目标达成！");
                        progressLabel.setForeground(Color.GREEN);
                    } else {
                        String direction = diff > 0 ? "减重" : "增重";
                        progressLabel.setText(String.format("需%s: %.1fkg", direction, Math.abs(diff)));
                        progressLabel.setForeground(Color.BLACK);
                    }
                }
            }
        } catch (NumberFormatException e) {
            // 忽略解析错误
        }
    }
    
    /**
     * 计算建议目标日期
     */
    private void calculateSuggestedTargetDate() {
        try {
            String weightText = weightField.getText().trim();
            String targetText = targetWeightField.getText().trim();
            
            if (!weightText.isEmpty() && !targetText.isEmpty()) {
                double currentWeight = Double.parseDouble(weightText);
                double targetWeight = Double.parseDouble(targetText);
                double weightDiff = Math.abs(currentWeight - targetWeight);
                
                // 健康减重速度：0.5kg/周，增重速度：0.3kg/周
                double weeklyRate = targetWeight < currentWeight ? 0.5 : 0.3;
                int weeksNeeded = (int) Math.ceil(weightDiff / weeklyRate);
                
                LocalDate suggestedDate = LocalDate.now().plusWeeks(weeksNeeded);
                targetDateField.setText(suggestedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                
                JOptionPane.showMessageDialog(this, 
                    String.format("建议在 %d 周内达成目标\n每周%s约 %.1fkg", 
                                 weeksNeeded, 
                                 targetWeight < currentWeight ? "减重" : "增重",
                                 weeklyRate),
                    "目标日期建议", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请先填写当前体重和目标体重！", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * 计算所有数据
     */
    private void calculateAll() {
        validateAndCalculate();
        updateProgressDisplay();
        JOptionPane.showMessageDialog(this, "计算完成！", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 保存用户档案
     */
    private void saveUserProfile() {
        try {
            // 首先验证年龄（从Spinner获取）
            int age = (Integer) ageSpinner.getValue();
            UserProfile.ValidationResult ageResult = UserProfile.validateAge(age);
            if (!ageResult.isValid()) {
                JOptionPane.showMessageDialog(this, "年龄验证失败：" + ageResult.getMessage(), 
                                            "数据验证错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // 验证姓名
            String name = nameField.getText().trim();
            UserProfile.ValidationResult nameResult = UserProfile.validateName(name);
            if (!nameResult.isValid()) {
                JOptionPane.showMessageDialog(this, "姓名验证失败：" + nameResult.getMessage(), 
                                            "数据验证错误", JOptionPane.WARNING_MESSAGE);
                nameField.requestFocus();
                return;
            }
            // 新增：校验用户名唯一性
            java.util.List<UserProfile> allUsers = service.DatabaseManager.getAllUserProfiles();
            for (UserProfile user : allUsers) {
                // 新增用户或修改为新名字时，不能与其他用户重名
                if (user.getName().equals(name)) {
                    // 如果是编辑已有用户，允许和自己重名
                    if (userComboBox.getSelectedItem() == null || ((UserProfile)userComboBox.getSelectedItem()).getId() != user.getId()) {
                        JOptionPane.showMessageDialog(this, "用户名已存在，请更换姓名！", "提示", JOptionPane.WARNING_MESSAGE);
                        nameField.requestFocus();
                        return;
                    }
                }
            }
            // 验证电话
            String phone = phoneField.getText().trim();
            UserProfile.ValidationResult phoneResult = UserProfile.validatePhone(phone);
            if (!phoneResult.isValid()) {
                JOptionPane.showMessageDialog(this, "电话验证失败：" + phoneResult.getMessage(), 
                                            "数据验证错误", JOptionPane.WARNING_MESSAGE);
                phoneField.requestFocus();
                return;
            }
            UserProfile profile = getProfileFromUI();
            // 使用完整的数据验证
            UserProfile.ValidationResult validationResult = profile.validateProfile();
            if (validationResult.isValid()) {
                // 检查基本信息是否完整
                if (profile.isProfileComplete()) {
                    currentProfile = profile;
                    updateStatusLabels();
                    // 新增或更新数据库
                    if (userComboBox.getSelectedItem() instanceof UserProfile) {
                        // 编辑：更新已有用户
                        profile.setId(((UserProfile) userComboBox.getSelectedItem()).getId());
                        service.DatabaseManager.updateUserProfile(profile);
                    } else {
                        // 新增：插入新用户
                        service.DatabaseManager.insertUserProfile(profile);
                    }
                    refreshUserComboBox();
                    // 显示详细的保存成功信息
                    String successMessage = "成功: 用户信息保存成功！\n\n" +
                                          "基本信息已验证通过\n" +
                                          "所有数值都在合理范围内\n" +
                                          "数据格式正确";
                    if (profile.getHeight() > 0 && profile.getWeight() > 0) {
                        successMessage += String.format("\n\n您的BMI指数：%.1f (%s)", 
                                                       profile.calculateBMI(), 
                                                       profile.getBMICategory());
                    }
                    JOptionPane.showMessageDialog(this, successMessage, "保存成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "请填写完整的基本信息：\n" +
                        "• 姓名（必填）\n" +
                        "• 年龄（必填）\n" +
                        "• 性别（必选）\n" +
                        "• 身高（必填）\n" +
                        "• 体重（必填）", 
                        "信息不完整", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                // 显示具体的验证错误信息
                JOptionPane.showMessageDialog(this, 
                    "数据验证失败：\n\n错误: " + validationResult.getMessage() + 
                    "\n\n请检查并修正输入的数据。", 
                    "数据验证错误", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "保存失败：" + e.getMessage() + 
                "\n\n请检查输入的数据格式是否正确。", 
                "保存错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 从界面获取用户档案数据
     */
    private UserProfile getProfileFromUI() {
        UserProfile profile = new UserProfile();
        
        profile.setName(nameField.getText().trim());
        profile.setAge((Integer) ageSpinner.getValue());
        profile.setGender(maleRadio.isSelected() ? "男" : "女");
        profile.setPhone(phoneField.getText().trim());
        
        if (!heightField.getText().trim().isEmpty()) {
            profile.setHeight(Double.parseDouble(heightField.getText().trim()));
        }
        if (!weightField.getText().trim().isEmpty()) {
            profile.setWeight(Double.parseDouble(weightField.getText().trim()));
        }
        if (!targetWeightField.getText().trim().isEmpty()) {
            profile.setTargetWeight(Double.parseDouble(targetWeightField.getText().trim()));
        }
        
        profile.setFitnessGoal((String) fitnessGoalBox.getSelectedItem());
        profile.setHealthNotes(healthNotesArea.getText().trim());
        
        // 收集健康状况信息
        StringBuilder healthStatus = new StringBuilder();
        if (noHealthIssuesBox.isSelected()) {
            healthStatus.append("无特殊疾病史");
        } else {
            boolean hasAnyStatus = false;
            if (hypertensionBox.isSelected()) {
                healthStatus.append("高血压");
                hasAnyStatus = true;
            }
            if (diabetesBox.isSelected()) {
                if (hasAnyStatus) healthStatus.append(", ");
                healthStatus.append("糖尿病");
                hasAnyStatus = true;
            }
            if (heartDiseaseBox.isSelected()) {
                if (hasAnyStatus) healthStatus.append(", ");
                healthStatus.append("心脏病");
                hasAnyStatus = true;
            }
            if (jointProblemsBox.isSelected()) {
                if (hasAnyStatus) healthStatus.append(", ");
                healthStatus.append("关节问题");
                hasAnyStatus = true;
            }
            if (allergiesBox.isSelected()) {
                if (hasAnyStatus) healthStatus.append(", ");
                healthStatus.append("过敏");
                hasAnyStatus = true;
            }
            if (chronicDiseaseBox.isSelected()) {
                if (hasAnyStatus) healthStatus.append(", ");
                healthStatus.append("慢性疾病");
                hasAnyStatus = true;
            }
            String other = otherHealthField.getText().trim();
            if (!other.isEmpty()) {
                if (hasAnyStatus) healthStatus.append(", ");
                healthStatus.append("其它:").append(other);
                hasAnyStatus = true;
            }
            if (!hasAnyStatus) {
                healthStatus.append("未选择");
            }
        }
        profile.setHealthStatus(healthStatus.toString());
        
        return profile;
    }
    
    /**
     * 将用户档案数据设置到界面
     */
    public void setProfileToUI(UserProfile profile) {
        if (profile != null) {
            nameField.setText(profile.getName() != null ? profile.getName() : "");
            ageSpinner.setValue(profile.getAge());
            if ("女".equals(profile.getGender())) {
                femaleRadio.setSelected(true);
            } else {
                maleRadio.setSelected(true);
            }
            phoneField.setText(profile.getPhone() != null ? profile.getPhone() : "");
            
            heightField.setText(profile.getHeight() > 0 ? String.valueOf(profile.getHeight()) : "");
            weightField.setText(profile.getWeight() > 0 ? String.valueOf(profile.getWeight()) : "");
            targetWeightField.setText(profile.getTargetWeight() > 0 ? String.valueOf(profile.getTargetWeight()) : "");
            
            if (profile.getFitnessGoal() != null) {
                fitnessGoalBox.setSelectedItem(profile.getFitnessGoal());
            }
            if (profile.getHealthNotes() != null) {
                healthNotesArea.setText(profile.getHealthNotes());
            } else {
                healthNotesArea.setText("无特殊备注");
            }
            
            // 恢复健康状况复选框
            if (profile.getHealthStatus() != null) {
                String healthStatus = profile.getHealthStatus();
                // 先清空所有选择
                noHealthIssuesBox.setSelected(false);
                hypertensionBox.setSelected(false);
                diabetesBox.setSelected(false);
                heartDiseaseBox.setSelected(false);
                jointProblemsBox.setSelected(false);
                allergiesBox.setSelected(false);
                chronicDiseaseBox.setSelected(false);
                otherHealthField.setText("");
                if (healthStatus.contains("无特殊疾病史")) {
                    noHealthIssuesBox.setSelected(true);
                } else {
                    if (healthStatus.contains("高血压")) hypertensionBox.setSelected(true);
                    if (healthStatus.contains("糖尿病")) diabetesBox.setSelected(true);
                    if (healthStatus.contains("心脏病")) heartDiseaseBox.setSelected(true);
                    if (healthStatus.contains("关节问题")) jointProblemsBox.setSelected(true);
                    if (healthStatus.contains("过敏")) allergiesBox.setSelected(true);
                    if (healthStatus.contains("慢性疾病")) chronicDiseaseBox.setSelected(true);
                    // 处理"其它"
                    String[] parts = healthStatus.split(",");
                    for (String part : parts) {
                        part = part.trim();
                        if (part.startsWith("其它:")) {
                            otherHealthField.setText(part.substring(3));
                        }
                    }
                }
                // 触发逻辑处理
                setupHealthStatusLogic();
            }
            
            currentProfile = profile;
            validateAndCalculate();
            updateStatusLabels();
        }
    }
    
    /**
     * 清空所有输入
     */
    private void clearAll() {
        clearAll(true);
    }
    private void clearAll(boolean confirm) {
        if (confirm) {
            int result = JOptionPane.showConfirmDialog(this, "确定要清空所有信息吗？", "确认清空", JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }
        nameField.setText("");
        ageSpinner.setValue(25);
        maleRadio.setSelected(true);
        phoneField.setText("");
        heightField.setText("");
        weightField.setText("");
        targetWeightField.setText("");
        fitnessGoalBox.setSelectedIndex(0);
        healthNotesArea.setText("无特殊备注");
        noHealthIssuesBox.setSelected(true);
        hypertensionBox.setSelected(false);
        diabetesBox.setSelected(false);
        heartDiseaseBox.setSelected(false);
        jointProblemsBox.setSelected(false);
        allergiesBox.setSelected(false);
        chronicDiseaseBox.setSelected(false);
        otherHealthField.setText("");
        if (noHealthIssuesBox.getActionListeners().length > 0) {
            noHealthIssuesBox.getActionListeners()[0].actionPerformed(null);
        }
        bmiLabel.setText("BMI: --");
        categoryLabel.setText("健康状态: --");
        idealWeightLabel.setText("理想体重: --");
        progressBar.setValue(0);
        progressLabel.setText("进度: 0%");
        heightField.setBackground(Color.WHITE);
        weightField.setBackground(Color.WHITE);
        currentProfile = null;
        updateStatusLabels();
    }
    
    /**
     * 显示报告
     */
    private void showReport() {
        if (currentProfile != null && currentProfile.isProfileComplete()) {
            StringBuilder report = new StringBuilder();
            report.append("=== 个人健康档案报告 ===\n\n");
            report.append("基本信息：\n");
            report.append(String.format("姓名：%s\n", currentProfile.getName()));
            report.append(String.format("年龄：%d岁\n", currentProfile.getAge()));
            report.append(String.format("性别：%s\n", currentProfile.getGender()));
            report.append(String.format("身高：%.1fcm\n", currentProfile.getHeight()));
            report.append(String.format("体重：%.1fkg\n", currentProfile.getWeight()));
            report.append("\n健康指标：\n");
            report.append(String.format("BMI：%.1f (%s)\n", currentProfile.calculateBMI(), currentProfile.getBMICategory()));
            if (currentProfile.getTargetWeight() > 0) {
                report.append(String.format("目标体重：%.1fkg\n", currentProfile.getTargetWeight()));
                report.append(String.format("体重差值：%+.1fkg\n", currentProfile.getWeightDifference()));
            }
            report.append(String.format("健身目标：%s\n", currentProfile.getFitnessGoal()));
            
            JTextArea textArea = new JTextArea(report.toString());
            textArea.setEditable(false);
            textArea.setFont(getChineseFont(Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "健康档案报告", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "请先完善并保存用户信息！", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * 更新状态标签
     */
    private void updateStatusLabels() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        if (currentProfile != null) {
            lastUpdatedLabel.setText("最后更新: " + now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            if (currentProfile.getCreatedDate() != null) {
                createdLabel.setText("创建: " + currentProfile.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        } else {
            lastUpdatedLabel.setText("最后更新: --");
            createdLabel.setText("创建: --");
        }
    }
    
    /**
     * 获取当前用户档案
     */
    public UserProfile getCurrentProfile() {
        return currentProfile;
    }
    
    /**
     * 初始化用户下拉框
     */
    private void refreshUserComboBox() {
        if (service.SessionManager.isAdmin()) {
            userList = service.DatabaseManager.getAllUserProfiles();
            userComboBox.setModel(new DefaultComboBoxModel<>(userList.toArray(new UserProfile[0])));
            userComboBox.setEnabled(true);
            userComboBox.setVisible(true);
            addButton.setVisible(true);
            deleteButton.setVisible(true);
            resetPasswordButton.setVisible(true);
        } else {
            UserProfile currentUserProfile = service.SessionManager.getCurrentProfile();
            if (currentUserProfile != null) {
                userList = new java.util.ArrayList<>();
                userList.add(currentUserProfile);
                userComboBox.setModel(new DefaultComboBoxModel<>(new UserProfile[]{currentUserProfile}));
                userComboBox.setSelectedItem(currentUserProfile);
                userComboBox.setEnabled(false);
                userComboBox.setVisible(true);
                addButton.setVisible(false);
                deleteButton.setVisible(false);
                resetPasswordButton.setVisible(false);
            } else {
                userList = new java.util.ArrayList<>();
                userComboBox.setModel(new DefaultComboBoxModel<>(userList.toArray(new UserProfile[0])));
                userComboBox.setEnabled(false);
                userComboBox.setVisible(true);
                addButton.setVisible(false);
                deleteButton.setVisible(false);
                resetPasswordButton.setVisible(false);
            }
        }
    }
    
    private void onResetPassword() {
        UserProfile selected = (UserProfile) userComboBox.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "请先选择要重置密码的用户！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ResetPasswordDialog dialog = new ResetPasswordDialog((Frame) SwingUtilities.getWindowAncestor(this), selected.getName());
        dialog.setVisible(true);
    }
} 