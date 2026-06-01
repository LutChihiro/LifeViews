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

CREATE TABLE IF NOT EXISTS diary_record (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日记ID',

                              user_id BIGINT NOT NULL COMMENT '用户ID',

                              title VARCHAR(255) NOT NULL COMMENT '日记标题',
                              content TEXT DEFAULT NULL COMMENT '日记正文',

                              diary_date DATE NOT NULL COMMENT '日记日期',
                              diary_time TIME DEFAULT NULL COMMENT '日记时间',

                              mood TINYINT DEFAULT NULL COMMENT '心情评分：1-5',
                              mood_text VARCHAR(64) DEFAULT NULL COMMENT '心情文字，如开心、平静、焦虑',

                              weather VARCHAR(64) DEFAULT NULL COMMENT '天气，如晴、多云、雨',

                              is_pinned TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：0否，1是',

                              word_count INT NOT NULL DEFAULT 0 COMMENT '字数',

                              status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0草稿，1正常，2删除',

                              created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
                              deleted_at DATETIME DEFAULT NULL COMMENT '删除时间',

                              UNIQUE KEY uk_user_diary_date (user_id, diary_date),
                              KEY idx_user_id (user_id),
                              KEY idx_diary_date (diary_date),
                              KEY idx_user_date (user_id, diary_date),
                              KEY idx_user_updated_at (user_id, updated_at),

                              CONSTRAINT fk_diary_user
                                  FOREIGN KEY (user_id) REFERENCES sys_user(id)
                                      ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日记表';

CREATE TABLE IF NOT EXISTS diary_image (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日记图片ID',

                             diary_id BIGINT NOT NULL COMMENT '日记ID',
                             user_id BIGINT NOT NULL COMMENT '用户ID',

                             image_url VARCHAR(512) NOT NULL COMMENT '图片地址',
                             image_name VARCHAR(255) DEFAULT NULL COMMENT '图片原始名称',

                             sort_order INT NOT NULL DEFAULT 0 COMMENT '图片排序',

                             created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

                             KEY idx_diary_id (diary_id),
                             KEY idx_user_id (user_id),
                             KEY idx_diary_sort (diary_id, sort_order),

                             CONSTRAINT fk_diary_image_diary
                                 FOREIGN KEY (diary_id) REFERENCES diary_record(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_diary_image_user
                                 FOREIGN KEY (user_id) REFERENCES sys_user(id)
                                     ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日记图片表';
