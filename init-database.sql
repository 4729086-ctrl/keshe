-- ============================================================
-- 鳝溪购物交易中心 — 数据库初始化脚本
-- 用法：mysql -u root -p < init-database.sql
-- ============================================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS shanxi_shopping
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE shanxi_shopping;

-- ============================================================
-- 2. 建表（共 10 张）
-- ============================================================

-- 2.1 用户表
DROP TABLE IF EXISTS `returns`;
DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `cart_items`;
DROP TABLE IF EXISTS `product_images`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `user_addresses`;
DROP TABLE IF EXISTS `sellers`;
DROP TABLE IF EXISTS `users`;

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

CREATE TABLE `categories` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `parent_id` BIGINT UNSIGNED DEFAULT 0,
    `sort_order` INT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

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

-- ============================================================
-- 3. 种子数据
-- ============================================================

-- 3.1 买家账号 (密码: 123456, BCrypt 加密)
INSERT INTO `users` (`id`, `username`, `password`, `phone`, `email`, `status`)
VALUES
(1, 'xiaoming',  '$2a$10$YWPquNCxS4scDEwo7z/vxe55XIllKFeVTRPGS5uvX60.sHesutu6y', '13800000001', 'xiaoming@example.com', 1),
(2, 'xiaohong',  '$2a$10$YWPquNCxS4scDEwo7z/vxe55XIllKFeVTRPGS5uvX60.sHesutu6y', '13800000002', 'xiaohong@example.com', 1),
(3, 'testbuyer', '$2a$10$YWPquNCxS4scDEwo7z/vxe55XIllKFeVTRPGS5uvX60.sHesutu6y', '13800000003', 'testbuyer@example.com', 1);

-- 3.2 买家收货地址
INSERT INTO `user_addresses` (`user_id`, `receiver_name`, `receiver_phone`, `province`, `city`, `district`, `detail`, `is_default`)
VALUES
(1, '小明', '13800000001', '福建省', '福州市', '鼓楼区', '五一北路100号阳光大厦1201室', 1),
(1, '李明', '13800000001', '福建省', '福州市', '仓山区', '金山大道200号万达广场3号楼501', 0),
(2, '小红', '13800000002', '福建省', '厦门市', '思明区', '湖滨南路50号软件园二期A栋', 1);

-- 3.3 卖家账号 (密码: admin123, BCrypt 加密)
INSERT INTO `sellers` (`id`, `username`, `password`, `shop_name`, `phone`, `email`, `description`, `status`)
VALUES
(1, 'shop_laowang', '$2a$10$u.mV6UUdUg2g1DnQTNWYL.WbAhNgeP/HWuZrnPHq4aY1zFGFwAbf6', '老王数码旗舰店', '13900000001', 'laowang@example.com', '专注数码产品 10 年，正品保障，售后无忧', 1),
(2, 'shop_meimei',  '$2a$10$u.mV6UUdUg2g1DnQTNWYL.WbAhNgeP/HWuZrnPHq4aY1zFGFwAbf6', '美美潮流服饰',   '13900000002', 'meimei@example.com',  '精选时尚女装，每周上新，品质之选', 1),
(3, 'shop_chef',    '$2a$10$u.mV6UUdUg2g1DnQTNWYL.WbAhNgeP/HWuZrnPHq4aY1zFGFwAbf6', '松鼠零食小铺',   '13900000003', 'chef@example.com',    '进口零食、网红爆款，好吃不贵', 1);

-- 3.4 商品分类
-- 顶级分类
INSERT INTO `categories` (`id`, `name`, `parent_id`, `sort_order`) VALUES
(1, '手机数码', 0, 1),
(2, '电脑办公', 0, 2),
(3, '服装鞋帽', 0, 3),
(4, '食品饮料', 0, 4),
(5, '家居生活', 0, 5);

-- 二级分类（手机数码）
INSERT INTO `categories` (`id`, `name`, `parent_id`, `sort_order`) VALUES
(11, '智能手机', 1, 1),
(12, '平板电脑', 1, 2),
(13, '耳机/音箱', 1, 3),
(14, '数码配件', 1, 4);

-- 二级分类（电脑办公）
INSERT INTO `categories` (`id`, `name`, `parent_id`, `sort_order`) VALUES
(21, '笔记本', 2, 1),
(22, '台式机', 2, 2),
(23, '电脑配件', 2, 3);

-- 二级分类（服装鞋帽）
INSERT INTO `categories` (`id`, `name`, `parent_id`, `sort_order`) VALUES
(31, '女装', 3, 1),
(32, '男装', 3, 2),
(33, '鞋靴', 3, 3);

-- 二级分类（食品饮料）
INSERT INTO `categories` (`id`, `name`, `parent_id`, `sort_order`) VALUES
(41, '休闲零食', 4, 1),
(42, '饮料冲调', 4, 2);

-- 二级分类（家居生活）
INSERT INTO `categories` (`id`, `name`, `parent_id`, `sort_order`) VALUES
(51, '家居家纺', 5, 1),
(52, '厨房用品', 5, 2);

-- 3.5 商品数据
INSERT INTO `products` (`id`, `seller_id`, `category_id`, `name`, `description`, `price`, `stock`, `main_image`, `status`, `sales_count`) VALUES
-- 老王数码旗舰店 (seller_id=1)
(1,  1, 11, '华为MateBook X Pro 2024款',
    '14.2英寸OLED原色全面屏丨Intel Ultra 9处理器丨32GB+2TB大存储丨触屏轻薄本',
    8999.00, 50, '/images/products/matebook_x_pro.jpg', 1, 128),
(2,  1, 13, 'AirPods Pro 第三代 无线蓝牙耳机',
    '自适应降噪丨空间音频丨MagSafe充电丨IPX4抗汗抗水丨36小时续航',
    1899.00, 100, '/images/products/airpods_pro3.jpg', 1, 356),
(3,  1, 11, '小米14 Ultra 5G智能手机',
    '骁龙8 Gen3丨6.73英寸2K微曲屏丨徕卡光学Summilux镜头丨5000mAh电池',
    5999.00, 80, '/images/products/xiaomi14_ultra.jpg', 1, 210),
(4,  1, 14, 'Anker 65W 氮化镓充电器',
    '三口快充丨GaN技术丨兼容笔记本/平板/手机丨折叠插脚便携设计',
    149.00, 500, '/images/products/anker_gan65.jpg', 1, 1024),
(5,  1, 12, 'iPad mini 7 8.3英寸平板电脑',
    'A17 Pro芯片丨8.3英寸Liquid Retina全面屏丨支持Apple Pencil Pro丨Wi-Fi 256GB',
    4799.00, 30, '/images/products/ipad_mini7.jpg', 1, 89),

-- 美美潮流服饰 (seller_id=2)
(6,  2, 31, '法式复古碎花连衣裙 2024夏季新款',
    'V领泡泡袖丨高腰收腰显瘦丨雪纺面料亲肤透气丨S/M/L/XL码齐全',
    269.00, 200, '/images/products/floral_dress.jpg', 1, 520),
(7,  2, 31, '韩版宽松针织开衫 空调衫防晒衫',
    '纯棉面料柔软舒适丨百搭基础款丨七分袖丨8色可选',
    159.00, 300, '/images/products/knit_cardigan.jpg', 1, 731),
(8,  2, 33, 'Nike Air Force 1 经典空军一号 休闲板鞋',
    '经典复古百搭丨Air缓震科技丨耐磨橡胶外底丨男女同款',
    799.00, 150, '/images/products/nike_af1.jpg', 1, 445),
(9,  2, 32, '简约商务休闲长袖衬衫 免烫抗皱',
    '新疆长绒棉丨液氨免烫工艺丨修身版/标准版可选丨白/蓝/灰3色',
    239.00, 180, '/images/products/biz_shirt.jpg', 1, 332),

-- 松鼠零食小铺 (seller_id=3)
(10, 3, 41, '乐事薯片大礼包 12包装 混合口味',
    '原味/番茄/烧烤/红烩丨家庭分享装丨膨化零食组合',
    49.90, 1000, '/images/products/lays_pack.jpg', 1, 2180),
(11, 3, 41, '三只松鼠 坚果大礼盒 每日坚果混合装',
    '6种坚果+3种果干丨30天不重样丨锁鲜包装丨年货送礼',
    99.00, 600, '/images/products/nuts_giftbox.jpg', 1, 1560),
(12, 3, 42, '隅田川 冷萃咖啡液 0蔗糖 黑咖啡',
    '阿拉比卡豆丨7倍浓缩丨冷水即溶丨0糖0脂丨10条装',
    39.90, 800, '/images/products/coffee_liquid.jpg', 1, 3400);

-- 3.6 商品图片（每个商品配套图）
INSERT INTO `product_images` (`product_id`, `image_url`, `sort_order`) VALUES
(1, '/images/products/matebook_x_pro_detail1.jpg', 1),
(1, '/images/products/matebook_x_pro_detail2.jpg', 2),
(2, '/images/products/airpods_pro3_detail1.jpg', 1),
(3, '/images/products/xiaomi14_ultra_detail1.jpg', 1),
(3, '/images/products/xiaomi14_ultra_detail2.jpg', 2),
(6, '/images/products/floral_dress_detail1.jpg', 1),
(6, '/images/products/floral_dress_detail2.jpg', 2),
(8, '/images/products/nike_af1_detail1.jpg', 1),
(10, '/images/products/lays_pack_detail1.jpg', 1);
