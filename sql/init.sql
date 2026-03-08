-- ============================================================
-- 农家乐线上预约管理系统 - 数据库初始化脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS farmstay_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE farmstay_db;

SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;

-- -----------------------------------------------------------
-- 1. 用户表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名/登录账号',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    nickname VARCHAR(50) COMMENT '昵称',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(500) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER-普通用户, ADMIN-管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- -----------------------------------------------------------
-- 2. 农家乐表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS farmhouse;
CREATE TABLE farmhouse (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '农家乐ID',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    description TEXT COMMENT '详细描述',
    short_desc VARCHAR(500) COMMENT '简短描述',
    address VARCHAR(255) COMMENT '地址',
    phone VARCHAR(20) COMMENT '联系电话',
    cover_image VARCHAR(500) COMMENT '封面图片URL',
    images TEXT COMMENT '图片列表(JSON数组)',
    owner_name VARCHAR(50) COMMENT '经营者姓名',
    rating DECIMAL(2,1) DEFAULT 0.0 COMMENT '平均评分(1-5)',
    review_count INT DEFAULT 0 COMMENT '评价数量',
    latitude DECIMAL(10,7) COMMENT '纬度',
    longitude DECIMAL(10,7) COMMENT '经度',
    tags VARCHAR(500) COMMENT '标签(逗号分隔)',
    features TEXT COMMENT '特色/设施(JSON数组)',
    business_hours VARCHAR(100) COMMENT '营业时间',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_name (name),
    INDEX idx_status (status),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='农家乐表';

-- -----------------------------------------------------------
-- 3. 套餐/项目表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS farmhouse_package;
CREATE TABLE farmhouse_package (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '套餐ID',
    farmhouse_id BIGINT NOT NULL COMMENT '所属农家乐ID',
    name VARCHAR(100) NOT NULL COMMENT '套餐名称',
    description TEXT COMMENT '套餐描述',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    cover_image VARCHAR(500) COMMENT '封面图片',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型: 1-住宿, 2-餐饮, 3-活动体验, 4-综合套餐',
    capacity INT DEFAULT 1 COMMENT '可容纳人数',
    duration VARCHAR(50) COMMENT '时长描述(如: 1晚, 2小时)',
    includes TEXT COMMENT '包含内容(JSON数组)',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_farmhouse_id (farmhouse_id),
    INDEX idx_type (type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='套餐/项目表';

-- -----------------------------------------------------------
-- 4. 档期/名额表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS schedule;
CREATE TABLE schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '档期ID',
    package_id BIGINT NOT NULL COMMENT '所属套餐ID',
    farmhouse_id BIGINT NOT NULL COMMENT '所属农家乐ID',
    schedule_date DATE NOT NULL COMMENT '日期',
    total_quota INT NOT NULL DEFAULT 10 COMMENT '总名额',
    remaining_quota INT NOT NULL DEFAULT 10 COMMENT '剩余名额',
    price_override DECIMAL(10,2) COMMENT '当日特价(为空则使用套餐默认价格)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-不可预约, 1-可预约',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE INDEX uk_package_date (package_id, schedule_date),
    INDEX idx_farmhouse_id (farmhouse_id),
    INDEX idx_schedule_date (schedule_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='档期/名额表';

-- -----------------------------------------------------------
-- 5. 预约表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS reservation;
CREATE TABLE reservation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    reservation_no VARCHAR(32) NOT NULL UNIQUE COMMENT '预约编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    farmhouse_id BIGINT NOT NULL COMMENT '农家乐ID',
    package_id BIGINT NOT NULL COMMENT '套餐ID',
    schedule_id BIGINT NOT NULL COMMENT '档期ID',
    reserve_date DATE NOT NULL COMMENT '预约日期',
    person_count INT NOT NULL DEFAULT 1 COMMENT '预约人数',
    contact_name VARCHAR(50) COMMENT '联系人姓名',
    contact_phone VARCHAR(20) COMMENT '联系人电话',
    remark VARCHAR(500) COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待确认, 1-已确认, 2-已取消, 3-已完成',
    cancel_reason VARCHAR(500) COMMENT '取消原因',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_farmhouse_id (farmhouse_id),
    INDEX idx_status (status),
    INDEX idx_reserve_date (reserve_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约表';

-- -----------------------------------------------------------
-- 6. 订单表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    reservation_id BIGINT COMMENT '关联预约ID',
    farmhouse_id BIGINT NOT NULL COMMENT '农家乐ID',
    package_id BIGINT NOT NULL COMMENT '套餐ID',
    farmhouse_name VARCHAR(100) COMMENT '农家乐名称(冗余)',
    package_name VARCHAR(100) COMMENT '套餐名称(冗余)',
    person_count INT NOT NULL DEFAULT 1 COMMENT '人数',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待支付, 1-已支付, 2-已取消, 3-退款中, 4-已退款, 5-已完成',
    payment_method VARCHAR(20) COMMENT '支付方式: WECHAT/ALIPAY/BALANCE',
    pay_time DATETIME COMMENT '支付时间',
    cancel_time DATETIME COMMENT '取消时间',
    cancel_reason VARCHAR(500) COMMENT '取消/退款原因',
    complete_time DATETIME COMMENT '完成时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_farmhouse_id (farmhouse_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- -----------------------------------------------------------
-- 7. 支付记录表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS payment;
CREATE TABLE payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付记录ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    payment_method VARCHAR(20) NOT NULL COMMENT '支付方式',
    transaction_no VARCHAR(64) COMMENT '第三方交易号',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待支付, 1-支付成功, 2-支付失败, 3-已退款',
    pay_time DATETIME COMMENT '支付时间',
    refund_time DATETIME COMMENT '退款时间',
    refund_amount DECIMAL(10,2) COMMENT '退款金额',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_order_no (order_no),
    INDEX idx_transaction_no (transaction_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- -----------------------------------------------------------
-- 8. 评价表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS review;
CREATE TABLE review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    farmhouse_id BIGINT NOT NULL COMMENT '农家乐ID',
    order_id BIGINT COMMENT '关联订单ID',
    rating TINYINT NOT NULL DEFAULT 5 COMMENT '评分(1-5)',
    content TEXT COMMENT '评价内容',
    images TEXT COMMENT '评价图片(JSON数组)',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待审核, 1-已通过, 2-已拒绝',
    admin_reply TEXT COMMENT '管理员回复',
    reply_time DATETIME COMMENT '回复时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_farmhouse_id (farmhouse_id),
    INDEX idx_order_id (order_id),
    INDEX idx_status (status),
    INDEX idx_rating (rating)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- -----------------------------------------------------------
-- 9. 收藏表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS favorite;
CREATE TABLE favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    farmhouse_id BIGINT NOT NULL COMMENT '农家乐ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE INDEX uk_user_farmhouse (user_id, farmhouse_id),
    INDEX idx_user_id (user_id),
    INDEX idx_farmhouse_id (farmhouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- -----------------------------------------------------------
-- 10. 公告/活动表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS announcement;
CREATE TABLE announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型: 1-公告, 2-活动',
    cover_image VARCHAR(500) COMMENT '封面图片',
    start_time DATETIME COMMENT '活动开始时间',
    end_time DATETIME COMMENT '活动结束时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-下架, 1-发布',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_type (type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告/活动表';

-- -----------------------------------------------------------
-- 11. 消息通知表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS notification;
CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型: 1-系统通知, 2-预约通知, 3-订单通知, 4-评价通知',
    is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知表';

-- -----------------------------------------------------------
-- 12. 审计日志表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS audit_log;
CREATE TABLE audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '操作用户ID',
    username VARCHAR(50) COMMENT '操作用户名',
    action VARCHAR(50) NOT NULL COMMENT '操作类型',
    module VARCHAR(50) COMMENT '操作模块',
    description VARCHAR(500) COMMENT '操作描述',
    ip VARCHAR(50) COMMENT '操作IP',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- -----------------------------------------------------------
-- 13. 系统设置表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS system_setting;
CREATE TABLE system_setting (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '设置ID',
    setting_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    setting_value TEXT COMMENT '配置值',
    description VARCHAR(500) COMMENT '配置描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_setting_key (setting_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统设置表';
