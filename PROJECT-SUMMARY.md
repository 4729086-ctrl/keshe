# 鳝溪购物交易中心 — 项目总结

> 电商购物系统，Java 17 + Spring Boot 3.2.6 + MySQL + Swing，84 源文件，联调通过。

## 技术栈

| 层 | 方案 | 备注 |
|---|---|---|
| 数据库 | MySQL 8.0 (InnoDB + utf8mb4) | 10 张表 |
| 后端 | Spring Boot 3.2.6, MyBatis 3.0.3 | RESTful JSON API |
| 安全 | Spring Security + JWT (jjwt 0.12.5) | BCrypt 密码加密 |
| 前端 | Java Swing, CardLayout | 19 个页面，买/卖双角色 |
| 构建 | Maven 多模块 | backend + client 两子模块 |
| JDK | 25.0.2 → target 17 | |

## 快速启动

```bash
# 1. 初始化数据库（仅首次）
mysql -u root -p --default-character-set=utf8mb4 -e "source init-database.sql"

# 2. 编译
mvn compile

# 3. 启动后端
cd backend && mvn spring-boot:run          # → http://localhost:8080

# 4. 启动客户端（双击运行）
start-client.bat
```

## 测试账号

| 角色 | 用户 | 密码 | 备注 |
|---|---|---|---|
| 买家 | `xiaoming` | `123456` | 2 个地址 |
| 买家 | `xiaohong` | `123456` | 1 个地址 |
| 卖家 | `shop_laowang` | `admin123` | 数码店（5 件商品） |
| 卖家 | `shop_meimei` | `admin123` | 服装店（4 件商品） |
| 卖家 | `shop_chef` | `admin123` | 零食店（3 件商品） |

## 核心业务

**订单状态机：** 待付款(1) → 待发货(2) → 待收货(3) → 已完成(4) / 已取消(5) / 退货中(6) → 已退货(7)

**防超卖：** MySQL 行级锁 `UPDATE … SET stock = stock - N WHERE stock >= N`，affected rows = 0 回滚。

**快照设计：** 下单时快照收货地址和商品价格，卖家改价/用户改地址不影响已有订单。

## 项目结构

```
backend/src/main/java/com/shanxi/shopping/
├── controller/    5 个     REST 接口（~25 端点）
├── service/       10 个    业务逻辑（接口 + 实现）
├── mapper/        10 + 4   MyBatis（接口 + XML）
├── model/         22 个    Entity(10) + DTO(6) + VO(6)
├── common/        6 个     Result, 枚举, 异常处理
├── security/      5 个     JWT, Security 配置
└── config/        2 个     CORS, MyBatis 扫描

client/src/main/java/com/shanxi/shopping/client/
├── api/           2 个     ApiClient, HttpHelper
├── common/        1 个     AppContext（全局状态）
└── ui/            19 个    所有 Swing 页面（见下）
```

**19 个 UI 页面：** 登录 → 商品列表 → 详情 → 购物车 → 订单确认 → 订单列表 → 详情 → 退货 / 卖家：商品管理 → 编辑 → 订单管理 → 发货 → 退货 → 统计 → 店铺信息

## 关键设计文件

| 文件 | 用途 |
|---|---|
| [CLAUDE.md](CLAUDE.md) | 开发指南（命令、架构、注意事项） |
| [requirements.md](requirements.md) | 需求规格 + 页面设计 |
| [database-design.md](database-design.md) | 10 表 ER 关系 + DDL |
| [init-database.sql](init-database.sql) | 一键初始化 |
| [start-client.bat](start-client.bat) | 客户端启动脚本 |
