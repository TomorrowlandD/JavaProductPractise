# 个人健康管理器 (Personal Health Manager)

一个基于Java Swing开发的桌面应用程序，帮助用户管理个人健康数据，包括用户档案、每日记录、运动计划、饮食管理和数据分析等功能。

## 🌟 项目特色

- **多用户支持**：支持多个用户独立管理各自的健康数据
- **数据持久化**：基于MySQL数据库，数据安全可靠
- **界面友好**：简洁美观的图形用户界面
- **功能完整**：涵盖健康管理的各个方面
- **实时统计**：提供详细的健康数据分析和统计

## 📋 功能模块

### 1. 用户档案管理
- 用户基本信息录入（姓名、年龄、性别、身高、体重）
- 健康目标设置（目标体重、健身目标类型）
- 健康状况记录（疾病史、过敏史等）
- 用户档案的增删改查操作

### 2. 每日健康记录
- 每日体重记录和BMI计算
- 运动内容和时长记录
- 睡眠时长记录
- 心情状态记录
- 历史记录查看和编辑

### 3. 运动计划管理
- 运动计划制定（运动类型、日期、时长、强度）
- 计划完成状态跟踪
- 实际运动时长记录
- 运动统计（完成率、活跃天数）
- 历史计划查看和管理

### 4. 饮食记录管理
- 三餐详细记录（早餐、午餐、晚餐）
- 常见食物快速选择
- 自定义食物添加
- 饮食备注记录
- 饮食统计（记录天数、频率）

### 5. 数据分析统计
- **健康统计**：体重变化趋势、BMI等级、健康建议
- **运动统计**：完成率分析、活跃天数统计
- **饮食统计**：记录频率分析、饮食规律统计
- 多维度数据可视化展示

## 🛠 技术栈

- **开发语言**：Java 8+
- **GUI框架**：Java Swing
- **数据库**：MySQL 8.0+
- **数据库连接**：JDBC
- **构建工具**：Maven（可选）
- **IDE支持**：IntelliJ IDEA, Eclipse, VS Code

## 📦 系统要求

- **操作系统**：Windows 10+, macOS 10.14+, Linux
- **Java环境**：JDK 8 或更高版本
- **数据库**：MySQL 8.0 或更高版本
- **内存**：建议 2GB 以上
- **存储空间**：至少 100MB 可用空间

## 🚀 安装和运行

### 1. 环境准备

#### 安装Java环境
```bash
# 检查Java版本
java -version

# 如果没有安装，请下载并安装JDK 8+
# 下载地址：https://www.oracle.com/java/technologies/downloads/
```

#### 安装MySQL数据库
```bash
# Windows: 下载MySQL Installer
# macOS: brew install mysql
# Linux: sudo apt-get install mysql-server

# 启动MySQL服务
# Windows: 通过服务管理器启动
# macOS/Linux: sudo systemctl start mysql
```

### 2. 数据库配置

#### 创建数据库和用户
```sql
-- 登录MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE health_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'health_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON health_manager.* TO 'health_user'@'localhost';
FLUSH PRIVILEGES;
```

#### 配置数据库连接
编辑 `HealthManager/src/service/DatabaseManager.java` 文件中的数据库连接信息：

```java
private static final String URL = "jdbc:mysql://localhost:3306/health_manager?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

### 3. 编译和运行

#### 方法一：使用IDE
1. 使用IntelliJ IDEA或Eclipse打开项目
2. 配置项目JDK
3. 运行 `Main.java` 文件

#### 方法二：命令行编译
```bash
# 进入项目目录
cd HealthManager

# 编译Java文件
javac -cp "lib/*" -d classes src/**/*.java

# 运行程序
java -cp "classes;lib/*" Main
```

