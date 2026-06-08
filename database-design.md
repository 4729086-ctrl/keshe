# 鳝溪购物交易中心 — 数据库表设计

## 一、用户相关表

### 1. 用户表 `users`

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(255) | NOT NULL | 密码（加密存储） |
| phone | VARCHAR(20) | UNIQUE | 手机号 |
| email | VARCHAR(100) | UNIQUE | 邮箱 |
| avatar | VARCHAR(500) | DEFAULT '' | 头像URL |
| balance | DECIMAL(12,2) | DEFAULT 0.00 | 账户余额 |
| status | TINYINT | DEFAULT 1 | 状态：1-正常, 0-禁用 |
| last_login_at | DATETIME | NULL | 最后登录时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

```sql
CREATE TABLE `users` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `phone` VARCHAR(20) DEFAULT NULL,
    `email` VARCHAR(100) DEFAULT NULL,
    `avatar` VARCHAR(500) DEFAULT '',
    `balance` DECIMAL(12,2) DEFAULT 0.00,
    `status` TINYINT DEFAULT 1 COMMENT '1-正常, 0-禁用',
    `last_login_at` DATETIME DEFAULT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

---

### 2. 用户收货地址表 `user_addresses`

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | 地址ID |
| user_id | BIGINT UNSIGNED | FK → users.id | 用户ID |
| receiver_name | VARCHAR(50) | NOT NULL | 收货人姓名 |
| receiver_phone | VARCHAR(20) | NOT NULL | 收货人电话 |
| province | VARCHAR(50) | NOT NULL | 省 |
| city | VARCHAR(50) | NOT NULL | 市 |
| district | VARCHAR(50) | NOT NULL | 区/县 |
| detail | VARCHAR(255) | NOT NULL | 详细地址 |
| is_default | TINYINT | DEFAULT 0 | 是否默认地址 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

```sql
CREATE TABLE `user_addresses` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `receiver_name` VARCHAR(50) NOT NULL,
    `receiver_phone` VARCHAR(20) NOT NULL,
    `province` VARCHAR(50) NOT NULL,
    `city` VARCHAR(50) NOT NULL,
    `district` VARCHAR(50) NOT NULL,
    `detail` VARCHAR(255) NOT NULL,
    `is_default` TINYINT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';
```

---

## 二、卖家/商家相关表

### 3. 卖家表 `sellers`

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | 卖家ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 卖家用户名 |
| password | VARCHAR(255) | NOT NULL | 密码（加密存储） |
| shop_name | VARCHAR(100) | NOT NULL | 店铺名称 |
| phone | VARCHAR(20) | NOT NULL, UNIQUE | 手机号 |
| email | VARCHAR(100) | UNIQUE | 邮箱 |
| description | TEXT | NULL | 店铺简介 |
| avatar | VARCHAR(500) | DEFAULT '' | 店铺头像/logo |
| status | TINYINT | DEFAULT 0 | 状态：1-正常, 0-禁用, 2-待审核 |
| last_login_at | DATETIME | NULL | 最后登录时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

```sql
CREATE TABLE `sellers` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `shop_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `email` VARCHAR(100) DEFAULT NULL,
    `description` TEXT DEFAULT NULL,
    `avatar` VARCHAR(500) DEFAULT '',
    `status` TINYINT DEFAULT 0 COMMENT '1-正常, 0-禁用, 2-待审核',
    `last_login_at` DATETIME DEFAULT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_seller_username` (`username`),
    UNIQUE KEY `uk_seller_phone` (`phone`),
    UNIQUE KEY `uk_seller_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卖家表';
```

---

## 三、商品相关表

### 4. 商品分类表 `categories`

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | 分类ID |
| name | VARCHAR(50) | NOT NULL | 分类名称 |
| parent_id | BIGINT UNSIGNED | DEFAULT 0 | 父级分类ID，0为顶级 |
| sort_order | INT | DEFAULT 0 | 排序号 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

```sql
CREATE TABLE `categories` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `parent_id` BIGINT UNSIGNED DEFAULT 0,
    `sort_order` INT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';
```

---

### 5. 商品表 `products`

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | 商品ID |
| seller_id | BIGINT UNSIGNED | FK → sellers.id | 所属卖家ID |
| category_id | BIGINT UNSIGNED | FK → categories.id | 所属分类ID |
| name | VARCHAR(200) | NOT NULL | 商品名称 |
| description | TEXT | NULL | 商品描述（富文本） |
| price | DECIMAL(12,2) | NOT NULL | 商品单价 |
| stock | INT UNSIGNED | NOT NULL DEFAULT 0 | 库存数量 |
| main_image | VARCHAR(500) | DEFAULT '' | 主图URL |
| status | TINYINT | DEFAULT 0 | 状态：1-上架, 0-下架 |
| sales_count | INT UNSIGNED | DEFAULT 0 | 累计销量 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

```sql
CREATE TABLE `products` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `seller_id` BIGINT UNSIGNED NOT NULL,
    `category_id` BIGINT UNSIGNED NOT NULL,
    `name` VARCHAR(200) NOT NULL,
    `description` TEXT DEFAULT NULL,
    `price` DECIMAL(12,2) NOT NULL,
    `stock` INT UNSIGNED NOT NULL DEFAULT 0,
    `main_image` VARCHAR(500) DEFAULT '',
    `status` TINYINT DEFAULT 0 COMMENT '1-上架, 0-下架',
    `sales_count` INT UNSIGNED DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_name` (`name`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_product_seller` FOREIGN KEY (`seller_id`) REFERENCES `sellers`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
```

---

### 6. 商品图片表 `product_images`

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | 图片ID |
| product_id | BIGINT UNSIGNED | FK → products.id | 商品ID |
| image_url | VARCHAR(500) | NOT NULL | 图片URL |
| sort_order | INT | DEFAULT 0 | 排序号 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

```sql
CREATE TABLE `product_images` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `product_id` BIGINT UNSIGNED NOT NULL,
    `image_url` VARCHAR(500) NOT NULL,
    `sort_order` INT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    CONSTRAINT `fk_image_product` FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';
```

---

## 四、购物车表

### 7. 购物车表 `cart_items`

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | 购物车项ID |
| user_id | BIGINT UNSIGNED | FK → users.id | 用户ID |
| product_id | BIGINT UNSIGNED | FK → products.id | 商品ID |
| quantity | INT UNSIGNED | NOT NULL DEFAULT 1 | 购买数量 |
| checked | TINYINT | DEFAULT 1 | 是否选中（用于结算） |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 添加时间 |

```sql
CREATE TABLE `cart_items` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `product_id` BIGINT UNSIGNED NOT NULL,
    `quantity` INT UNSIGNED NOT NULL DEFAULT 1,
    `checked` TINYINT DEFAULT 1 COMMENT '1-选中, 0-未选中',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';
```

---

## 五、订单相关表

### 8. 订单表 `orders`

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | 订单ID |
| order_no | VARCHAR(32) | NOT NULL, UNIQUE | 订单编号 |
| user_id | BIGINT UNSIGNED | FK → users.id | 买家ID |
| seller_id | BIGINT UNSIGNED | FK → sellers.id | 卖家ID |
| total_amount | DECIMAL(12,2) | NOT NULL | 订单总金额 |
| status | TINYINT | DEFAULT 1 | 订单状态（见下方说明） |
| receiver_name | VARCHAR(50) | NOT NULL | 收货人（下单快照） |
| receiver_phone | VARCHAR(20) | NOT NULL | 收货电话（下单快照） |
| receiver_address | VARCHAR(500) | NOT NULL | 收货地址（下单快照） |
| logistics_company | VARCHAR(50) | NULL | 物流公司 |
| logistics_no | VARCHAR(50) | NULL | 物流单号 |
| shipped_at | DATETIME | NULL | 发货时间 |
| received_at | DATETIME | NULL | 收货时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 下单时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

> **订单状态流转：**  
> 1-待付款 → 2-已付款/待发货 → 3-已发货/待收货 → 4-已收货/已完成  
> 可中断状态：5-已取消, 6-退货中, 7-已退货

```sql
CREATE TABLE `orders` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `order_no` VARCHAR(32) NOT NULL,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `seller_id` BIGINT UNSIGNED NOT NULL,
    `total_amount` DECIMAL(12,2) NOT NULL,
    `status` TINYINT DEFAULT 1 COMMENT '1-待付款, 2-待发货, 3-待收货, 4-已完成, 5-已取消, 6-退货中, 7-已退货',
    `receiver_name` VARCHAR(50) NOT NULL,
    `receiver_phone` VARCHAR(20) NOT NULL,
    `receiver_address` VARCHAR(500) NOT NULL,
    `logistics_company` VARCHAR(50) DEFAULT NULL,
    `logistics_no` VARCHAR(50) DEFAULT NULL,
    `shipped_at` DATETIME DEFAULT NULL,
    `received_at` DATETIME DEFAULT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    CONSTRAINT `fk_order_seller` FOREIGN KEY (`seller_id`) REFERENCES `sellers`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
```

---

### 9. 订单商品明细表 `order_items`

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | 明细ID |
| order_id | BIGINT UNSIGNED | FK → orders.id | 订单ID |
| product_id | BIGINT UNSIGNED | FK → products.id | 商品ID |
| product_name | VARCHAR(200) | NOT NULL | 商品名称（下单快照） |
| product_image | VARCHAR(500) | DEFAULT '' | 商品图片（下单快照） |
| price | DECIMAL(12,2) | NOT NULL | 购买时单价 |
| quantity | INT UNSIGNED | NOT NULL | 购买数量 |
| subtotal | DECIMAL(12,2) | NOT NULL | 小计（price × quantity） |

```sql
CREATE TABLE `order_items` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT UNSIGNED NOT NULL,
    `product_id` BIGINT UNSIGNED NOT NULL,
    `product_name` VARCHAR(200) NOT NULL,
    `product_image` VARCHAR(500) DEFAULT '',
    `price` DECIMAL(12,2) NOT NULL,
    `quantity` INT UNSIGNED NOT NULL,
    `subtotal` DECIMAL(12,2) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品明细表';
```

---

### 10. 退货记录表 `returns`

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT UNSIGNED | PK, AUTO_INCREMENT | 退货ID |
| order_id | BIGINT UNSIGNED | FK → orders.id | 关联订单ID |
| user_id | BIGINT UNSIGNED | FK → users.id | 申请人 |
| reason | TEXT | NOT NULL | 退货原因 |
| status | TINYINT | DEFAULT 1 | 1-待处理, 2-已同意, 3-已拒绝, 4-已完成 |
| refund_amount | DECIMAL(12,2) | NOT NULL | 退款金额 |
| seller_remark | TEXT | NULL | 卖家处理备注 |
| processed_at | DATETIME | NULL | 处理时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 申请时间 |

```sql
CREATE TABLE `returns` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT UNSIGNED NOT NULL,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `reason` TEXT NOT NULL,
    `status` TINYINT DEFAULT 1 COMMENT '1-待处理, 2-已同意, 3-已拒绝, 4-已完成',
    `refund_amount` DECIMAL(12,2) NOT NULL,
    `seller_remark` TEXT DEFAULT NULL,
    `processed_at` DATETIME DEFAULT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_return_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`),
    CONSTRAINT `fk_return_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退货记录表';
```

---

## 六、ER 关系图（文字版）

```
┌─────────────┐       ┌──────────────────┐
│   users     │       │  user_addresses  │
│─────────────│ 1───n │──────────────────│
│ id (PK)     │       │ id (PK)          │
│ username    │       │ user_id (FK)     │
│ password    │       │ receiver_name    │
│ phone       │       │ receiver_phone   │
│ email       │       │ ...              │
│ balance     │       └──────────────────┘
│ ...         │
└──┬──┬───────┘
   │  │
   │  │ 1───n  ┌─────────────┐
   │  └────────│ cart_items  │
   │           │─────────────│
   │           │ id (PK)     │
   │           │ user_id(FK) │
   │           │ product_id  │
   │           │ quantity    │
   │           └──────┬──────┘
   │                  │
   │ 1───n ┌──────────┴──────┐
   └───────│    orders       │
    ┌──────│─────────────────│──────┐
    │      │ id (PK)         │      │
    │      │ order_no        │      │
    │      │ user_id (FK)    │      │
    │      │ seller_id (FK)  │      │
    │      │ total_amount    │      │
    │      │ status          │      │
    │      │ ...             │      │
    │      └────────┬────────┘      │
    │               │               │
    │ 1───n ┌───────┴────────┐      │
    │       │  order_items   │      │
    │       │────────────────│      │
    │       │ id (PK)        │      │
    │       │ order_id (FK)  │      │
    │       │ product_id     │      │
    │       │ price          │      │
    │       │ quantity       │      │
    │       │ subtotal       │      │
    │       └────────────────┘      │
    │                               │
    │ 1───n ┌─────────────┐         │
    └───────│   returns   │         │
            │─────────────│         │
            │ id (PK)     │         │
            │ order_id(FK)│         │
            │ user_id(FK) │         │
            │ reason      │         │
            │ ...         │         │
            └─────────────┘         │
                                    │
┌─────────────┐              ┌──────┴──────────┐
│   sellers   │              │    products     │
│─────────────│ 1───────n   │─────────────────│
│ id (PK)     │──────────────│ id (PK)         │
│ username    │              │ seller_id (FK)  │
│ password    │              │ category_id(FK) │
│ shop_name   │              │ name            │
│ phone       │              │ price           │
│ ...         │              │ stock           │
└─────────────┘              │ status          │
                             │ ...             │
                             └───────┬─────────┘
                                     │
                            ┌────────┴──────────┐
                            │  product_images   │ 1───n
                            │───────────────────│
┌─────────────┐             │ id (PK)           │
│ categories  │ 1───n       │ product_id (FK)   │
│─────────────│─────────────│ image_url         │
│ id (PK)     │             │ ...               │
│ name        │             └───────────────────┘
│ parent_id   │
│ ...         │
└─────────────┘
```

---

## 七、关键设计说明

| 设计点 | 说明 |
|--------|------|
| **用户与卖家分离** | 买家和卖家独立建表，各自管理认证信息和业务属性，职责清晰 |
| **订单地址快照** | 订单中冗余存储收货地址，防止用户后续修改地址导致历史订单数据异常 |
| **商品价格快照** | `order_items` 中保存下单时的商品名、图片、单价，即使商家后续改价也不影响已有订单 |
| **库存扣减** | 下单时扣减 `products.stock`，需在事务中处理防止超卖；取消订单/退货时回补库存 |
| **退货独立表** | 退货流程独立于订单表，支持部分退货场景，卖家处理后状态同步回 `orders.status` |
| **购物车唯一约束** | `uk_user_product` 保证同一用户不会重复添加同一商品，重复时更新数量即可 |
| **软删除考虑** | 商品下架使用 `status` 字段而非物理删除，保留历史订单关联 |
