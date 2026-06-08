# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 工作范围约束

**所有操作必须严格限制在项目根目录 `d:/ke she/` 内。** 包括但不限于：

- 创建、修改、删除文件不得超出此目录
- Shell 命令执行前必须确认工作路径在此目录下
- 不得读取、写入或引用项目目录之外的任何文件
- 安装依赖、生成配置等所有产出物均落在此目录内

## 项目概况

鳝溪购物交易中心 — 电商购物系统，买家浏览商品→加购→下单→支付→收货，卖家管理商品→发货→处理退货。

**项目状态：已完整编码，84 个 Java 源文件，后端 + 客户端联调通过。**

### 设计文档

- [requirements.md](requirements.md) — 需求规格说明书（含 19 页 UI 设计）
- [database-design.md](database-design.md) — 数据库表设计（10 张表）
- [init-database.sql](init-database.sql) — 建库建表 + 种子数据

## 技术选型

| 层级 | 选型 | 版本 |
|------|------|------|
| 数据库 | MySQL（InnoDB + utf8mb4） | 8.0 |
| 后端框架 | Spring Boot | 3.2.6 |
| 安全框架 | Spring Security + JWT（jjwt） | 0.12.5 |
| 持久层 | MyBatis（注解 + XML 混合） | 3.0.3 |
| 密码加密 | BCrypt（spring-security-crypto） | — |
| 前端 | Java Swing（CardLayout 导航） | — |
| 构建工具 | Maven 多模块 | Java 17 target |
| JDK 运行版本 | Java 25.0.2 | — |

## 常用命令

### 构建

```bash
# 全量编译
cd "d:/ke she" && mvn compile

# 打包（含 fat jar）
mvn package -DskipTests
```

### 数据库

```bash
# 初始化（首次或重置）
mysql -u root -p234rdfes2 --default-character-set=utf8mb4 -e "source init-database.sql"
```

### 启动后端

```bash
cd "d:/ke she/backend" && mvn spring-boot:run
# 启动在 http://localhost:8080，约 2-3 秒
```

### 启动客户端

```bash
cd "d:/ke she" && start-client.bat
# 或双击项目根目录下的 start-client.bat
```

## 项目结构（84 文件）

```
d:/ke she/
├── pom.xml                                    ← 父 POM（版本管理、公共配置）
├── init-database.sql                          ← 数据库初始化脚本
├── start-client.bat                           ← Swing 客户端一键启动
│
├── backend/  (61 文件)
│   └── src/main/
│       ├── java/com/shanxi/shopping/
│       │   ├── ShoppingApplication.java       ← Spring Boot 入口
│       │   ├── config/                        ← WebConfig, MyBatisConfig
│       │   ├── security/                      ← JwtUtil, JwtAuthFilter, SecurityConfig, SecurityUtil
│       │   ├── common/                        ← Result<T>, BusinessException, GlobalExceptionHandler, 枚举
│       │   ├── model/
│       │   │   ├── entity/ (10)               ← User, Seller, Product, Order, CartItem ……
│       │   │   ├── dto/ (6)                   ← RegisterDTO, LoginDTO, CreateOrderDTO …
│       │   │   └── vo/ (6)                    ← LoginVO, ProductVO, OrderVO, PageVO …
│       │   ├── mapper/ (10 接口 + 4 XML)      ← MyBatis 数据访问
│       │   ├── service/ (5 接口 + 5 实现)      ← 业务逻辑层
│       │   └── controller/ (5)                ← REST API 层
│       └── resources/
│           ├── application.yml                 ← DB / JWT / 端口配置
│           └── mapper/ (4 XML)
│
└── client/  (23 文件)
    └── src/main/java/com/shanxi/shopping/client/
        ├── ShoppingClient.java                ← Swing 入口
        ├── common/AppContext.java             ← 全局状态（token/role/跨页传参）
        ├── api/ApiClient.java                 ← HTTP 通信（Jackson 序列化）
        └── ui/
            ├── MainFrame.java                 ← JFrame 主窗口 (CardLayout)
            ├── LoginPanel.java                ← PG-01 登录
            ├── RegisterPanel.java             ← PG-02 买家注册
            ├── SellerRegisterPanel.java       ← PG-03 卖家注册
            ├── ProductListPanel.java          ← PG-04 商品列表
            ├── ProductDetailPanel.java        ← PG-05 商品详情
            ├── CartPanel.java                 ← PG-06 购物车
            ├── OrderConfirmPanel.java         ← PG-07 订单确认
            ├── OrderListPanel.java            ← PG-08 订单列表
            ├── OrderDetailPanel.java          ← PG-09 订单详情
            ├── ReturnPanel.java               ← PG-10 退货申请
            ├── AddressPanel.java              ← PG-11 地址管理
            ├── ProfilePanel.java              ← PG-12 个人信息
            ├── SellerProductPanel.java        ← PG-13 商品管理
            ├── SellerProductEditPanel.java    ← PG-14 商品编辑
            ├── SellerOrderPanel.java          ← PG-15 订单管理
            ├── SellerShippingPanel.java       ← PG-16 发货处理
            ├── SellerReturnPanel.java         ← PG-17 退货处理
            ├── SellerStatsPanel.java          ← PG-18 销售统计
            └── SellerShopPanel.java           ← PG-19 店铺信息
```

## 核心业务规则

### 订单状态机

```
待付款(1) → 待发货(2) → 待收货(3) → 已完成(4)
    ↓                                        ↓
已取消(5)                              退货中(6) → 已退货(7)
```

状态校验通过 `OrderStatus` 枚举的 `canCancel()`/`canShip()`/`canReceive()`/`canReturn()` 方法，数据库更新用 CAS（`UPDATE … WHERE status = #{oldStatus}`）防并发。

### 关键设计点

- **库存扣减（防超卖）**：`UPDATE products SET stock = stock - #{qty} WHERE id = #{id} AND stock >= #{qty}` → affected rows == 0 即抛异常回滚
- **地址/价格快照**：orders 表存下单时收货信息；order_items 表存商品名/图片/单价——卖家改价不影响历史订单
- **购物车唯一约束**：`uk_user_product`（user_id + product_id），重复加购合并数量
- **订单号规则**：`SX + yyyyMMddHHmmss + 6位大写UUID`
- **支付为模拟**：直接状态 1→2，不对接真实支付
- **商品下架为软删除**：status=0 下架，不物理删除保留历史订单

## 种子数据

| 角色 | 用户名 | 密码 | 备注 |
|------|--------|------|------|
| 买家 | `xiaoming` | `123456` | 含 2 个地址 |
| 买家 | `xiaohong` | `123456` | 含 1 个地址 |
| 买家 | `testbuyer` | `123456` | — |
| 卖家 | `shop_laowang` | `admin123` | 老王数码旗舰店（5 件商品） |
| 卖家 | `shop_meimei` | `admin123` | 美美潮流服饰（4 件商品） |
| 卖家 | `shop_chef` | `admin123` | 松鼠零食小铺（3 件商品） |

19 个商品分类（5 顶级 + 14 二级），12 件种子商品。

## 开发注意事项

### URL 映射

`@GetMapping("/{id}")` 必须加 `\d+` 正则约束（`@GetMapping("/{id:\\d+}")`），否则 "search" 等字符串路径会被当 ID 解析。

### JDBC 连接

`characterEncoding` 参数必须用 `UTF-8`，不能用 `utf8mb4`（Java charset 不识别 MySQL 编码名）。

### 客户端跨页传参

使用 `AppContext.setAttribute(key, value)` → 导航后由 `MainFrame.refreshPage()` 调用 `getAttribute(key)` 消费。key 用完即删（像通道，不是状态）。

目前已对接的传参：
- `productId` → ProductDetailPanel.loadProduct()、SellerProductEditPanel.loadProduct()
- `orderId` → OrderDetailPanel.loadOrder()、SellerShippingPanel.loadOrder()

### 客户端 HTTP 请求模式

部分 Panel 用裸 `java.net.http.HttpRequest` 而非 `ApiClient` 封装，因为需要灵活的 Map 反序列化。保持一致即可，修改时注意 JWT token 需从 `AppContext.getInstance().getToken()` 获取并放入 `Authorization: Bearer` 头。

### JWT 认证

- Token 有效期 24 小时，由 `jwt.expiration` 配置
- 公开端点不校验：`/api/user/login`、`/api/user/register`、`/api/seller/login`、`/api/seller/register`、`/api/products/**`、`/api/categories/**`、`/images/**`
- 买家端点需 `ROLE_USER`，卖家端点需 `ROLE_SELLER`
- `SecurityUtil.getCurrentUserId()` 从 SecurityContext 取当前登录用户
