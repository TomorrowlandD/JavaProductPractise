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

/**
 * 每日健康记录管理面板
 * 
 * 功能说明：
 * 1. 提供用户每日健康数据的录入界面（体重、运动、睡眠、心情等）
 * 2. 支持数据的增删改查操作
 * 3. 提供数据验证功能，确保数据合理性
 * 4. 支持管理员查看所有用户数据，普通用户只能查看自己的数据
 * 5. 提供历史记录的表格展示和编辑功能
 * 
 * 设计模式：
 * - 采用MVC模式，UI层负责界面展示和用户交互
 * - 通过DatabaseManager进行数据持久化
 * - 使用事件驱动编程处理用户操作
 * 
 * @author 健康管理系统开发团队
 * @version 1.0
 */
public class DailyRecordPanel extends JPanel {
    
    // ==================== 界面组件字段 ====================
    
    /** 日期输入框 - 格式：yyyy-MM-dd */
    private JTextField dateField;
    
    /** 体重输入框 - 单位：kg */
    private JTextField weightField;
    
    /** 运动内容输入框 - 描述用户进行的运动项目 */
    private JTextField exerciseField;
    
    /** 运动时长输入框 - 单位：小时 */
    private JTextField exerciseDurationField;
    
    /** 睡眠时长输入框 - 单位：小时 */
    private JTextField sleepDurationField;
    
    /** 心情选择下拉框 - 预设选项：愉快、一般、疲惫、压力大、沮丧 */
    private JComboBox<String> moodBox;
    
    /** 备注文本区域 - 用户可输入额外的健康备注信息 */
    private JTextArea noteArea;
    
    /** 保存按钮 - 用于保存或更新每日记录 */
    private JButton saveButton;
    
    /** 清空按钮 - 清空表单所有输入内容 */
    private JButton clearButton;
    
    /** 删除按钮 - 删除选中的历史记录 */
    private JButton deleteButton;
    
    /** 历史记录表格 - 显示用户的所有每日记录 */
    private JTable recordTable;
    
    /** 表格数据模型 - 管理表格的数据和结构 */
    private DefaultTableModel tableModel;
    
    /** 当前编辑的记录ID - -1表示新增，>0表示编辑现有记录 */
    private Integer editingRecordId = -1;
    
    /** 用户选择下拉框 - 管理员可选择不同用户，普通用户只能看到自己 */
    private JComboBox<UserProfile> userComboBox;
    
    /** 刷新按钮 - 刷新用户列表和表格数据 */
    private JButton refreshBtn;

    /**
     * 构造函数 - 初始化每日记录管理面板
     * 
     * 初始化流程：
     * 1. 设置面板布局
     * 2. 创建并布局所有界面组件
     * 3. 绑定事件监听器
     * 4. 初始化数据
     */
    public DailyRecordPanel() {
        // 设置面板布局为边界布局
        setLayout(new BorderLayout());
        
        // 创建表单面板，使用网格包布局实现复杂的表单布局
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  // 设置组件间距
        gbc.fill = GridBagConstraints.HORIZONTAL;  // 水平填充
        gbc.gridx = 0; gbc.gridy = 0;  // 起始位置

        // ==================== 用户选择区域 ====================
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.add(new JLabel("选择用户:"));
        userComboBox = new JComboBox<>();
        refreshBtn = new JButton("刷新");
        userPanel.add(userComboBox);
        userPanel.add(refreshBtn);
        formPanel.add(userPanel, gbc);

        // ==================== 日期输入区域 ====================
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("日期(yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(10);
        dateField.setText(LocalDate.now().toString());  // 默认设置为今天
        formPanel.add(dateField, gbc);

        // ==================== 体重输入区域 ====================
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("体重(kg):"), gbc);
        gbc.gridx = 1;
        weightField = new JTextField(10);
        formPanel.add(weightField, gbc);

        // ==================== 运动内容输入区域 ====================
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("运动内容:"), gbc);
        gbc.gridx = 1;
        exerciseField = new JTextField(15);
        formPanel.add(exerciseField, gbc);

        // ==================== 运动时长输入区域 ====================
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("运动时长(小时):"), gbc);
        gbc.gridx = 1;
        exerciseDurationField = new JTextField(10);
        formPanel.add(exerciseDurationField, gbc);

        // ==================== 睡眠时长输入区域 ====================
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("睡眠时长(小时):"), gbc);
        gbc.gridx = 1;
        sleepDurationField = new JTextField(10);
        formPanel.add(sleepDurationField, gbc);

        // ==================== 心情选择区域 ====================
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("心情:"), gbc);
        gbc.gridx = 1;
        // 预设心情选项，便于用户快速选择
        moodBox = new JComboBox<>(new String[]{"愉快", "一般", "疲惫", "压力大", "沮丧"});
        formPanel.add(moodBox, gbc);

        // ==================== 备注输入区域 ====================
        gbc.gridx = 0; gbc.gridy++;
        formPanel.add(new JLabel("备注:"), gbc);
        gbc.gridx = 1;
        noteArea = new JTextArea(3, 15);
        JScrollPane noteScroll = new JScrollPane(noteArea);  // 添加滚动条支持
        formPanel.add(noteScroll, gbc);

        // ==================== 操作按钮区域 ====================
        JPanel buttonPanel = new JPanel();
        saveButton = new JButton("保存");
        clearButton = new JButton("清空");
        deleteButton = new JButton("删除");
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteButton);

        // 将表单和按钮合并到主面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(formPanel);
        mainPanel.add(buttonPanel);
        add(mainPanel, BorderLayout.NORTH);

        // ==================== 历史记录表格区域 ====================
        // 定义表格列名
        String[] columns = {"ID", "用户名", "日期", "体重", "运动", "运动时长", "睡眠时长", "心情", "备注"};
        // 创建不可编辑的表格模型
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return false;  // 表格不可直接编辑，需要通过表单编辑
            }
        };
        recordTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(recordTable);
        tableScroll.setPreferredSize(new Dimension(700, 180));
        add(tableScroll, BorderLayout.CENTER);

        // ==================== 事件监听器绑定 ====================
        // 绑定按钮点击事件
        saveButton.addActionListener(this::onSave);
        clearButton.addActionListener(e -> clearForm());
        deleteButton.addActionListener(e -> onDelete());
        
        // 绑定表格选择事件 - 当用户选择表格行时，自动填充表单
        recordTable.getSelectionModel().addListSelectionListener(e -> onTableSelect());
        
        // 绑定用户切换事件 - 当用户切换时自动刷新表格数据
        userComboBox.addActionListener(e -> refreshTable());
        
        // 绑定刷新按钮事件
        refreshBtn.addActionListener(e -> refreshUserComboBox());

        // ==================== 初始化数据 ====================
        // 初始化用户列表和表格数据
        refreshUserComboBox();
    }

    /**
     * 保存按钮点击事件处理方法
     * 
     * 执行流程：
     * 1. 读取界面输入的所有数据
     * 2. 进行多重数据验证（用户选择、重复记录、日期范围、数值范围等）
     * 3. 创建DailyRecord数据对象
     * 4. 根据editingRecordId判断是新增还是更新操作
     * 5. 调用DatabaseManager保存数据
     * 6. 根据保存结果给出用户反馈
     * 7. 成功后清空表单并刷新表格
     * 
     * 数据验证规则：
     * - 必须选择用户
     * - 日期不能重复（同一用户同一日期只能有一条记录）
     * - 日期范围：2020-01-01 到 今天
     * - 运动时长和睡眠时长：0-24小时
     * - 所有数值字段必须为有效数字
     * 
     * @param e 按钮点击事件对象
     */
    private void onSave(ActionEvent e) {
        try {
            // ==================== 第一步：用户选择验证 ====================
            UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(this, "请先选择用户！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // ==================== 第二步：读取并验证日期数据 ====================
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            
            // 重复记录验证：检查该用户该日期是否已存在记录
            // editingRecordId为-1表示新增，>0表示编辑现有记录
            List<DailyRecord> records = DatabaseManager.getDailyRecordsByUser(selectedUser.getName());
            boolean exists = records.stream().anyMatch(r ->
                r.getDate().equals(date)
                && (editingRecordId == -1 || r.getId() != editingRecordId)
            );
            if (exists) {
                JOptionPane.showMessageDialog(this, "该用户该日期已存在每日记录，请勿重复添加", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 日期范围验证：不能记录未来的健康数据
            LocalDate minDate = LocalDate.of(2020, 1, 1);  // 最早记录日期
            LocalDate maxDate = LocalDate.now();  // 最晚记录日期（今天）
            if (date.isBefore(minDate)) {
                JOptionPane.showMessageDialog(this, "日期不能早于2020-01-01！", "日期错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (date.isAfter(maxDate)) {
                JOptionPane.showMessageDialog(this, "不能记录未来的健康数据！\n体重、睡眠等数据只能记录今天或过去的数据。", "日期错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // ==================== 第三步：读取并验证数值数据 ====================
            double weight = Double.parseDouble(weightField.getText().trim());
            String exercise = exerciseField.getText().trim();
            double exerciseDuration = Double.parseDouble(exerciseDurationField.getText().trim());
            double sleepDuration = Double.parseDouble(sleepDurationField.getText().trim());
            
            // 运动时长范围验证：0-24小时
            if (exerciseDuration < 0 || exerciseDuration > 24) {
                JOptionPane.showMessageDialog(this, "运动时长应在0~24小时之间！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // 睡眠时长范围验证：0-24小时
            if (sleepDuration < 0 || sleepDuration > 24) {
                JOptionPane.showMessageDialog(this, "睡眠时长应在0~24小时之间！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // ==================== 第四步：读取其他数据 ====================
            String mood = (String) moodBox.getSelectedItem();
            String note = noteArea.getText().trim();

            // ==================== 第五步：创建数据对象 ====================
            DailyRecord record = new DailyRecord();
            record.setUserName(selectedUser.getName());
            record.setDate(date);
            record.setWeight(weight);
            record.setExercise(exercise);
            record.setExerciseDuration(exerciseDuration);
            record.setSleepDuration(sleepDuration);
            record.setMood(mood);
            record.setNote(note);
            
            // ==================== 第六步：保存到数据库 ====================
            if (editingRecordId == -1) {
                // 新增记录操作
                boolean success = DatabaseManager.saveDailyRecord(record);
                if (success) {
                    JOptionPane.showMessageDialog(this, "保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();  // 清空表单
                    refreshTable();  // 刷新表格显示
                } else {
                    JOptionPane.showMessageDialog(this, "保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // 更新记录操作
                record.setId(editingRecordId);
                boolean success = DatabaseManager.updateDailyRecord(record);
                if (success) {
                    JOptionPane.showMessageDialog(this, "保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();  // 清空表单
                    refreshTable();  // 刷新表格显示
                } else {
                    JOptionPane.showMessageDialog(this, "保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (DateTimeParseException ex) {
            // 日期格式错误处理
            JOptionPane.showMessageDialog(this, "日期格式错误，应为yyyy-MM-dd！", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            // 数字格式错误处理
            JOptionPane.showMessageDialog(this, "体重、运动时长、睡眠时长应为数字！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 删除按钮点击事件处理方法
     * 
     * 执行流程：
     * 1. 检查是否选中了表格行
     * 2. 获取选中记录的ID
     * 3. 弹出确认对话框
     * 4. 用户确认后调用DatabaseManager删除记录
     * 5. 根据删除结果给出用户反馈
     * 6. 成功后清空表单并刷新表格
     */
    private void onDelete() {
        // 检查是否选中了表格行
        int row = recordTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的记录！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 获取选中记录的ID（第一列是隐藏的ID列）
        int id = (int) tableModel.getValueAt(row, 0);
        
        // 弹出确认对话框
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除该记录吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // 调用数据库删除方法
            boolean success = DatabaseManager.deleteDailyRecordById(id);
            if (success) {
                JOptionPane.showMessageDialog(this, "删除成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                clearForm();  // 清空表单
                refreshTable();  // 刷新表格显示
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 表格选择事件处理方法
     * 
     * 功能：当用户在表格中选择某一行时，自动将该行的数据填充到表单中
     * 这样用户就可以直接编辑现有的记录
     * 
     * 执行流程：
     * 1. 获取选中的表格行
     * 2. 如果没有选中行，清空表单并重置编辑状态
     * 3. 如果选中了行，将表格数据填充到表单中
     * 4. 设置editingRecordId为当前记录的ID，表示进入编辑模式
     */
    private void onTableSelect() {
        int row = recordTable.getSelectedRow();
        if (row == -1) {
            // 没有选中行，清空表单并重置编辑状态
            editingRecordId = -1;
            clearForm();
            return;
        }
        
        // 获取选中记录的ID
        editingRecordId = (int) tableModel.getValueAt(row, 0);
        
        // 将表格数据填充到表单中
        // 注意：表格列的顺序是：ID(0), 用户名(1), 日期(2), 体重(3), 运动(4), 运动时长(5), 睡眠时长(6), 心情(7), 备注(8)
        dateField.setText(tableModel.getValueAt(row, 2).toString());
        weightField.setText(tableModel.getValueAt(row, 3).toString());
        exerciseField.setText(tableModel.getValueAt(row, 4).toString());
        exerciseDurationField.setText(tableModel.getValueAt(row, 5).toString());
        sleepDurationField.setText(tableModel.getValueAt(row, 6).toString());
        moodBox.setSelectedItem(tableModel.getValueAt(row, 7).toString());
        noteArea.setText(tableModel.getValueAt(row, 8).toString());
    }

    /**
     * 刷新表格数据
     * 
     * 功能：从数据库重新获取当前选中用户的每日记录，并更新表格显示
     * 
     * 执行流程：
     * 1. 获取当前选中的用户
     * 2. 调用DatabaseManager获取该用户的所有每日记录
     * 3. 清空表格现有数据
     * 4. 将数据库记录逐行添加到表格中
     * 5. 隐藏ID列（第一列）
     */
    private void refreshTable() {
        // 获取当前选中的用户
        UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
        List<DailyRecord> records;
        
        if (selectedUser != null) {
            // 从数据库获取该用户的所有每日记录
            records = DatabaseManager.getDailyRecordsByUser(selectedUser.getName());
        } else {
            // 如果没有选中用户，显示空列表
            records = new java.util.ArrayList<>();
        }
        
        // 清空表格现有数据
        tableModel.setRowCount(0);
        
        // 将数据库记录逐行添加到表格中
        for (DailyRecord r : records) {
            tableModel.addRow(new Object[]{
                r.getId(),           // ID列（隐藏）
                r.getUserName() != null ? r.getUserName() : "(无)",  // 用户名
                r.getDate(),         // 日期
                r.getWeight(),       // 体重
                r.getExercise(),     // 运动内容
                r.getExerciseDuration(), // 运动时长
                r.getSleepDuration(),    // 睡眠时长
                r.getMood(),         // 心情
                r.getNote()          // 备注
            });
        }
        
        // 隐藏ID列（第一列），因为ID对用户来说没有意义
        if (recordTable.getColumnModel().getColumnCount() > 0) {
            recordTable.getColumnModel().getColumn(0).setMinWidth(0);
            recordTable.getColumnModel().getColumn(0).setMaxWidth(0);
            recordTable.getColumnModel().getColumn(0).setWidth(0);
        }
    }

    /**
     * 刷新用户下拉框
     * 
     * 功能：根据当前用户权限重新加载用户列表
     * - 管理员可以看到所有用户
     * - 普通用户只能看到自己的档案
     * 
     * 执行流程：
     * 1. 保存当前选中的用户（用于恢复选择状态）
     * 2. 清空用户下拉框
     * 3. 根据用户权限加载不同的用户列表
     * 4. 恢复之前选中的用户
     * 5. 根据是否有用户来控制保存按钮的启用状态
     * 6. 刷新表格数据
     */
    private void refreshUserComboBox() {
        // 保存当前选中的用户，用于恢复选择状态
        UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
        String selectedUserName = selectedUser != null ? selectedUser.getName() : null;
        
        // 清空用户下拉框
        userComboBox.removeAllItems();
        
        if (service.SessionManager.isAdmin()) {
            // 管理员可以看到所有用户
            List<UserProfile> userList = DatabaseManager.getAllUserProfiles();
            for (UserProfile user : userList) {
                userComboBox.addItem(user);
            }
            userComboBox.setEnabled(true);  // 管理员可以选择不同用户
        } else {
            // 普通用户只能看到自己的档案
            UserProfile currentUserProfile = service.SessionManager.getCurrentProfile();
            if (currentUserProfile != null) {
                userComboBox.addItem(currentUserProfile);
                userComboBox.setSelectedItem(currentUserProfile);
                userComboBox.setEnabled(false);  // 普通用户不能切换用户
            }
        }
        
        // 恢复之前选中的用户（仅管理员）
        if (service.SessionManager.isAdmin() && selectedUserName != null) {
            for (int i = 0; i < userComboBox.getItemCount(); i++) {
                UserProfile user = userComboBox.getItemAt(i);
                if (user.getName().equals(selectedUserName)) {
                    userComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else if (service.SessionManager.isAdmin() && userComboBox.getItemCount() > 0) {
            // 如果没有之前选中的用户，选择第一个
            userComboBox.setSelectedIndex(0);
        }
        
        // 根据是否有用户来控制保存按钮的启用状态
        if (userComboBox.getItemCount() == 0) {
            saveButton.setEnabled(false);  // 没有用户时禁用保存按钮
        } else {
            saveButton.setEnabled(true);   // 有用户时启用保存按钮
        }
        
        // 刷新表格数据
        refreshTable();
    }

    /**
     * 清空表单
     * 
     * 功能：清空所有输入框的内容，重置为默认状态
     * 通常在以下情况调用：
     * - 保存成功后
     * - 删除成功后
     * - 用户点击清空按钮
     * - 表格选择变化时
     */
    protected void clearForm() {
        dateField.setText(LocalDate.now().toString());  // 日期重置为今天
        weightField.setText("");                        // 清空体重
        exerciseField.setText("");                      // 清空运动内容
        exerciseDurationField.setText("");              // 清空运动时长
        sleepDurationField.setText("");                 // 清空睡眠时长
        moodBox.setSelectedIndex(0);                    // 心情重置为第一个选项
        noteArea.setText("");                           // 清空备注
        editingRecordId = -1;                           // 重置编辑状态为新增模式
        recordTable.clearSelection();                   // 清除表格选择
    }
} 