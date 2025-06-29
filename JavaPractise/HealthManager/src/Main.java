import ui.MainWindow;
import ui.LoginDialog;
import service.DatabaseManager;
import service.SessionManager;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // 启动主窗口前初始化数据库，自动建表
        DatabaseManager.initializeDatabase();
        
        // 设置Look and Feel
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // 使用默认Look and Feel
        }
        
        // 显示登录对话框
        SwingUtilities.invokeLater(() -> {
            boolean loginSuccess = LoginDialog.showLoginDialog();
            
            if (loginSuccess && SessionManager.isLoggedIn()) {
                // 登录成功，显示主界面
                SwingUtilities.invokeLater(() -> {
                    MainWindow mainWindow = new MainWindow();
                    mainWindow.setVisible(true);
                    System.out.println("系统启动成功，当前用户: " + SessionManager.getCurrentUserDisplayName());
                });
            } else {
                // 登录失败或取消登录，退出程序
                System.out.println("用户取消登录，程序退出");
                System.exit(0);
            }
        });
    }
} 