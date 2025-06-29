package ui;

import ui.panels.UserProfilePanel;
import ui.panels.DailyRecordPanel;
import ui.panels.ExercisePlanPanel;
import ui.panels.DietPanel;
import ui.panels.DataAnalysisPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * 主窗口类 - 图形界面
 * 
 * 本类负责创建和管理应用程序的主窗口。在第2阶段，
 * 主要功能是创建包含5个空标签页的窗口界面：
 * 1. 用户档案 - 显示和管理用户基本信息
 * 2. 每日记录 - 记录用户每日健康数据
 * 3. 运动计划 - 制定和跟踪运动计划
 * 4. 饮食管理 - 记录和管理饮食
 * 5. 数据分析 - 分析和展示健康数据统计
 * 
 */
public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    
    /**
     * 构造方法 - 初始化主窗口
     */
    @SuppressWarnings("this-escape")
    public MainWindow() {
        initComponents();
        updateWindowTitle();
    }
    
    /**
     * 更新窗口标题，显示当前用户信息
     */
    private void updateWindowTitle() {
        String title = "健康管理系统";
        if (service.SessionManager.isLoggedIn()) {
            String userInfo = service.SessionManager.getCurrentUserDisplayName();
            title += " - " + userInfo;
        }
        setTitle(title);
    }
    
    /**
     * 获取中文字体
     * 按优先顺序尝试不同的中文字体，确保中文字符能正确显示
     * 
     * @param style 字体样式（Font.PLAIN, Font.BOLD, Font.ITALIC）
     * @param size 字号
     * @return 中文字体
     */
    private Font getChineseFont(int style, int size) {
        // 按优先顺序尝试不同的中文字体
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
                // 忽略字体创建失败，继续尝试下一个
            }
        }
        
        // 如果所有中文字体都不可用，使用系统默认字体
        return new Font(Font.SANS_SERIF, style, size);
    }
    
    /**
     * 初始化窗口组件
     * 
     * 本方法负责：
     * 1. 设置窗口基本属性（标题、大小、关闭行为）
     * 2. 创建菜单栏
     * 3. 创建标签面板
     * 4. 添加5个空标签页
     */
    private void initComponents() {
        // 设置窗口属性
        setTitle("Personal Health Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);  // 窗口居中显示
        
        // 创建菜单栏
        setJMenuBar(createMenuBar());
        
        // 创建标签面板并设置字体
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(getChineseFont(Font.PLAIN, 14));
        
        // 添加标签页
        tabbedPane.addTab("用户档案", new UserProfilePanel());
        tabbedPane.addTab("每日记录", new DailyRecordPanel());
        tabbedPane.addTab("运动计划", new ExercisePlanPanel());
        tabbedPane.addTab("饮食管理", new DietPanel());
        tabbedPane.addTab("数据分析", new DataAnalysisPanel());
        
        // 将标签面板添加到窗口
        add(tabbedPane);
    }
    
    /**
     * 创建菜单栏
     * 
     * @return 配置好的菜单栏
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // 创建文件菜单
        JMenu fileMenu = new JMenu("文件");
        fileMenu.setFont(getChineseFont(Font.PLAIN, 14));
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem exitItem = new JMenuItem("退出", KeyEvent.VK_X);
        exitItem.setFont(getChineseFont(Font.PLAIN, 14));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        exitItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // 创建帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        helpMenu.setFont(getChineseFont(Font.PLAIN, 14));
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        // 添加帮助菜单项
        JMenuItem aboutItem = new JMenuItem("关于", KeyEvent.VK_A);
        aboutItem.setFont(getChineseFont(Font.PLAIN, 14));
        aboutItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                    MainWindow.this,
                    "个人健康管理器 v1.0\n\n" +
                    "本程序是基于Java Swing开发的健康管理应用，\n" +
                    "旨在帮助用户管理个人健康数据，制定运动计划，\n" +
                    "记录饮食，并提供数据分析功能。\n\n" +
                    "开发目的：Java GUI编程学习项目\n" +
                    "开发框架：Java Swing\n\n" +
                    "© 2025 个人健康管理器 - 学习项目",
                    "关于个人健康管理器",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        
        helpMenu.add(aboutItem);
        
        // 将菜单添加到菜单栏
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    /**
     * 创建空面板
     * 
     * @param message 提示信息
     * @return 创建的面板
     */
    private JPanel createEmptyPanel(String message) {
        // 创建面板，使用BorderLayout布局
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // 创建提示信息标签
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(getChineseFont(Font.PLAIN, 14));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setForeground(Color.GRAY);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // 将标签添加到面板顶部
        panel.add(messageLabel, BorderLayout.NORTH);
        
        return panel;
    }
} 