package ui;

import ui.panels.UserProfilePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Main window class - GUI
 * 
 * This class is responsible for creating and managing the main window of the application. In Day 2,
 * the main function is to create a window interface with 5 empty tabs:
 * 1. User Profile - for displaying and managing user's basic info
 * 2. Daily Record - for recording user's daily health data
 * 3. Exercise Plan - for making and tracking exercise plans
 * 4. Diet Management - for recording and managing diet
 * 5. Data Analysis - for analyzing and displaying health data statistics
 * 
 */
public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor - Initialize main window
     */
    @SuppressWarnings("this-escape")
    public MainWindow() {
        initComponents();
    }
    
    /**
     * Get Chinese font
     * Try different Chinese fonts in priority order to ensure Chinese characters can be displayed correctly
     * 
     * @param style Font style (Font.PLAIN, Font.BOLD, Font.ITALIC)
     * @param size Font size
     * @return Chinese font
     */
    private Font getChineseFont(int style, int size) {
        // Try different Chinese fonts in priority order
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
                // Ignore font creation failure and continue to the next
            }
        }
        
        // If all Chinese fonts are unavailable, use the system default font
        return new Font(Font.SANS_SERIF, style, size);
    }
    
    /**
     * Initialize window components
     * 
     * This method is responsible for:
     * 1. Setting window basic properties (title, size, close behavior)
     * 2. Creating menu bar
     * 3. Creating tabbed panel
     * 4. Adding 5 empty tabs
     */
    private void initComponents() {
        // Set window properties
        setTitle("Personal Health Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);  // Window center display
        
        // Create menu bar
        setJMenuBar(createMenuBar());
        
        // Create tabbed panel and set font
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(getChineseFont(Font.PLAIN, 14));
        
        // Add tabs
        tabbedPane.addTab("用户档案", new UserProfilePanel());
        tabbedPane.addTab("每日记录", createEmptyPanel("每日记录功能开发中..."));
        tabbedPane.addTab("运动计划", createEmptyPanel("运动计划功能开发中..."));
        tabbedPane.addTab("饮食管理", createEmptyPanel("饮食管理功能开发中..."));
        tabbedPane.addTab("数据分析", createEmptyPanel("数据分析功能开发中..."));
        
        // Add tabbed panel to window
        add(tabbedPane);
    }
    
    /**
     * Create menu bar
     * 
     * @return Configured menu bar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Create file menu
        JMenu fileMenu = new JMenu("文件");
        fileMenu.setFont(getChineseFont(Font.PLAIN, 14));
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        // Add file menu items
        JMenuItem saveItem = new JMenuItem("保存数据", KeyEvent.VK_S);
        saveItem.setFont(getChineseFont(Font.PLAIN, 14));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                    MainWindow.this, 
                    "数据保存功能将在第三阶段实现", 
                    "提示", 
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        
        JMenuItem exitItem = new JMenuItem("退出", KeyEvent.VK_X);
        exitItem.setFont(getChineseFont(Font.PLAIN, 14));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        exitItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Create help menu
        JMenu helpMenu = new JMenu("帮助");
        helpMenu.setFont(getChineseFont(Font.PLAIN, 14));
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        // Add help menu items
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
        
        // Add menu to menu bar
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    /**
     * Create empty panel
     * 
     * @param message Prompt information
     * @return Created panel
     */
    private JPanel createEmptyPanel(String message) {
        // Create panel, using BorderLayout
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Create prompt information label
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(getChineseFont(Font.PLAIN, 14));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setForeground(Color.GRAY);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Add label to panel top
        panel.add(messageLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    /**
     * Program entry method
     * 
     * @param args Command line parameters (currently not used)
     */
    public static void main(String[] args) {
        // Set interface appearance to system default appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Start program in event dispatch thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow window = new MainWindow();
                window.setVisible(true);
            }
        });
    }
} 