package ui.panels;

import model.ExercisePlan;
import model.UserProfile;
import service.DatabaseManager;
import javax.swing.*;
import javax.swing.border.TitledBorder;
//import javax.swing.table.DefaultCellEditor;
// import javax.swing.table.TableColumn;
// import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 运动计划管理面板
 * 提供简约朴素的运动计划制定和管理功能
 * 升级版本 - 支持实际时长录入和管理
 */
public class ExercisePlanPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // 用户选择组件
    private JComboBox<UserProfile> userComboBox;
    private JButton refreshBtn;
    private JButton addPlanButton;
    private JButton deletePlanButton;
    private JButton cancelEditButton;
    
    // 计划制定组件
    private JPanel exerciseTypePanel;
    private JCheckBox[] exerciseTypeChecks;
    private JTextField dateField;
    private JTextField durationField;
    private JTextField actualDurationField; // 新增：实际时长输入框
    private JComboBox<String> intensityBox;
    private JTextArea notesArea;
    private JButton saveButton;
    
    // 状态显示
    private JLabel statusLabel;
    
    // 1. 添加成员变量
    private JTable planTable;
    private PlanTableModel planTableModel;
    private JLabel statsLabel;
    private java.util.List<ExercisePlan> currentPlans = new java.util.ArrayList<>();
    private int editingPlanId = -1;
    // 新增：跟踪表单是否有未保存的修改
    private boolean hasUnsavedChanges = false;
    // 新增：保存原始数据用于比较
    private ExercisePlan originalPlan = null;
    
    /**
     * 获取支持中文的字体
     */
    private Font getChineseFont(int style, int size) {
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
                // 忽略字体创建失败的情况
            }
        }
        
        return new Font(Font.SANS_SERIF, style, size);
    }
    
    @SuppressWarnings("this-escape")
    public ExercisePlanPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDefaults();
        refreshUserComboBox();
    }
    
    /**
     * 初始化组件
     */
    private void initializeComponents() {
        // 用户选择组件
        userComboBox = new JComboBox<>();
        refreshBtn = new JButton("刷新");
        addPlanButton = new JButton("新增计划");
        deletePlanButton = new JButton("删除计划");
        cancelEditButton = new JButton("取消编辑");
        
        // 计划制定组件
        exerciseTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        exerciseTypeChecks = new JCheckBox[ExercisePlan.EXERCISE_TYPES.length];
        for (int i = 0; i < ExercisePlan.EXERCISE_TYPES.length; i++) {
            exerciseTypeChecks[i] = new JCheckBox(ExercisePlan.EXERCISE_TYPES[i]);
            exerciseTypePanel.add(exerciseTypeChecks[i]);
        }
        dateField = new JTextField(10);
        durationField = new JTextField(5);
        actualDurationField = new JTextField(5); // 新增：实际时长输入框
        intensityBox = new JComboBox<>(ExercisePlan.INTENSITY_LEVELS);
        notesArea = new JTextArea(2, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        saveButton = new JButton("保存计划");
        
        // 状态显示
        statusLabel = new JLabel("准备就绪");
        
        // 设置组件样式
        setupComponentStyles();
    }
    
    /**
     * 设置组件样式
     */
    private void setupComponentStyles() {
        Font defaultFont = getChineseFont(Font.PLAIN, 12);
        Font labelFont = getChineseFont(Font.BOLD, 12);
        
        // 设置字体
        userComboBox.setFont(defaultFont);
        refreshBtn.setFont(defaultFont);
        addPlanButton.setFont(defaultFont);
        deletePlanButton.setFont(defaultFont);
        cancelEditButton.setFont(defaultFont);
        for (JCheckBox cb : exerciseTypeChecks) cb.setFont(defaultFont);
        dateField.setFont(defaultFont);
        durationField.setFont(defaultFont);
        actualDurationField.setFont(defaultFont); // 新增
        intensityBox.setFont(defaultFont);
        notesArea.setFont(defaultFont);
        saveButton.setFont(defaultFont);
        statusLabel.setFont(defaultFont);
        
        // 设置提示文本
        dateField.setToolTipText("请输入计划日期 (格式: yyyy-MM-dd)");
        durationField.setToolTipText("请输入计划运动时长 (小时)");
        actualDurationField.setToolTipText("请输入实际运动时长 (小时)，完成后填写");
        notesArea.setToolTipText("可填写备注信息，如各项运动时间分配");
        
        // 设置实际时长输入框初始状态
        actualDurationField.setEnabled(false);
        actualDurationField.setBackground(Color.LIGHT_GRAY);
    }
    
    /**
     * 设置界面布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 顶部：用户选择区域
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // 中部：计划制定区域
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);
        
        // 新增底部历史计划表和统计
        planTableModel = new PlanTableModel();
        planTable = new JTable(planTableModel);
        planTable.setRowHeight(24);
        
        // 新增：为完成状态列设置复选框编辑器
        setupCompletionColumnEditor();
        
        JScrollPane tableScroll = new JScrollPane(planTable);
        tableScroll.setPreferredSize(new Dimension(0, 180));
        statsLabel = new JLabel("统计：");
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(tableScroll);
        bottomPanel.add(statsLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 创建顶部面板（用户选择）
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(new TitledBorder("用户选择"));
        
        panel.add(new JLabel("选择用户:"));
        panel.add(userComboBox);
        panel.add(refreshBtn);
        panel.add(addPlanButton);
        panel.add(deletePlanButton);
        panel.add(cancelEditButton);
        
        return panel;
    }
    
    /**
     * 创建中部面板（计划制定）
     */
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("运动计划制定"));
        
        // 运动类型选择
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        typePanel.add(new JLabel("运动类型:"));
        typePanel.add(exerciseTypePanel);
        panel.add(typePanel);
        
        // 日期和时长输入
        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        dateTimePanel.add(new JLabel("计划日期:"));
        dateTimePanel.add(dateField);
        dateTimePanel.add(new JLabel("计划时长:"));
        dateTimePanel.add(durationField);
        dateTimePanel.add(new JLabel("小时"));
        dateTimePanel.add(Box.createHorizontalStrut(20)); // 间距
        dateTimePanel.add(new JLabel("实际时长:")); // 新增
        dateTimePanel.add(actualDurationField); // 新增
        dateTimePanel.add(new JLabel("小时")); // 新增
        panel.add(dateTimePanel);
        
        // 强度选择
        JPanel intensityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        intensityPanel.add(new JLabel("运动强度:"));
        intensityPanel.add(intensityBox);
        panel.add(intensityPanel);
        
        // 备注输入
        JPanel notesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        notesPanel.add(new JLabel("备注:"));
        notesPanel.add(new JScrollPane(notesArea));
        panel.add(notesPanel);
        
        // 保存按钮
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        savePanel.add(saveButton);
        savePanel.add(statusLabel);
        panel.add(savePanel);
        
        return panel;
    }
    
    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        // 用户选择变化
        userComboBox.addActionListener(e -> {
            if (!checkUnsavedChanges()) return;
            refreshPlanTable();
        });
        
        // 刷新按钮
        refreshBtn.addActionListener(e -> {
            refreshUserComboBox();
            refreshPlanTable();
        });
        
        // 新增计划按钮
        addPlanButton.addActionListener(e -> {
            if (!checkUnsavedChanges()) return;
            clearForm();
            editingPlanId = -1;
            saveButton.setText("保存计划");
            statusLabel.setText("请填写新的运动计划");
        });
        
        // 删除计划按钮
        deletePlanButton.addActionListener(e -> {
            int selectedRow = planTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "请先选择要删除的计划", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            ExercisePlan plan = currentPlans.get(selectedRow);
            int result = JOptionPane.showConfirmDialog(this,
                    String.format("确定要删除计划 \"%s\" 吗？", plan.getExerciseType()),
                    "确认删除",
                    JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                if (DatabaseManager.deleteExercisePlanById(plan.getId())) {
                    statusLabel.setText("计划删除成功");
                    refreshPlanTable();
                    clearForm();
                    editingPlanId = -1;
                } else {
                    JOptionPane.showMessageDialog(this, "删除计划失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // 取消编辑按钮
        cancelEditButton.addActionListener(e -> {
            if (checkUnsavedChanges()) {
                clearForm();
                editingPlanId = -1;
                saveButton.setText("保存计划");
                statusLabel.setText("已取消编辑");
            }
        });
        
        // 保存按钮
        saveButton.addActionListener(e -> saveExercisePlan());
        
        // 表格双击编辑
        planTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = planTable.getSelectedRow();
                    if (selectedRow != -1 && checkUnsavedChanges()) {
                        ExercisePlan plan = currentPlans.get(selectedRow);
                        fillFormWithPlan(plan);
                    }
                }
            }
        });
        
        // 表格单击选择
        planTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedRow = planTable.getSelectedRow();
                    if (selectedRow != -1) {
                        ExercisePlan plan = currentPlans.get(selectedRow);
                        statusLabel.setText(String.format("选中计划: %s (%s)", 
                            plan.getExerciseType(), plan.getPlanDateString()));
                    }
                }
            }
        });
        
        // 添加表单变化监听
        setupFormChangeListeners();
    }
    
    /**
     * 设置默认值
     */
    private void setupDefaults() {
        // 设置当前日期
        LocalDate today = LocalDate.now();
        dateField.setText(today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // 设置默认强度
        intensityBox.setSelectedItem("中");
        
        // 设置默认时长
        durationField.setText("1.0");
    }
    
    /**
     * 刷新用户下拉框
     */
    private void refreshUserComboBox() {
        UserProfile currentSelected = (UserProfile) userComboBox.getSelectedItem();
        userComboBox.removeAllItems();
        if (service.SessionManager.isAdmin()) {
            List<UserProfile> profiles = DatabaseManager.getAllUserProfiles();
            for (UserProfile profile : profiles) {
                userComboBox.addItem(profile);
            }
            userComboBox.setEnabled(true);
            if (currentSelected != null) {
                for (int i = 0; i < userComboBox.getItemCount(); i++) {
                    UserProfile profile = userComboBox.getItemAt(i);
                    if (profile.getName().equals(currentSelected.getName())) {
                        userComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } else {
            UserProfile currentUserProfile = service.SessionManager.getCurrentProfile();
            if (currentUserProfile != null) {
                userComboBox.addItem(currentUserProfile);
                userComboBox.setSelectedItem(currentUserProfile);
                userComboBox.setEnabled(false);
            }
        }
        refreshPlanTable();
    }
    
    /**
     * 清空表单
     */
    private void clearForm() {
        for (JCheckBox cb : exerciseTypeChecks) {
            cb.setSelected(false);
        }
        
        LocalDate today = LocalDate.now();
        dateField.setText(today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        durationField.setText("1.0");
        actualDurationField.setText(""); // 新增：清空实际时长
        intensityBox.setSelectedItem("中");
        notesArea.setText("");
        
        // 重置实际时长输入框状态
        actualDurationField.setEnabled(false);
        actualDurationField.setBackground(Color.LIGHT_GRAY);
        
        editingPlanId = -1;
        hasUnsavedChanges = false;
        originalPlan = null;
    }
    
    /**
     * 保存运动计划
     */
    private void saveExercisePlan() {
        // 验证用户选择
        UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "请先选择用户", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 验证运动类型
        StringBuilder exerciseTypes = new StringBuilder();
        for (JCheckBox cb : exerciseTypeChecks) {
            if (cb.isSelected()) {
                if (exerciseTypes.length() > 0) {
                    exerciseTypes.append(", ");
                }
                exerciseTypes.append(cb.getText());
            }
        }
        
        if (exerciseTypes.length() == 0) {
            JOptionPane.showMessageDialog(this, "请选择至少一种运动类型", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 验证日期
        String dateStr = dateField.getText().trim();
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入计划日期", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        LocalDate planDate;
        try {
            planDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "日期格式错误，请使用 yyyy-MM-dd 格式", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 验证计划时长
        String durationStr = durationField.getText().trim();
        if (durationStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入计划时长", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Double duration;
        try {
            duration = Double.parseDouble(durationStr);
            if (duration <= 0 || duration > 24) {
                JOptionPane.showMessageDialog(this, "计划时长必须在0-24小时之间", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "计划时长格式错误，请输入数字", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 验证实际时长（如果有输入）
        Double actualDuration = null;
        String actualDurationStr = actualDurationField.getText().trim();
        if (!actualDurationStr.isEmpty()) {
            try {
                actualDuration = Double.parseDouble(actualDurationStr);
                if (actualDuration < 0 || actualDuration > 24) {
                    JOptionPane.showMessageDialog(this, "实际时长必须在0-24小时之间", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 新增：业务逻辑验证 - 实际时长应该大于等于计划时长
                if (actualDuration < duration) {
                    int result = JOptionPane.showConfirmDialog(this,
                        String.format("实际时长(%.1f小时)小于计划时长(%.1f小时)，\n这可能表示运动计划未完全完成。\n\n确定要保存吗？", 
                            actualDuration, duration),
                        "实际时长验证",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                    
                    if (result != JOptionPane.YES_OPTION) {
                        actualDurationField.requestFocus();
                        return;
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "实际时长格式错误，请输入数字", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // 创建或更新计划对象
        ExercisePlan plan = new ExercisePlan();
        if (editingPlanId != -1) {
            plan.setId(editingPlanId);
        }
        plan.setUserName(selectedUser.getName());
        plan.setExerciseType(exerciseTypes.toString());
        plan.setPlanDate(planDate);
        plan.setDuration(duration);
        plan.setActualDuration(actualDuration); // 新增：设置实际时长
        plan.setIntensity((String) intensityBox.getSelectedItem());
        plan.setNotes(notesArea.getText().trim());
        
        // 如果有实际时长，自动标记为完成
        if (actualDuration != null && actualDuration > 0) {
            plan.setCompleted(true);
        }
        
        // 保存到数据库
        boolean success;
        if (editingPlanId == -1) {
            success = DatabaseManager.insertExercisePlan(plan);
        } else {
            success = DatabaseManager.updateExercisePlan(plan);
        }
        
        if (success) {
            statusLabel.setText(editingPlanId == -1 ? "运动计划保存成功" : "运动计划更新成功");
            clearForm();
            refreshPlanTable();
            hasUnsavedChanges = false;
        } else {
            JOptionPane.showMessageDialog(this, "保存计划失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 用计划数据填充表单
     */
    private void fillFormWithPlan(ExercisePlan plan) {
        // 清空并设置运动类型
        for (JCheckBox cb : exerciseTypeChecks) {
            cb.setSelected(false);
        }
        
        String[] types = plan.getExerciseType().split(", ");
        for (String type : types) {
            for (JCheckBox cb : exerciseTypeChecks) {
                if (cb.getText().equals(type.trim())) {
                    cb.setSelected(true);
                    break;
                }
            }
        }
        
        // 设置其他字段
        dateField.setText(plan.getPlanDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        durationField.setText(plan.getDuration() != null ? plan.getDuration().toString() : "");
        
        // 新增：设置实际时长
        if (plan.getActualDuration() != null) {
            actualDurationField.setText(plan.getActualDuration().toString());
            actualDurationField.setEnabled(true);
            actualDurationField.setBackground(Color.WHITE);
        } else {
            actualDurationField.setText("");
            actualDurationField.setEnabled(plan.isCompleted()); // 如果已完成，允许编辑实际时长
            actualDurationField.setBackground(plan.isCompleted() ? Color.WHITE : Color.LIGHT_GRAY);
        }
        
        intensityBox.setSelectedItem(plan.getIntensity());
        notesArea.setText(plan.getNotes() != null ? plan.getNotes() : "");
        // 设置编辑ID
        editingPlanId = plan.getId();
        saveButton.setText("保存修改");
        statusLabel.setText("正在编辑计划（ID=" + plan.getId() + ")，修改后请点击保存修改");
        
        // 新增：保存原始数据并重置修改标记
        originalPlan = new ExercisePlan();
        originalPlan.setId(plan.getId());
        originalPlan.setUserName(plan.getUserName());
        originalPlan.setExerciseType(plan.getExerciseType());
        originalPlan.setPlanDate(plan.getPlanDate());
        originalPlan.setDuration(plan.getDuration());
        originalPlan.setActualDuration(plan.getActualDuration()); // 新增
        originalPlan.setIntensity(plan.getIntensity());
        originalPlan.setNotes(plan.getNotes());
        originalPlan.setCompleted(plan.isCompleted());
        hasUnsavedChanges = false;
    }
    
    // 3. TableModel内部类 - 升级版本
    private class PlanTableModel extends javax.swing.table.AbstractTableModel {
        private final String[] columns = {"日期", "类型", "计划时长", "实际时长", "强度", "备注", "完成"};
        @Override public int getRowCount() { return currentPlans.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }
        @Override public Object getValueAt(int row, int col) {
            ExercisePlan plan = currentPlans.get(row);
            switch (col) {
                case 0: return plan.getPlanDateString();
                case 1: return plan.getExerciseType();
                case 2: return plan.getDurationString();
                case 3: return plan.getActualDurationString(); // 新增：实际时长列
                case 4: return plan.getIntensity();
                case 5: return plan.getNotes();
                case 6: return plan.isCompleted(); // 调整索引
                default: return "";
            }
        }
        @Override public Class<?> getColumnClass(int col) {
            if (col == 6) return Boolean.class; // 调整完成状态列索引
            return String.class;
        }
        @Override public boolean isCellEditable(int row, int col) {
            return col == 6; // 只有完成状态列可编辑（调整索引）
        }
        @Override public void setValueAt(Object value, int row, int col) {
            if (col == 6 && row >= 0 && row < currentPlans.size()) { // 调整完成状态列索引
                ExercisePlan plan = currentPlans.get(row);
                boolean newValue = (Boolean) value;
                if (plan.isCompleted() != newValue) {
                    // 新增：如果标记为完成且没有实际时长，提示输入
                    if (newValue && plan.getActualDuration() == null) {
                        String actualDurationStr = JOptionPane.showInputDialog(
                            ExercisePlanPanel.this,
                            "请输入实际运动时长（小时）：",
                            "录入实际时长",
                            JOptionPane.QUESTION_MESSAGE
                        );
                        
                        if (actualDurationStr != null && !actualDurationStr.trim().isEmpty()) {
                            try {
                                Double actualDuration = Double.parseDouble(actualDurationStr.trim());
                                if (actualDuration >= 0 && actualDuration <= 24) {
                                    // 新增：业务逻辑验证 - 实际时长应该大于等于计划时长
                                    Double plannedDuration = plan.getDuration();
                                    if (plannedDuration != null && actualDuration < plannedDuration) {
                                        int result = JOptionPane.showConfirmDialog(ExercisePlanPanel.this,
                                            String.format("实际时长(%.1f小时)小于计划时长(%.1f小时)，\n这可能表示运动计划未完全完成。\n\n确定要标记为完成吗？", 
                                                actualDuration, plannedDuration),
                                            "实际时长验证",
                                            JOptionPane.YES_NO_OPTION,
                                            JOptionPane.QUESTION_MESSAGE);
                                        
                                        if (result != JOptionPane.YES_OPTION) {
                                            return; // 不更新完成状态
                                        }
                                    }
                                    plan.setActualDuration(actualDuration);
                                } else {
                                    JOptionPane.showMessageDialog(ExercisePlanPanel.this,
                                        "实际时长必须在0-24小时之间", "输入错误", JOptionPane.WARNING_MESSAGE);
                                    return; // 不更新完成状态
                                }
                            } catch (NumberFormatException e) {
                                JOptionPane.showMessageDialog(ExercisePlanPanel.this,
                                    "请输入有效的数字", "输入错误", JOptionPane.WARNING_MESSAGE);
                                return; // 不更新完成状态
                            }
                        }
                    }
                    
                    plan.setCompleted(newValue);
                    // 立即更新数据库
                    if (DatabaseManager.updateExercisePlan(plan)) {
                        // 通过SwingUtilities.invokeLater确保在EDT中执行UI更新
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("完成状态已更新");
                            updateStatsLabel(); // 更新统计信息
                            fireTableDataChanged(); // 刷新整个表格以显示实际时长变化
                        });
                    } else {
                        // 更新失败，恢复原状态
                        plan.setCompleted(!newValue);
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(ExercisePlanPanel.this, 
                                "更新完成状态失败，请重试", "更新失败", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                }
                fireTableCellUpdated(row, col);
            }
        }
    }
    
    // 4. 刷新表格和统计信息
    private void refreshPlanTable() {
        UserProfile selected = (UserProfile) userComboBox.getSelectedItem();
        if (selected != null) {
            currentPlans = DatabaseManager.getExercisePlansByUser(selected.getName());
        } else {
            currentPlans = new java.util.ArrayList<>();
        }
        planTableModel.fireTableDataChanged();
        updateStatsLabel();
    }
    
    private void updateStatsLabel() {
        int total = currentPlans.size();
        int completed = 0;
        double totalPlannedHours = 0.0;
        double totalActualHours = 0.0;
        
        for (ExercisePlan plan : currentPlans) {
            if (plan.isCompleted()) completed++;
            if (plan.getDuration() != null) totalPlannedHours += plan.getDuration();
            if (plan.getActualDuration() != null) totalActualHours += plan.getActualDuration();
        }
        
        String rate = total > 0 ? String.format("%.0f%%", completed * 100.0 / total) : "0%";
        statsLabel.setText(String.format("统计：总计划%d条，已完成%d条，完成率%s | 计划时长%.1f小时，实际时长%.1f小时", 
            total, completed, rate, totalPlannedHours, totalActualHours));
    }
    
    /**
     * 为表单组件添加变化监听器
     */
    private void setupFormChangeListeners() {
        // 运动类型复选框变化监听
        for (JCheckBox cb : exerciseTypeChecks) {
            cb.addActionListener(e -> markFormChanged());
        }
        
        // 日期字段变化监听
        dateField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { markFormChanged(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { markFormChanged(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { markFormChanged(); }
        });
        
        // 计划时长字段变化监听
        durationField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { markFormChanged(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { markFormChanged(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { markFormChanged(); }
        });
        
        // 新增：实际时长字段变化监听
        actualDurationField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { markFormChanged(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { markFormChanged(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { markFormChanged(); }
        });
        
        // 强度下拉框变化监听
        intensityBox.addActionListener(e -> markFormChanged());
        
        // 备注文本区域变化监听
        notesArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { markFormChanged(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { markFormChanged(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { markFormChanged(); }
        });
    }
    
    /**
     * 标记表单已发生变化
     */
    private void markFormChanged() {
        if (editingPlanId != -1) {
            hasUnsavedChanges = true;
            statusLabel.setText("正在编辑计划（ID=" + editingPlanId + "），有未保存的修改");
        }
    }
    
    /**
     * 检查是否有未保存的修改
     */
    private boolean checkUnsavedChanges() {
        if (hasUnsavedChanges && editingPlanId != -1) {
            int result = JOptionPane.showConfirmDialog(this,
                "当前有未保存的修改，确定要放弃修改吗？",
                "确认放弃修改",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            return result == JOptionPane.YES_OPTION;
        }
        return true;
    }
    
    /**
     * 根据计划ID查找表格中的行索引
     */
    private int getRowIndexById(int planId) {
        for (int i = 0; i < currentPlans.size(); i++) {
            if (currentPlans.get(i).getId() == planId) {
                return i;
            }
        }
        return -1;
    }
    
    // 新增：为完成状态列设置复选框编辑器
    private void setupCompletionColumnEditor() {
        // 为第6列（完成状态列）设置复选框编辑器（调整索引）
        planTable.getColumnModel().getColumn(6).setCellEditor(
            new javax.swing.DefaultCellEditor(new JCheckBox())
        );
        
        // 设置列宽
        planTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // 日期
        planTable.getColumnModel().getColumn(1).setPreferredWidth(120); // 类型
        planTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // 计划时长
        planTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // 实际时长
        planTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // 强度
        planTable.getColumnModel().getColumn(5).setPreferredWidth(150); // 备注
        planTable.getColumnModel().getColumn(6).setPreferredWidth(60);  // 完成
    }
} 