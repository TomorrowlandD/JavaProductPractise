package ui.panels;

import model.DailyRecord;
import model.UserProfile;
import service.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class DailyRecordPanel extends JPanel {
    private JTextField dateField;
    private JTextField weightField;
    private JTextField exerciseField;
    private JTextField exerciseDurationField;
    private JTextField sleepDurationField;
    private JComboBox<String> moodBox;
    private JTextArea noteArea;
    private JButton saveButton;
    private JButton clearButton;
    private JTable recordTable;
    private DefaultTableModel tableModel;
    private JButton deleteButton;
    private Integer editingRecordId = -1;
    private JComboBox<UserProfile> userComboBox;
    private JButton refreshBtn;

    public DailyRecordPanel() {
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        // 用户选择区域
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.add(new JLabel("选择用户:"));
        userComboBox = new JComboBox<>();
        refreshBtn = new JButton("刷新");
        userPanel.add(userComboBox);
        userPanel.add(refreshBtn);
        formPanel.add(userPanel, gbc);

        // 日期
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("日期(yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(10);
        dateField.setText(LocalDate.now().toString());
        formPanel.add(dateField, gbc);

        // 体重
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("体重(kg):"), gbc);
        gbc.gridx = 1;
        weightField = new JTextField(10);
        formPanel.add(weightField, gbc);

        // 运动内容
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("运动内容:"), gbc);
        gbc.gridx = 1;
        exerciseField = new JTextField(15);
        formPanel.add(exerciseField, gbc);

        // 运动时长
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("运动时长(小时):"), gbc);
        gbc.gridx = 1;
        exerciseDurationField = new JTextField(10);
        formPanel.add(exerciseDurationField, gbc);

        // 睡眠时长
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("睡眠时长(小时):"), gbc);
        gbc.gridx = 1;
        sleepDurationField = new JTextField(10);
        formPanel.add(sleepDurationField, gbc);

        // 心情
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("心情:"), gbc);
        gbc.gridx = 1;
        moodBox = new JComboBox<>(new String[]{"愉快", "一般", "疲惫", "压力大", "沮丧"});
        formPanel.add(moodBox, gbc);

        // 备注
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("备注:"), gbc);
        gbc.gridx = 1;
        noteArea = new JTextArea(3, 15);
        JScrollPane noteScroll = new JScrollPane(noteArea);
        formPanel.add(noteScroll, gbc);

        // 按钮
        JPanel buttonPanel = new JPanel();
        saveButton = new JButton("保存");
        clearButton = new JButton("清空");
        deleteButton = new JButton("删除");
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteButton);

        // 表单和按钮合并到mainPanel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(formPanel);
        mainPanel.add(buttonPanel);
        add(mainPanel, BorderLayout.NORTH);

        // 历史记录表格
        String[] columns = {"ID", "用户名", "日期", "体重", "运动", "运动时长", "睡眠时长", "心情", "备注"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        recordTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(recordTable);
        tableScroll.setPreferredSize(new Dimension(700, 180));
        add(tableScroll, BorderLayout.CENTER);

        // 事件处理
        saveButton.addActionListener(this::onSave);
        clearButton.addActionListener(e -> clearForm());
        deleteButton.addActionListener(e -> onDelete());
        recordTable.getSelectionModel().addListSelectionListener(e -> onTableSelect());
        
        // 用户切换时刷新表格
        userComboBox.addActionListener(e -> refreshTable());
        
        // 刷新按钮事件
        refreshBtn.addActionListener(e -> refreshUserComboBox());

        // 初始化用户列表和表格数据
        refreshUserComboBox();
    }

    private void onSave(ActionEvent e) {
        try {
            // 校验是否选择了用户
            UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(this, "请先选择用户！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            // 优化唯一性校验：只查当前用户的记录，editingRecordId为-1表示新增
            List<DailyRecord> records = DatabaseManager.getDailyRecordsByUser(selectedUser.getName());
            boolean exists = records.stream().anyMatch(r ->
                r.getDate().equals(date)
                && (editingRecordId == -1 || r.getId() != editingRecordId)
            );
            if (exists) {
                JOptionPane.showMessageDialog(this, "该用户该日期已存在每日记录，请勿重复添加", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // 日期合理性校验 - 每日记录不能记录未来的数据
            LocalDate minDate = LocalDate.of(2020, 1, 1);
            LocalDate maxDate = LocalDate.now(); // 修改：只能记录今天或过去的数据
            if (date.isBefore(minDate)) {
                JOptionPane.showMessageDialog(this, "日期不能早于2020-01-01！", "日期错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (date.isAfter(maxDate)) {
                JOptionPane.showMessageDialog(this, "不能记录未来的健康数据！\n体重、睡眠等数据只能记录今天或过去的数据。", "日期错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double weight = Double.parseDouble(weightField.getText().trim());
            String exercise = exerciseField.getText().trim();
            double exerciseDuration = Double.parseDouble(exerciseDurationField.getText().trim());
            double sleepDuration = Double.parseDouble(sleepDurationField.getText().trim());
            // 运动时长/睡眠时长范围校验
            if (exerciseDuration < 0 || exerciseDuration > 24) {
                JOptionPane.showMessageDialog(this, "运动时长应在0~24小时之间！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (sleepDuration < 0 || sleepDuration > 24) {
                JOptionPane.showMessageDialog(this, "睡眠时长应在0~24小时之间！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String mood = (String) moodBox.getSelectedItem();
            String note = noteArea.getText().trim();

            DailyRecord record = new DailyRecord();
            // 设置用户名
            record.setUserName(selectedUser.getName());
            record.setDate(date);
            record.setWeight(weight);
            record.setExercise(exercise);
            record.setExerciseDuration(exerciseDuration);
            record.setSleepDuration(sleepDuration);
            record.setMood(mood);
            record.setNote(note);
            if (editingRecordId == -1) {
                // 新增
                boolean success = DatabaseManager.saveDailyRecord(record);
                if (success) {
                    JOptionPane.showMessageDialog(this, "保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // 更新
                record.setId(editingRecordId);
                boolean success = DatabaseManager.updateDailyRecord(record);
                if (success) {
                    JOptionPane.showMessageDialog(this, "保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "日期格式错误，应为yyyy-MM-dd！", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "体重、运动时长、睡眠时长应为数字！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int row = recordTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的记录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0); // 隐藏的ID列
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除该记录吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = DatabaseManager.deleteDailyRecordById(id);
            if (success) {
                JOptionPane.showMessageDialog(this, "删除成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onTableSelect() {
        int row = recordTable.getSelectedRow();
        if (row == -1) {
            editingRecordId = -1;
            clearForm();
            return;
        }
        editingRecordId = (int) tableModel.getValueAt(row, 0);
        dateField.setText(tableModel.getValueAt(row, 2).toString());
        weightField.setText(tableModel.getValueAt(row, 3).toString());
        exerciseField.setText(tableModel.getValueAt(row, 4).toString());
        exerciseDurationField.setText(tableModel.getValueAt(row, 5).toString());
        sleepDurationField.setText(tableModel.getValueAt(row, 6).toString());
        moodBox.setSelectedItem(tableModel.getValueAt(row, 7).toString());
        noteArea.setText(tableModel.getValueAt(row, 8).toString());
    }

    private void refreshTable() {
        UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
        List<DailyRecord> records;
        if (selectedUser != null) {
            records = DatabaseManager.getDailyRecordsByUser(selectedUser.getName());
        } else {
            records = new java.util.ArrayList<>();
        }
        
        tableModel.setRowCount(0);
        for (DailyRecord r : records) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getUserName() != null ? r.getUserName() : "(无)",
                r.getDate(),
                r.getWeight(),
                r.getExercise(),
                r.getExerciseDuration(),
                r.getSleepDuration(),
                r.getMood(),
                r.getNote()
            });
        }
        // 隐藏ID列
        if (recordTable.getColumnModel().getColumnCount() > 0) {
            recordTable.getColumnModel().getColumn(0).setMinWidth(0);
            recordTable.getColumnModel().getColumn(0).setMaxWidth(0);
            recordTable.getColumnModel().getColumn(0).setWidth(0);
        }
    }

    private void refreshUserComboBox() {
        UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
        String selectedUserName = selectedUser != null ? selectedUser.getName() : null;
        userComboBox.removeAllItems();
        if (service.SessionManager.isAdmin()) {
            List<UserProfile> userList = DatabaseManager.getAllUserProfiles();
            for (UserProfile user : userList) {
                userComboBox.addItem(user);
            }
            userComboBox.setEnabled(true);
        } else {
            UserProfile currentUserProfile = service.SessionManager.getCurrentProfile();
            if (currentUserProfile != null) {
                userComboBox.addItem(currentUserProfile);
                userComboBox.setSelectedItem(currentUserProfile);
                userComboBox.setEnabled(false);
            }
        }
        if (service.SessionManager.isAdmin() && selectedUserName != null) {
            for (int i = 0; i < userComboBox.getItemCount(); i++) {
                UserProfile user = userComboBox.getItemAt(i);
                if (user.getName().equals(selectedUserName)) {
                    userComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else if (service.SessionManager.isAdmin() && userComboBox.getItemCount() > 0) {
            userComboBox.setSelectedIndex(0);
        }
        if (userComboBox.getItemCount() == 0) {
            saveButton.setEnabled(false);
        } else {
            saveButton.setEnabled(true);
        }
        refreshTable();
    }

    protected void clearForm() {
        dateField.setText(LocalDate.now().toString());
        weightField.setText("");
        exerciseField.setText("");
        exerciseDurationField.setText("");
        sleepDurationField.setText("");
        moodBox.setSelectedIndex(0);
        noteArea.setText("");
        editingRecordId = -1;
        recordTable.clearSelection();
    }
} 