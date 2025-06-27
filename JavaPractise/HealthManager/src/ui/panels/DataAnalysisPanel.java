package ui.panels;

import javax.swing.*;
import java.awt.*;
import model.UserProfile;
import service.DatabaseManager;
import java.util.List;
import model.DailyRecord;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

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

    // 健康统计面板（带用户选择框和统计展示）
    private JPanel createHealthStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));

        // 用户选择下拉框
        List<UserProfile> userList = DatabaseManager.getAllUserProfiles();
        JComboBox<UserProfile> userComboBox = new JComboBox<>(userList.toArray(new UserProfile[0]));
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
            List<DailyRecord> records = DatabaseManager.getAllDailyRecords();
            // 只保留当前用户的记录
            records.removeIf(r -> !selectedUser.getName().equals(r.getUserName()));
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
        refreshBtn.addActionListener(e -> refresh.run());
        return panel;
    }

    // 运动统计面板（带用户选择框）
    private JPanel createExerciseStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));

        // 用户选择下拉框
        List<UserProfile> userList = DatabaseManager.getAllUserProfiles();
        JComboBox<UserProfile> userComboBox = new JComboBox<>(userList.toArray(new UserProfile[0]));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("选择用户："));
        topPanel.add(userComboBox);
        panel.add(topPanel, BorderLayout.NORTH);

        // 占位内容
        JLabel placeholder = new JLabel("运动统计面板开发中...");
        placeholder.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(placeholder, BorderLayout.CENTER);

        // 用户切换事件（预留刷新逻辑）
        userComboBox.addActionListener(e -> {
            UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
            // TODO: 根据selectedUser刷新统计内容
        });

        return panel;
    }

    // 饮食统计面板（带用户选择框）
    private JPanel createDietStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));

        // 用户选择下拉框
        List<UserProfile> userList = DatabaseManager.getAllUserProfiles();
        JComboBox<UserProfile> userComboBox = new JComboBox<>(userList.toArray(new UserProfile[0]));
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("选择用户："));
        topPanel.add(userComboBox);
        panel.add(topPanel, BorderLayout.NORTH);

        // 占位内容
        JLabel placeholder = new JLabel("饮食统计面板开发中...");
        placeholder.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(placeholder, BorderLayout.CENTER);

        // 用户切换事件（预留刷新逻辑）
        userComboBox.addActionListener(e -> {
            UserProfile selectedUser = (UserProfile) userComboBox.getSelectedItem();
            // TODO: 根据selectedUser刷新统计内容
        });

        return panel;
    }
} 