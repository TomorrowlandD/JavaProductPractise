import ui.MainWindow;
import service.DatabaseManager;

public class Main {
    public static void main(String[] args) {
        // 启动主窗口前初始化数据库，自动建表
        DatabaseManager.initializeDatabase();
        // 启动主窗口
        MainWindow.main(args);
    }
} 