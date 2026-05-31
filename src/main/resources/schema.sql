CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',

    username VARCHAR(64) NOT NULL COMMENT '用户名，用于登录',
    password VARCHAR(255) NOT NULL COMMENT '密码，存储BCrypt加密后的密文',

    nickname VARCHAR(64) DEFAULT NULL COMMENT '用户昵称',
    avatar_url VARCHAR(512) DEFAULT NULL COMMENT '头像地址',

    email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(32) DEFAULT NULL COMMENT '手机号',

    status TINYINT NOT NULL DEFAULT 1 COMMENT '用户状态：1正常，0禁用',

    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',

    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_phone (phone),
    KEY idx_status (status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
