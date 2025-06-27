package ui.panels;

import javax.swing.*;
import java.awt.*;

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

    // 健康统计面板（占位）
    private JPanel createHealthStatsPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("健康统计面板开发中..."));
        return panel;
    }

    // 运动统计面板（占位）
    private JPanel createExerciseStatsPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("运动统计面板开发中..."));
        return panel;
    }

    // 饮食统计面板（占位）
    private JPanel createDietStatsPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("饮食统计面板开发中..."));
        return panel;
    }
} 