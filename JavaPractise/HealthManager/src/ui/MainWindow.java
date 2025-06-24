package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * 主窗口类 - 程序的图形界面
 * 
 * 这个类负责创建和管理应用程序的主窗口。在第一阶段Day 2中，
 * 主要功能是创建一个包含5个空标签页的窗口界面：
 * 1. 用户档案 - 用于显示和管理用户的基本信息
 * 2. 每日记录 - 用于记录用户的每日健康数据
 * 3. 运动计划 - 用于制定和跟踪运动计划
 * 4. 饮食管理 - 用于记录和管理饮食情况
 * 5. 数据分析 - 用于分析和展示健康数据统计
 * 
 */
public class MainWindow extends JFrame {
    
    /**
     * 构造方法 - 初始化主窗口
     */
    public MainWindow() {
        initComponents();
    }
    
    /**
     * 初始化窗口组件
     * 
     * 该方法负责：
     * 1. 设置窗口的基本属性（标题、大小、关闭行为）
     * 2. 创建菜单栏
     * 3. 创建选项卡面板
     * 4. 添加5个空的标签页
     */
    private void initComponents() {
        // 设置窗口属性
        setTitle("个人健康管理器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);  // 窗口居中显示
        
        // 创建菜单栏
        setJMenuBar(createMenuBar());
        
        // 创建选项卡面板并设置字体
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        
        // 添加5个空的标签页
        tabbedPane.addTab("用户档案", createEmptyPanel("个人基本信息", "用户档案功能开发中..."));
        tabbedPane.addTab("每日记录", createEmptyPanel("每日健康记录", "每日记录功能开发中..."));
        tabbedPane.addTab("运动计划", createEmptyPanel("个人运动计划", "运动计划功能开发中..."));
        tabbedPane.addTab("饮食管理", createEmptyPanel("饮食管理记录", "饮食管理功能开发中..."));
        tabbedPane.addTab("数据分析", createEmptyPanel("健康数据分析", "数据分析功能开发中..."));
        
        // 将选项卡面板添加到窗口
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
        fileMenu.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        // 添加文件菜单项
        JMenuItem saveItem = new JMenuItem("保存数据", KeyEvent.VK_S);
        saveItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveItem.addActionListener(e -> JOptionPane.showMessageDialog(
            this, 
            "数据保存功能将在第三阶段实现", 
            "提示", 
            JOptionPane.INFORMATION_MESSAGE
        ));
        
        JMenuItem exitItem = new JMenuItem("退出", KeyEvent.VK_X);
        exitItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // 创建帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        helpMenu.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        // 添加帮助菜单项
        JMenuItem aboutItem = new JMenuItem("关于", KeyEvent.VK_A);
        aboutItem.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(
            this,
            "个人健康管理器 v1.0\n\n" +
            "这是一个基于Java Swing开发的健康管理应用程序，\n" +
            "旨在帮助用户管理个人健康数据，制定运动计划，\n" +
            "跟踪饮食情况，并提供数据分析功能。\n\n" +
            "开发目的：Java GUI编程学习项目\n" +
            "开发框架：Java Swing\n\n" +
            "© 2025 个人健康管理器 - 学习项目",
            "关于个人健康管理器",
            JOptionPane.INFORMATION_MESSAGE
        ));
        
        helpMenu.add(aboutItem);
        
        // 将菜单添加到菜单栏
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    /**
     * 创建空面板
     * 
     * @param title 面板标题
     * @param message 提示信息
     * @return 创建好的面板
     */
    private JPanel createEmptyPanel(String title, String message) {
        // 创建面板，使用边界布局
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // 创建提示信息标签
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setForeground(Color.GRAY);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // 将标签添加到面板顶部
        panel.add(messageLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    /**
     * 程序入口方法
     * 
     * @param args 命令行参数（当前未使用）
     */
    public static void main(String[] args) {
        // 设置界面外观为系统默认外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 在事件调度线程中启动程序
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
} 