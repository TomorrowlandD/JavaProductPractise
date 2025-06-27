package ui.panels;

import model.ExercisePlan;
import model.UserProfile;
import service.DatabaseManager;
import javax.swing.*;
import javax.swing.border.TitledBorder;
//import javax.swing.table.DefaultCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 运动计划管理面板
 * 提供简约朴素的运动计划制定和管理功能
 */
public class ExercisePlanPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // 用户选择组件
    private JComboBox<UserProfile> userComboBox;
    private JButton addPlanButton;
    private JButton deletePlanButton;
    private JButton cancelEditButton;
    
    // 计划制定组件
    private JPanel exerciseTypePanel;
    private JCheckBox[] exerciseTypeChecks;
    private JTextField dateField;
    private JTextField durationField;
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
        addPlanButton.setFont(defaultFont);
        deletePlanButton.setFont(defaultFont);
        cancelEditButton.setFont(defaultFont);
        for (JCheckBox cb : exerciseTypeChecks) cb.setFont(defaultFont);
        dateField.setFont(defaultFont);
        durationField.setFont(defaultFont);
        intensityBox.setFont(defaultFont);
        notesArea.setFont(defaultFont);
        saveButton.setFont(defaultFont);
        statusLabel.setFont(defaultFont);
        
        // 设置提示文本
        dateField.setToolTipText("请输入计划日期 (格式: yyyy-MM-dd)");
        durationField.setToolTipText("请输入所有运动的总时长 (小时)");
        notesArea.setToolTipText("可填写备注信息，如各项运动时间分配");
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
        panel.setBorder(new TitledBorder("制定新计划"));

        // 第一行：运动类型
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        typePanel.add(new JLabel("运动类型:"));
        typePanel.add(exerciseTypePanel);
        panel.add(typePanel);

        // 第二行：总运动时长
        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        durationPanel.add(new JLabel("总运动时长:"));
        durationPanel.add(durationField);
        durationPanel.add(new JLabel("小时"));
        panel.add(durationPanel);

        // 第三行：计划日期和运动强度
        JPanel dateIntensityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        dateIntensityPanel.add(new JLabel("计划日期:"));
        dateIntensityPanel.add(dateField);
        dateIntensityPanel.add(Box.createHorizontalStrut(20));
        dateIntensityPanel.add(new JLabel("运动强度:"));
        dateIntensityPanel.add(intensityBox);
        panel.add(dateIntensityPanel);

        // 第四行：备注
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.add(new JLabel("备注:"), BorderLayout.WEST);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setPreferredSize(new Dimension(0, 60));
        notesPanel.add(notesScrollPane, BorderLayout.CENTER);
        panel.add(notesPanel);

        // 第五行：保存按钮
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        savePanel.add(saveButton);
        panel.add(savePanel);

        return panel;
    }
    
    /**
     * 设置事件处理
     */
    private void setupEventHandlers() {
        // 用户选择事件
        userComboBox.addActionListener(e -> {
            UserProfile selected = (UserProfile) userComboBox.getSelectedItem();
            if (selected != null) {
                statusLabel.setText("已选择用户: " + selected.getName());
            } else {
                statusLabel.setText("请选择用户");
            }
            refreshPlanTable();
        });
        
        // 新增计划按钮
        addPlanButton.addActionListener(e -> clearForm());
        
        // 取消编辑按钮
        cancelEditButton.addActionListener(e -> {
            clearForm();
            planTable.clearSelection();
        });
        
        // 删除计划按钮
        deletePlanButton.addActionListener(e -> {
            if (editingPlanId != -1) {
                // 删除当前编辑的计划
                int result = JOptionPane.showConfirmDialog(this, 
                    "确定要删除当前编辑的计划吗？", "确认删除", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                
                if (result == JOptionPane.YES_OPTION) {
                    if (DatabaseManager.deleteExercisePlanById(editingPlanId)) {
                        JOptionPane.showMessageDialog(this, "计划删除成功！", "删除成功", JOptionPane.INFORMATION_MESSAGE);
                        clearForm();
                        refreshPlanTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "删除失败，请重试", "删除失败", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先选择要删除的计划（点击表格中的某一行）", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // 保存计划按钮
        saveButton.addActionListener(e -> {
            saveExercisePlan();
            refreshPlanTable();
        });
        
        // 日期字段回车键设置今天日期
        dateField.addActionListener(e -> {
            if (dateField.getText().trim().isEmpty()) {
                dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        });
        // 新增：表格行点击事件，自动填充表单
        planTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && planTable.getSelectedRow() != -1) {
                int row = planTable.getSelectedRow();
                if (row >= 0 && row < currentPlans.size()) {
                    // 检查是否有未保存的修改
                    if (!checkUnsavedChanges()) {
                        // 用户选择不放弃修改，恢复原来的选中状态
                        planTable.getSelectionModel().setSelectionInterval(
                            getRowIndexById(editingPlanId), 
                            getRowIndexById(editingPlanId)
                        );
                        return;
                    }
                    
                    ExercisePlan plan = currentPlans.get(row);
                    fillFormWithPlan(plan);
                }
            }
        });
        
        // 新增：双击表格行切换选中状态
        planTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    // 双击时取消选中
                    planTable.clearSelection();
                    clearForm();
                }
            }
        });
        
        // 新增：为表单组件添加变化监听器
        setupFormChangeListeners();
        
        // 新增：点击表单区域取消表格选中
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // 如果点击的是表单区域（不是表格区域），则取消表格选中
                if (e.getSource() == ExercisePlanPanel.this || 
                    e.getSource() instanceof JPanel || 
                    e.getSource() instanceof JTextField || 
                    e.getSource() instanceof JTextArea || 
                    e.getSource() instanceof JComboBox ||
                    e.getSource() instanceof JCheckBox) {
                    planTable.clearSelection();
                    clearForm();
                }
            }
        });
    }
    
    /**
     * 设置默认值
     */
    private void setupDefaults() {
        // 设置默认日期为今天
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // 设置默认时长
        durationField.setText("1.0");
        
        // 设置默认强度
        intensityBox.setSelectedItem("中");
        
        // 设置默认备注
        notesArea.setText("无特殊备注");
    }
    
    /**
     * 刷新用户下拉框
     */
    private void refreshUserComboBox() {
        List<UserProfile> userList = DatabaseManager.getAllUserProfiles();
        userComboBox.setModel(new DefaultComboBoxModel<>(userList.toArray(new UserProfile[0])));
        
        if (userList.isEmpty()) {
            statusLabel.setText("暂无用户，请先在用户档案中添加用户");
        } else {
            statusLabel.setText("请选择用户制定运动计划");
        }
    }
    
    /**
     * 清空表单
     */
    private void clearForm() {
        for (JCheckBox cb : exerciseTypeChecks) cb.setSelected(false);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        durationField.setText("1.0");
        intensityBox.setSelectedItem("中");
        notesArea.setText("无特殊备注");
        statusLabel.setText("表单已清空，请填写新计划");
        // 新增：重置编辑ID，恢复为新增模式
        editingPlanId = -1;
        saveButton.setText("保存计划");
        // 新增：清除表格选中状态
        planTable.clearSelection();
        // 新增：重置修改标记和原始数据
        hasUnsavedChanges = false;
        originalPlan = null;
    }
    
    /**
     * 保存运动计划
     */
    private void saveExercisePlan() {
        try {
            // 获取选中的用户
            UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(this, "请先选择用户", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 创建运动计划对象
            ExercisePlan plan = new ExercisePlan();
            plan.setUserName(selectedUser.getName());
            
            // 如果是编辑模式，设置ID
            if (editingPlanId != -1) {
                plan.setId(editingPlanId);
            }
            
            // 多选运动类型
            StringBuilder types = new StringBuilder();
            for (JCheckBox cb : exerciseTypeChecks) {
                if (cb.isSelected()) {
                    if (types.length() > 0) types.append(",");
                    types.append(cb.getText());
                }
            }
            if (types.length() == 0) {
                JOptionPane.showMessageDialog(this, "请至少选择一种运动类型", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            plan.setExerciseType(types.toString());
            
            // 解析日期
            String dateText = dateField.getText().trim();
            if (dateText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入计划日期", "提示", JOptionPane.WARNING_MESSAGE);
                dateField.requestFocus();
                return;
            }
            try {
                LocalDate planDate = LocalDate.parse(dateText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                plan.setPlanDate(planDate);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "日期格式错误，请使用 yyyy-MM-dd 格式", "提示", JOptionPane.WARNING_MESSAGE);
                dateField.requestFocus();
                return;
            }
            
            // 解析时长（必填校验）
            String durationText = durationField.getText().trim();
            if (durationText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入总运动时长", "提示", JOptionPane.WARNING_MESSAGE);
                durationField.requestFocus();
                return;
            }
            try {
                double duration = Double.parseDouble(durationText);
                if (duration <= 0 || duration > 24) {
                    JOptionPane.showMessageDialog(this, "运动时长必须在0-24小时之间", "提示", JOptionPane.WARNING_MESSAGE);
                    durationField.requestFocus();
                    return;
                }
                plan.setDuration(duration);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "请输入有效的运动时长", "提示", JOptionPane.WARNING_MESSAGE);
                durationField.requestFocus();
                return;
            }
            
            // 设置其他字段
            plan.setIntensity((String) intensityBox.getSelectedItem());
            plan.setNotes(notesArea.getText().trim());
            
            // 验证计划
            ExercisePlan.ValidationResult result = plan.validatePlan();
            if (!result.isValid()) {
                JOptionPane.showMessageDialog(this, "数据验证失败：" + result.getMessage(), "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 根据编辑状态选择保存或更新
            boolean success = false;
            String operationType = "";
            
            if (editingPlanId != -1) {
                // 编辑模式：更新现有计划
                success = DatabaseManager.updateExercisePlan(plan);
                operationType = "修改";
            } else {
                // 新增模式：插入新计划
                success = DatabaseManager.insertExercisePlan(plan);
                operationType = "保存";
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "运动计划" + operationType + "成功！\n\n" +
                    "用户：" + plan.getUserName() + "\n" +
                    "运动：" + plan.getExerciseType() + "\n" +
                    "日期：" + plan.getPlanDateString() + "\n" +
                    "时长：" + plan.getDurationString(), 
                    operationType + "成功", JOptionPane.INFORMATION_MESSAGE);
                
                clearForm();
                statusLabel.setText("计划已" + operationType + "，可继续制定新计划");
                // 新增：重置修改标记
                hasUnsavedChanges = false;
            } else {
                JOptionPane.showMessageDialog(this, operationType + "失败，请检查数据", "错误", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "保存失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 新增：将计划数据填充到表单（编辑模式）
    private void fillFormWithPlan(ExercisePlan plan) {
        // 运动类型多选
        String[] types = plan.getExerciseType().split(",");
        for (JCheckBox cb : exerciseTypeChecks) {
            cb.setSelected(false);
            for (String t : types) {
                if (cb.getText().equals(t.trim())) {
                    cb.setSelected(true);
                }
            }
        }
        // 其他字段
        dateField.setText(plan.getPlanDateString());
        durationField.setText(plan.getDuration() != null ? String.valueOf(plan.getDuration()) : "");
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
        originalPlan.setIntensity(plan.getIntensity());
        originalPlan.setNotes(plan.getNotes());
        originalPlan.setCompleted(plan.isCompleted());
        hasUnsavedChanges = false;
    }
    
    // 3. TableModel内部类
    private class PlanTableModel extends javax.swing.table.AbstractTableModel {
        private final String[] columns = {"日期", "类型", "总时长", "强度", "备注", "完成"};
        @Override public int getRowCount() { return currentPlans.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }
        @Override public Object getValueAt(int row, int col) {
            ExercisePlan plan = currentPlans.get(row);
            switch (col) {
                case 0: return plan.getPlanDateString();
                case 1: return plan.getExerciseType();
                case 2: return plan.getDurationString();
                case 3: return plan.getIntensity();
                case 4: return plan.getNotes();
                case 5: return plan.isCompleted();
                default: return "";
            }
        }
        @Override public Class<?> getColumnClass(int col) {
            if (col == 5) return Boolean.class;
            return String.class;
        }
        @Override public boolean isCellEditable(int row, int col) {
            return col == 5; // 只有完成状态列可编辑
        }
        @Override public void setValueAt(Object value, int row, int col) {
            if (col == 5 && row >= 0 && row < currentPlans.size()) {
                ExercisePlan plan = currentPlans.get(row);
                boolean newValue = (Boolean) value;
                if (plan.isCompleted() != newValue) {
                    plan.setCompleted(newValue);
                    // 立即更新数据库
                    if (DatabaseManager.updateExercisePlan(plan)) {
                        // 通过SwingUtilities.invokeLater确保在EDT中执行UI更新
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("完成状态已更新");
                            updateStatsLabel(); // 更新统计信息
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
        for (ExercisePlan plan : currentPlans) {
            if (plan.isCompleted()) completed++;
        }
        String rate = total > 0 ? String.format("%.0f%%", completed * 100.0 / total) : "0%";
        statsLabel.setText(String.format("统计：总计划%d条，已完成%d条，完成率%s", total, completed, rate));
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
        
        // 时长字段变化监听
        durationField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
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
        // 为第5列（完成状态列）设置复选框编辑器
        planTable.getColumnModel().getColumn(5).setCellEditor(
            new javax.swing.DefaultCellEditor(new JCheckBox())
        );
        
        // 添加表格数据变化监听器
        planTableModel.addTableModelListener(e -> {
            if (e.getColumn() == 5) { // 完成状态列
                int row = e.getFirstRow();
                if (row >= 0 && row < currentPlans.size()) {
                    ExercisePlan plan = currentPlans.get(row);
                    boolean newCompleted = (Boolean) planTableModel.getValueAt(row, 5);
                    
                    // 如果完成状态发生变化，更新数据库
                    if (plan.isCompleted() != newCompleted) {
                        plan.setCompleted(newCompleted);
                        if (DatabaseManager.updateExercisePlan(plan)) {
                            statusLabel.setText("完成状态已更新");
                            updateStatsLabel(); // 更新统计信息
                        } else {
                            // 更新失败，恢复原状态
                            plan.setCompleted(!newCompleted); // 恢复原状态
                            planTableModel.fireTableCellUpdated(row, 5); // 刷新表格显示
                            javax.swing.SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(ExercisePlanPanel.this, 
                                    "更新完成状态失败，请重试", "更新失败", JOptionPane.ERROR_MESSAGE);
                            });
                        }
                    }
                }
            }
        });
    }
} 