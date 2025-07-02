package ui.panels;

import model.DietRecord;
import model.UserProfile;
import service.DatabaseManager;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 饮食管理面板（重构版）
 * 体验与运动计划面板完全一致
 */
public class DietPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // 顶部：用户选择与操作按钮
    private JComboBox<UserProfile> userComboBox;
    private JTextField dateField;
    private JButton addDietButton;
    private JButton deleteDietButton;
    private JButton cancelEditButton;
    private JButton saveButton;
    private JButton refreshBtn;

    // 中部：三餐内容录入
    private JPanel breakfastPanel, lunchPanel, dinnerPanel;
    private JCheckBox[] breakfastChecks, lunchChecks, dinnerChecks;
    private JTextField breakfastOther, lunchOther, dinnerOther;
    private JCheckBox breakfastNone, lunchNone, dinnerNone;
    private JTextArea notesArea;

    // 状态显示
    private JLabel statusLabel;

    // 底部：饮食记录表格与统计
    private JTable dietTable;
    private DietTableModel dietTableModel;
    private JLabel statsLabel;
    private java.util.List<DietRecord> currentRecords = new java.util.ArrayList<>();
    private int editingDietId = -1;

    // 其它
    private static final String[] COMMON_FOODS = {
        "米饭", "面条", "馒头", "面包", "鸡蛋", "牛奶", "豆浆", "蔬菜", "水果", "肉类", "鱼类", "豆制品", "坚果", "粥"
    };

    public DietPanel() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshUserComboBox();
    }

    private void initializeComponents() {
        // 顶部
        userComboBox = new JComboBox<>();
        refreshBtn = new JButton("刷新");
        dateField = new JTextField(10);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        addDietButton = new JButton("新增饮食");
        deleteDietButton = new JButton("删除");
        cancelEditButton = new JButton("取消编辑");
        saveButton = new JButton("保存");

        // 三餐内容
        breakfastChecks = new JCheckBox[COMMON_FOODS.length];
        for (int i = 0; i < COMMON_FOODS.length; i++) breakfastChecks[i] = new JCheckBox(COMMON_FOODS[i]);
        breakfastOther = new JTextField(8);
        breakfastNone = new JCheckBox("无安排");
        lunchChecks = new JCheckBox[COMMON_FOODS.length];
        for (int i = 0; i < COMMON_FOODS.length; i++) lunchChecks[i] = new JCheckBox(COMMON_FOODS[i]);
        lunchOther = new JTextField(8);
        lunchNone = new JCheckBox("无安排");
        dinnerChecks = new JCheckBox[COMMON_FOODS.length];
        for (int i = 0; i < COMMON_FOODS.length; i++) dinnerChecks[i] = new JCheckBox(COMMON_FOODS[i]);
        dinnerOther = new JTextField(8);
        dinnerNone = new JCheckBox("无安排");
        notesArea = new JTextArea(2, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setText("无特殊备注");
        statusLabel = new JLabel("准备就绪");
    }

    // 饮食记录表格模型
    private static class DietTableModel extends javax.swing.table.AbstractTableModel {
        private final String[] columns = {"日期", "早餐", "午餐", "晚餐", "备注"};
        private java.util.List<DietRecord> records = new java.util.ArrayList<>();
        public void setRecords(java.util.List<DietRecord> records) {
            this.records = records;
            fireTableDataChanged();
        }
        public DietRecord getRecordAt(int row) {
            return records.get(row);
        }
        @Override public int getRowCount() { return records.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }
        @Override public Object getValueAt(int row, int col) {
            DietRecord r = records.get(row);
            switch (col) {
                case 0: return r.getRecordDateString();
                case 1: return r.getBreakfast();
                case 2: return r.getLunch();
                case 3: return r.getDinner();
                case 4: return r.getNotes();
                default: return "";
            }
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部：用户选择+日期+操作按钮+保存
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBorder(new TitledBorder("用户选择"));
        topPanel.add(new JLabel("选择用户:"));
        topPanel.add(userComboBox);
        topPanel.add(refreshBtn);
        topPanel.add(new JLabel("日期:"));
        topPanel.add(dateField);
        topPanel.add(addDietButton);
        topPanel.add(deleteDietButton);
        topPanel.add(cancelEditButton);
        topPanel.add(saveButton);
        add(topPanel, BorderLayout.NORTH);

        // 中部：三餐内容录入+备注（加滚动条）
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new TitledBorder("录入饮食记录"));
        // 早餐
        breakfastPanel = createMealPanel("早餐", breakfastChecks, breakfastOther, breakfastNone);
        centerPanel.add(breakfastPanel);
        centerPanel.add(Box.createVerticalStrut(5));
        // 午餐
        lunchPanel = createMealPanel("午餐", lunchChecks, lunchOther, lunchNone);
        centerPanel.add(lunchPanel);
        centerPanel.add(Box.createVerticalStrut(5));
        // 晚餐
        dinnerPanel = createMealPanel("晚餐", dinnerChecks, dinnerOther, dinnerNone);
        centerPanel.add(dinnerPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        // 备注
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.add(new JLabel("备注:"), BorderLayout.WEST);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setPreferredSize(new Dimension(0, 60));
        notesPanel.add(notesScrollPane, BorderLayout.CENTER);
        centerPanel.add(notesPanel);
        // 用滚动面板包裹centerPanel
        JScrollPane centerScroll = new JScrollPane(centerPanel);
        centerScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(centerScroll, BorderLayout.CENTER);

        // 底部：表格+统计
        dietTableModel = new DietTableModel(); // 占位，后续补全
        dietTable = new JTable(dietTableModel);
        dietTable.setRowHeight(24);
        dietTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dietTable.setSelectionBackground(new Color(100, 150, 255));
        dietTable.setSelectionForeground(Color.WHITE);
        dietTable.setShowGrid(false);
        statsLabel = new JLabel("统计：");
        dietTable.setFillsViewportHeight(true);
        JScrollPane tableScroll = new JScrollPane(dietTable);
        tableScroll.setPreferredSize(new Dimension(0, 180));
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(tableScroll);
        bottomPanel.add(statsLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 创建每餐的面板
    private JPanel createMealPanel(String mealName, JCheckBox[] checks, JTextField otherField, JCheckBox noneBox) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder(mealName));
        // 第一行：常见食物复选框
        JPanel foodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        for (JCheckBox cb : checks) foodPanel.add(cb);
        panel.add(foodPanel);
        // 第二行：其它+无安排
        JPanel otherPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        otherPanel.add(new JLabel("其它:"));
        otherPanel.add(otherField);
        otherPanel.add(noneBox);
        panel.add(otherPanel);
        return panel;
    }

    private void setupEventHandlers() {
        // 新增饮食按钮
        addDietButton.addActionListener(e -> {
            clearForm();
            dietTable.clearSelection();
        });
        // 删除按钮
        deleteDietButton.addActionListener(e -> deleteCurrentRecord());
        // 取消编辑按钮
        cancelEditButton.addActionListener(e -> {
            clearForm();
            dietTable.clearSelection();
        });
        // 保存按钮
        saveButton.addActionListener(e -> saveDietRecord());
        // 用户切换时刷新表格
        userComboBox.addActionListener(e -> refreshDietTable());
        // 表格单选，点击行填充表单
        dietTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && dietTable.getSelectedRow() != -1) {
                int row = dietTable.getSelectedRow();
                if (row >= 0 && row < dietTableModel.getRowCount()) {
                    DietRecord record = dietTableModel.getRecordAt(row);
                    fillFormWithRecord(record);
                }
            }
        });
        // 双击表格行取消选中
        dietTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    dietTable.clearSelection();
                    clearForm();
                }
            }
        });
        // 点击表单区取消表格选中
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dietTable.clearSelection();
                clearForm();
            }
        });

        // 早餐无安排联动
        breakfastNone.addActionListener(e -> handleNoneBox(breakfastNone, breakfastChecks, breakfastOther));
        for (JCheckBox cb : breakfastChecks) {
            cb.addActionListener(e -> handleAnyCheckOrOther(breakfastNone, breakfastChecks, breakfastOther));
        }
        breakfastOther.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { handleAnyCheckOrOther(breakfastNone, breakfastChecks, breakfastOther); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { handleAnyCheckOrOther(breakfastNone, breakfastChecks, breakfastOther); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { handleAnyCheckOrOther(breakfastNone, breakfastChecks, breakfastOther); }
        });
        // 午餐无安排联动
        lunchNone.addActionListener(e -> handleNoneBox(lunchNone, lunchChecks, lunchOther));
        for (JCheckBox cb : lunchChecks) {
            cb.addActionListener(e -> handleAnyCheckOrOther(lunchNone, lunchChecks, lunchOther));
        }
        lunchOther.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { handleAnyCheckOrOther(lunchNone, lunchChecks, lunchOther); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { handleAnyCheckOrOther(lunchNone, lunchChecks, lunchOther); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { handleAnyCheckOrOther(lunchNone, lunchChecks, lunchOther); }
        });
        // 晚餐无安排联动
        dinnerNone.addActionListener(e -> handleNoneBox(dinnerNone, dinnerChecks, dinnerOther));
        for (JCheckBox cb : dinnerChecks) {
            cb.addActionListener(e -> handleAnyCheckOrOther(dinnerNone, dinnerChecks, dinnerOther));
        }
        dinnerOther.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { handleAnyCheckOrOther(dinnerNone, dinnerChecks, dinnerOther); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { handleAnyCheckOrOther(dinnerNone, dinnerChecks, dinnerOther); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { handleAnyCheckOrOther(dinnerNone, dinnerChecks, dinnerOther); }
        });

        // 刷新按钮
        refreshBtn.addActionListener(e -> refreshUserComboBox());
    }

    // 勾选"无安排"时禁用其它选项，取消时恢复
    private void handleNoneBox(JCheckBox noneBox, JCheckBox[] checks, JTextField otherField) {
        boolean selected = noneBox.isSelected();
        for (JCheckBox cb : checks) {
            cb.setEnabled(!selected);
            if (selected) cb.setSelected(false);
        }
        otherField.setEnabled(!selected);
        if (selected) otherField.setText("");
    }
    // 只要其它多选框或"其它"有内容，自动取消"无安排"
    private void handleAnyCheckOrOther(JCheckBox noneBox, JCheckBox[] checks, JTextField otherField) {
        boolean anyChecked = false;
        for (JCheckBox cb : checks) if (cb.isSelected()) anyChecked = true;
        boolean hasOther = !otherField.getText().trim().isEmpty();
        if (anyChecked || hasOther) {
            noneBox.setSelected(false);
            noneBox.setEnabled(true);
            for (JCheckBox cb : checks) cb.setEnabled(true);
            otherField.setEnabled(true);
        }
    }

    private void refreshUserComboBox() {
        UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
        String selectedUserName = selectedUser != null ? selectedUser.getName() : null;
        if (service.SessionManager.isAdmin()) {
            List<UserProfile> userList = DatabaseManager.getAllUserProfiles();
            userComboBox.setModel(new DefaultComboBoxModel<>(userList.toArray(new UserProfile[0])));
            userComboBox.setEnabled(true);
            if (selectedUserName != null) {
                for (int i = 0; i < userComboBox.getItemCount(); i++) {
                    UserProfile user = userComboBox.getItemAt(i);
                    if (user.getName().equals(selectedUserName)) {
                        userComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else if (!userList.isEmpty()) {
                userComboBox.setSelectedIndex(0);
            }
        } else {
            UserProfile currentUserProfile = service.SessionManager.getCurrentProfile();
            if (currentUserProfile != null) {
                List<UserProfile> userList = new java.util.ArrayList<>();
                userList.add(currentUserProfile);
                userComboBox.setModel(new DefaultComboBoxModel<>(userList.toArray(new UserProfile[0])));
                userComboBox.setSelectedItem(currentUserProfile);
                userComboBox.setEnabled(false);
            } else {
                userComboBox.setModel(new DefaultComboBoxModel<>(new UserProfile[0]));
                userComboBox.setEnabled(false);
            }
        }
        if (userComboBox.getItemCount() > 0) {
            refreshDietTable();
        } else {
            dietTableModel.setRecords(new java.util.ArrayList<>());
            statsLabel.setText("统计：暂无用户");
        }
    }

    // 清空表单
    private void clearForm() {
        for (JCheckBox cb : breakfastChecks) cb.setSelected(false);
        for (JCheckBox cb : lunchChecks) cb.setSelected(false);
        for (JCheckBox cb : dinnerChecks) cb.setSelected(false);
        breakfastOther.setText("");
        lunchOther.setText("");
        dinnerOther.setText("");
        breakfastNone.setSelected(false);
        lunchNone.setSelected(false);
        dinnerNone.setSelected(false);
        notesArea.setText("无特殊备注");
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        editingDietId = -1;
        saveButton.setText("保存");
        statusLabel.setText("表单已清空，请填写新饮食记录");
    }

    // 刷新表格
    private void refreshDietTable() {
        UserProfile selected = (UserProfile) userComboBox.getSelectedItem();
        if (selected != null) {
            currentRecords = DatabaseManager.getDietRecordsByUser(selected.getName());
        } else {
            currentRecords = new java.util.ArrayList<>();
        }
        dietTableModel.setRecords(currentRecords);
        updateStatsLabel();
    }

    // 填充表单（编辑模式）
    private void fillFormWithRecord(DietRecord record) {
        if (record == null) return;
        editingDietId = record.getId();
        // 早餐
        fillMealForm(breakfastChecks, breakfastOther, breakfastNone, record.getBreakfast());
        // 午餐
        fillMealForm(lunchChecks, lunchOther, lunchNone, record.getLunch());
        // 晚餐
        fillMealForm(dinnerChecks, dinnerOther, dinnerNone, record.getDinner());
        notesArea.setText(record.getNotes() != null ? record.getNotes() : "");
        dateField.setText(record.getRecordDateString());
        saveButton.setText("保存修改");
        statusLabel.setText("正在编辑饮食记录（ID=" + record.getId() + ")，修改后请点击保存修改");
    }

    // 辅助：填充每餐表单
    private void fillMealForm(JCheckBox[] checks, JTextField otherField, JCheckBox noneBox, String mealStr) {
        for (JCheckBox cb : checks) cb.setSelected(false);
        otherField.setText("");
        noneBox.setSelected(false);
        if (mealStr == null || mealStr.trim().isEmpty()) return;
        if ("无安排".equals(mealStr.trim())) {
            noneBox.setSelected(true);
            return;
        }
        String[] parts = mealStr.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.startsWith("其它:")) {
                otherField.setText(part.substring(3));
            } else {
                for (JCheckBox cb : checks) {
                    if (cb.getText().equals(part)) {
                        cb.setSelected(true);
                    }
                }
            }
        }
    }

    // 统计信息
    private void updateStatsLabel() {
        int total = currentRecords.size();
        statsLabel.setText("统计：共" + total + "条饮食记录");
    }

    // 删除饮食记录
    private void deleteCurrentRecord() {
        if (editingDietId == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要删除的饮食记录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, "确定要删除该饮食记录吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            if (DatabaseManager.deleteDietRecordById(editingDietId)) {
                JOptionPane.showMessageDialog(this, "删除成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshDietTable();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败，请重试", "删除失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 保存/修改饮食记录
    private void saveDietRecord() {
        UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this, "请先选择用户", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String dateText = dateField.getText().trim();
        LocalDate recordDate;
        try {
            recordDate = LocalDate.parse(dateText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "日期格式错误，请使用yyyy-MM-dd", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (recordDate.isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "日期不能晚于今天", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 校验是否已存在同一天的记录（新增时校验，编辑时排除自己）
        List<DietRecord> records = DatabaseManager.getDietRecordsByUser(selectedUser.getName());
        boolean exists = records.stream().anyMatch(r ->
            r.getRecordDate().equals(recordDate) && (editingDietId == -1 || r.getId() != editingDietId)
        );
        if (exists) {
            JOptionPane.showMessageDialog(this, "该用户该日期已存在饮食记录，请勿重复添加", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 组装DietRecord
        DietRecord record = new DietRecord();
        record.setUserName(selectedUser.getName());
        record.setRecordDate(recordDate);
        record.setBreakfast(collectMealString(breakfastChecks, breakfastOther, breakfastNone));
        record.setLunch(collectMealString(lunchChecks, lunchOther, lunchNone));
        record.setDinner(collectMealString(dinnerChecks, dinnerOther, dinnerNone));
        record.setNotes(notesArea.getText().trim());
        // 校验三餐
        if (!isMealValid(breakfastChecks, breakfastOther, breakfastNone)) {
            JOptionPane.showMessageDialog(this, "请为早餐选择食物或勾选'无安排'", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!isMealValid(lunchChecks, lunchOther, lunchNone)) {
            JOptionPane.showMessageDialog(this, "请为午餐选择食物或勾选'无安排'", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!isMealValid(dinnerChecks, dinnerOther, dinnerNone)) {
            JOptionPane.showMessageDialog(this, "请为晚餐选择食物或勾选'无安排'", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean success;
        if (editingDietId != -1) {
            record.setId(editingDietId);
            success = DatabaseManager.updateDietRecord(record);
        } else {
            success = DatabaseManager.insertDietRecord(record);
        }
        if (success) {
            JOptionPane.showMessageDialog(this, editingDietId != -1 ? "饮食记录修改成功！" : "饮食记录保存成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            refreshDietTable();
        } else {
            JOptionPane.showMessageDialog(this, "保存失败，请检查数据", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 辅助：收集每餐内容
    private String collectMealString(JCheckBox[] checks, JTextField otherField, JCheckBox noneBox) {
        if (noneBox.isSelected()) return "无安排";
        StringBuilder sb = new StringBuilder();
        for (JCheckBox cb : checks) {
            if (cb.isSelected()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(cb.getText());
            }
        }
        String other = otherField.getText().trim();
        if (!other.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(other); // 只拼接内容，不加"其它:"
        }
        return sb.toString();
    }

    // 辅助：校验每餐
    private boolean isMealValid(JCheckBox[] checks, JTextField otherField, JCheckBox noneBox) {
        if (noneBox.isSelected()) return true;
        for (JCheckBox cb : checks) {
            if (cb.isSelected()) return true;
        }
        return !otherField.getText().trim().isEmpty();
    }

    // ...后续将补全构造方法、初始化、布局、事件、表格模型等...
} 