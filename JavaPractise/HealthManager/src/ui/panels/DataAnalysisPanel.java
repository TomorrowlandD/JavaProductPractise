package ui.panels;

import javax.swing.*;
import java.awt.*;
import model.UserProfile;
import service.DatabaseManager;
import java.util.List;
import model.DailyRecord;
import java.time.format.DateTimeFormatter;
//import java.time.temporal.ChronoUnit;
import java.util.Collections;
import model.ExercisePlan;
import java.util.Comparator;
import model.DietRecord;

public class DataAnalysisPanel extends JPanel {
    private JComboBox<String> statsTypeCombo;
    private JPanel cardsPanel;

    public DataAnalysisPanel() {
        setLayout(new BorderLayout(10, 10));

        // 顶部选择框
        statsTypeCombo = new JComboBox<>(new String[]{"健康统计", "运动统计", "饮食统计"});
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("请选择统计类型："));
        topPanel.add(statsTypeCombo);
        add(topPanel, BorderLayout.NORTH);

        // CardLayout主内容区
        cardsPanel = new JPanel(new CardLayout());
        cardsPanel.add(createHealthStatsPanel(), "健康统计");
        cardsPanel.add(createExerciseStatsPanel(), "运动统计");
        cardsPanel.add(createDietStatsPanel(), "饮食统计");
        add(cardsPanel, BorderLayout.CENTER);

        // 选择框切换事件
        statsTypeCombo.addActionListener(e -> {
            CardLayout cl = (CardLayout) (cardsPanel.getLayout());
            cl.show(cardsPanel, (String) statsTypeCombo.getSelectedItem());
        });
    }

    // 健康统计面板（带用户选择框、刷新按钮、统计展示和历史表格）
    private JPanel createHealthStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));

        // 用户选择下拉框和刷新按钮
        JComboBox<UserProfile> userComboBox;
        if (service.SessionManager.isAdmin()) {
            userComboBox = new JComboBox<>(DatabaseManager.getAllUserProfiles().toArray(new UserProfile[0]));
            userComboBox.setEnabled(true);
        } else {
            UserProfile currentUserProfile = service.SessionManager.getCurrentProfile();
            if (currentUserProfile != null) {
                userComboBox = new JComboBox<>(new UserProfile[]{currentUserProfile});
                userComboBox.setSelectedItem(currentUserProfile);
                userComboBox.setEnabled(false);
            } else {
                userComboBox = new JComboBox<>(new UserProfile[0]);
                userComboBox.setEnabled(false);
            }
        }
        
        JButton refreshBtn = new JButton("刷新");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("选择用户："));
        topPanel.add(userComboBox);
        topPanel.add(refreshBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);

        // 统计区（上方）
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        JLabel weightLabel = new JLabel();
        JLabel bmiLabel = new JLabel();
        JLabel changeLabel = new JLabel();
        JLabel originWeightLabel = new JLabel();
        JLabel originBmiLabel = new JLabel();
        statsPanel.add(weightLabel);
        statsPanel.add(bmiLabel);
        statsPanel.add(originWeightLabel);
        statsPanel.add(originBmiLabel);
        statsPanel.add(changeLabel);

        // 表格区
        String[] columns = {"日期", "体重(kg)", "身高(cm)", "BMI", "等级"};
        Object[][] tableData = {};
        JTable table = new JTable(tableData, columns);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(0, 200));

        // 垂直容器，统计区+表格区
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(statsPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(tableScroll);
        panel.add(centerPanel, BorderLayout.CENTER);

        // 刷新统计和表格
        Runnable refresh = () -> {
            UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
            if (selectedUser == null) {
                weightLabel.setText("暂无用户");
                bmiLabel.setText("");
                originWeightLabel.setText("");
                originBmiLabel.setText("");
                changeLabel.setText("");
                table.setModel(new javax.swing.table.DefaultTableModel(new Object[0][0], columns));
                return;
            }
            List<DailyRecord> records = DatabaseManager.getDailyRecordsByUser(selectedUser.getName());
            // 按日期升序
            Collections.sort(records, java.util.Comparator.comparing(DailyRecord::getDate));
            if (records.isEmpty()) {
                weightLabel.setText("暂无每日记录");
                bmiLabel.setText("");
                originWeightLabel.setText("");
                originBmiLabel.setText("");
                changeLabel.setText("");
                table.setModel(new javax.swing.table.DefaultTableModel(new Object[0][0], columns));
                return;
            }
            // 最新体重、身高
            DailyRecord latest = records.get(records.size() - 1);
            double latestWeight = latest.getWeight();
            double latestHeight = selectedUser.getHeight();
            // 原始体重、身高
            DailyRecord origin = records.get(0);
            double originWeight = origin.getWeight();
            double originHeight = selectedUser.getHeight();
            // BMI
            double bmi = (latestHeight > 0) ? latestWeight / Math.pow(latestHeight / 100.0, 2) : 0.0;
            String bmiLevel;
            if (bmi < 18.5) bmiLevel = "偏瘦";
            else if (bmi < 24.0) bmiLevel = "正常";
            else if (bmi < 28.0) bmiLevel = "超重";
            else bmiLevel = "肥胖";
            // 原始BMI
            double originBmi = (originHeight > 0) ? originWeight / Math.pow(originHeight / 100.0, 2) : 0.0;
            String originBmiLevel;
            if (originBmi < 18.5) originBmiLevel = "偏瘦";
            else if (originBmi < 24.0) originBmiLevel = "正常";
            else if (originBmi < 28.0) originBmiLevel = "超重";
            else originBmiLevel = "肥胖";
            // 体重变化
            double change = latestWeight - originWeight;
            // 展示
            weightLabel.setText(String.format("最新体重：%.1f kg", latestWeight));
            bmiLabel.setText(String.format("最新BMI：%.2f（%s）", bmi, bmiLevel));
            originWeightLabel.setText(String.format("原始体重：%.1f kg", originWeight));
            originBmiLabel.setText(String.format("原始BMI：%.2f（%s）", originBmi, originBmiLevel));
            changeLabel.setText(String.format("体重总变化：%+.1f kg", change));
            // 表格数据
            Object[][] data = new Object[records.size()][5];
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (int i = 0; i < records.size(); i++) {
                DailyRecord r = records.get(i);
                double w = r.getWeight();
                double h = selectedUser.getHeight();
                double b = (h > 0) ? w / Math.pow(h / 100.0, 2) : 0.0;
                String level;
                if (b < 18.5) level = "偏瘦";
                else if (b < 24.0) level = "正常";
                else if (b < 28.0) level = "超重";
                else level = "肥胖";
                data[i][0] = r.getDate().format(fmt);
                data[i][1] = String.format("%.1f", w);
                data[i][2] = String.format("%.1f", h);
                data[i][3] = String.format("%.2f", b);
                data[i][4] = level;
            }
            table.setModel(new javax.swing.table.DefaultTableModel(data, columns));
        };
        // 初始刷新
        refresh.run();
        // 用户切换事件
        userComboBox.addActionListener(e -> refresh.run());
        // 刷新按钮事件
        refreshBtn.addActionListener(e -> {
            UserProfile selected = (UserProfile) userComboBox.getSelectedItem();
            String selectedName = selected != null ? selected.getName() : null;
            List<UserProfile> userList = DatabaseManager.getAllUserProfiles();
            userComboBox.setModel(new DefaultComboBoxModel<>(userList.toArray(new UserProfile[0])));
            if (selectedName != null) {
                for (int i = 0; i < userComboBox.getItemCount(); i++) {
                    UserProfile user = userComboBox.getItemAt(i);
                    if (user.getName().equals(selectedName)) {
                        userComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else if (userComboBox.getItemCount() > 0) {
                // 如果没有之前选中的用户，选择第一个
                userComboBox.setSelectedIndex(0);
            }
            // 刷新统计和表格数据
            refresh.run();
        });
        return panel;
    }

    // 运动统计面板（带用户选择框、刷新按钮、统计展示和历史表格）
    private JPanel createExerciseStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));

        // 用户选择下拉框和刷新按钮
        JComboBox<UserProfile> userComboBox;
        if (service.SessionManager.isAdmin()) {
            userComboBox = new JComboBox<>(DatabaseManager.getAllUserProfiles().toArray(new UserProfile[0]));
            userComboBox.setEnabled(true);
        } else {
            UserProfile currentUserProfile = service.SessionManager.getCurrentProfile();
            if (currentUserProfile != null) {
                userComboBox = new JComboBox<>(new UserProfile[]{currentUserProfile});
                userComboBox.setSelectedItem(currentUserProfile);
                userComboBox.setEnabled(false);
            } else {
                userComboBox = new JComboBox<>(new UserProfile[0]);
                userComboBox.setEnabled(false);
            }
        }
        
        JButton refreshBtn = new JButton("刷新");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("选择用户："));
        topPanel.add(userComboBox);
        topPanel.add(refreshBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);

        // 统计区
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        JLabel completionLabel = new JLabel();
        JLabel activeDaysLabel = new JLabel();
        statsPanel.add(completionLabel);
        statsPanel.add(activeDaysLabel);

        // 表格区
        String[] columns = {"日期", "类型", "计划时长", "实际时长", "强度", "完成状态"};
        Object[][] tableData = {};
        JTable table = new JTable(tableData, columns);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(0, 200));

        // 垂直容器，统计区+表格区
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(statsPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(tableScroll);
        panel.add(centerPanel, BorderLayout.CENTER);

        // 刷新统计和表格
        Runnable refresh = () -> {
            UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
            if (selectedUser == null) {
                completionLabel.setText("暂无用户");
                activeDaysLabel.setText("");
                table.setModel(new javax.swing.table.DefaultTableModel(new Object[0][0], columns));
                return;
            }
            List<ExercisePlan> plans = DatabaseManager.getExercisePlansByUser(selectedUser.getName());
            if (plans == null || plans.isEmpty()) {
                completionLabel.setText("暂无运动计划记录");
                activeDaysLabel.setText("");
                table.setModel(new javax.swing.table.DefaultTableModel(new Object[0][0], columns));
                return;
            }
            // 按日期升序
            plans.sort(Comparator.comparing(ExercisePlan::getPlanDate));
            // 统计完成率
            int total = plans.size();
            int completed = (int) plans.stream().filter(ExercisePlan::isCompleted).count();
            double completionRate = total > 0 ? (double) completed / total * 100 : 0.0;
            // 统计活跃天数
            long activeDays = plans.stream().map(ExercisePlan::getPlanDate).distinct().count();
            // 展示
            completionLabel.setText(String.format("本周期完成率：%.1f%%", completionRate));
            activeDaysLabel.setText(String.format("本周期活跃天数：%d天", activeDays));
            // 表格数据
            Object[][] data = new Object[plans.size()][6];
            for (int i = 0; i < plans.size(); i++) {
                ExercisePlan p = plans.get(i);
                data[i][0] = p.getPlanDateString();
                data[i][1] = p.getExerciseType();
                data[i][2] = p.getDurationString();
                data[i][3] = p.getActualDurationString();
                data[i][4] = p.getIntensity();
                data[i][5] = p.getCompletionStatusText();
            }
            table.setModel(new javax.swing.table.DefaultTableModel(data, columns));
        };
        // 初始刷新
        refresh.run();
        // 用户切换事件
        userComboBox.addActionListener(e -> refresh.run());
        // 刷新按钮事件
        refreshBtn.addActionListener(e -> {
            UserProfile selected = (UserProfile) userComboBox.getSelectedItem();
            String selectedName = selected != null ? selected.getName() : null;
            List<UserProfile> userList = DatabaseManager.getAllUserProfiles();
            userComboBox.setModel(new DefaultComboBoxModel<>(userList.toArray(new UserProfile[0])));
            if (selectedName != null) {
                for (int i = 0; i < userComboBox.getItemCount(); i++) {
                    UserProfile user = userComboBox.getItemAt(i);
                    if (user.getName().equals(selectedName)) {
                        userComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else if (userComboBox.getItemCount() > 0) {
                // 如果没有之前选中的用户，选择第一个
                userComboBox.setSelectedIndex(0);
            }
            // 刷新统计和表格数据
            refresh.run();
        });
        return panel;
    }

    // 饮食统计面板（带用户选择框、刷新按钮、统计展示和历史表格）
    private JPanel createDietStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));

        // 用户选择下拉框和刷新按钮
        JComboBox<UserProfile> userComboBox;
        if (service.SessionManager.isAdmin()) {
            userComboBox = new JComboBox<>(DatabaseManager.getAllUserProfiles().toArray(new UserProfile[0]));
            userComboBox.setEnabled(true);
        } else {
            UserProfile currentUserProfile = service.SessionManager.getCurrentProfile();
            if (currentUserProfile != null) {
                userComboBox = new JComboBox<>(new UserProfile[]{currentUserProfile});
                userComboBox.setSelectedItem(currentUserProfile);
                userComboBox.setEnabled(false);
            } else {
                userComboBox = new JComboBox<>(new UserProfile[0]);
                userComboBox.setEnabled(false);
            }
        }
        
        JButton refreshBtn = new JButton("刷新");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("选择用户："));
        topPanel.add(userComboBox);
        topPanel.add(refreshBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);

        // 统计区
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        JLabel daysLabel = new JLabel();
        JLabel freqLabel = new JLabel();
        statsPanel.add(daysLabel);
        statsPanel.add(freqLabel);

        // 表格区
        String[] columns = {"日期", "早餐", "午餐", "晚餐", "备注"};
        Object[][] tableData = {};
        JTable table = new JTable(tableData, columns);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(0, 200));

        // 垂直容器，统计区+表格区
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(statsPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(tableScroll);
        panel.add(centerPanel, BorderLayout.CENTER);

        // 刷新统计和表格
        Runnable refresh = () -> {
            UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
            if (selectedUser == null) {
                daysLabel.setText("暂无用户");
                freqLabel.setText("");
                table.setModel(new javax.swing.table.DefaultTableModel(new Object[0][0], columns));
                return;
            }
            List<DietRecord> records = DatabaseManager.getDietRecordsByUser(selectedUser.getName());
            if (records == null || records.isEmpty()) {
                daysLabel.setText("暂无饮食记录");
                freqLabel.setText("");
                table.setModel(new javax.swing.table.DefaultTableModel(new Object[0][0], columns));
                return;
            }
            // 按日期升序
            records.sort(java.util.Comparator.comparing(DietRecord::getRecordDate));
            // 统计天数
            long days = records.stream().map(DietRecord::getRecordDate).distinct().count();
            // 统计频率（以用户有记录的天数/总天数，若只有一条则为100%）
            long totalDays = 1;
            if (records.size() > 1) {
                totalDays = java.time.temporal.ChronoUnit.DAYS.between(
                    records.get(0).getRecordDate(),
                    records.get(records.size() - 1).getRecordDate()
                ) + 1;
            }
            double freq = totalDays > 0 ? (double) days / totalDays * 100 : 100.0;
            // 展示
            daysLabel.setText(String.format("本周期饮食记录天数：%d天", days));
            freqLabel.setText(String.format("本周期记录频率：%.1f%%", freq));
            // 表格数据
            Object[][] data = new Object[records.size()][5];
            for (int i = 0; i < records.size(); i++) {
                DietRecord r = records.get(i);
                data[i][0] = r.getRecordDateString();
                data[i][1] = r.getBreakfast();
                data[i][2] = r.getLunch();
                data[i][3] = r.getDinner();
                data[i][4] = r.getNotes();
            }
            table.setModel(new javax.swing.table.DefaultTableModel(data, columns));
        };
        // 初始刷新
        refresh.run();
        // 用户切换事件
        userComboBox.addActionListener(e -> refresh.run());
        // 刷新按钮事件
        refreshBtn.addActionListener(e -> {
            UserProfile selected = (UserProfile) userComboBox.getSelectedItem();
            String selectedName = selected != null ? selected.getName() : null;
            List<UserProfile> userList = DatabaseManager.getAllUserProfiles();
            userComboBox.setModel(new DefaultComboBoxModel<>(userList.toArray(new UserProfile[0])));
            if (selectedName != null) {
                for (int i = 0; i < userComboBox.getItemCount(); i++) {
                    UserProfile user = userComboBox.getItemAt(i);
                    if (user.getName().equals(selectedName)) {
                        userComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else if (userComboBox.getItemCount() > 0) {
                // 如果没有之前选中的用户，选择第一个
                userComboBox.setSelectedIndex(0);
            }
            // 刷新统计和表格数据
            refresh.run();
        });
        return panel;
    }
} 