CREATE TABLE IF NOT EXISTS movie_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '观影记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    movie_name VARCHAR(255) NOT NULL COMMENT '电影名称',
    poster_url VARCHAR(512) DEFAULT NULL COMMENT '电影海报地址',
    category VARCHAR(128) DEFAULT NULL COMMENT '电影类型，如喜剧、爱情、科幻、动画',
    region VARCHAR(64) DEFAULT NULL COMMENT '国家或地区，如中国、日本、美国',
    release_year INT DEFAULT NULL COMMENT '上映年份',
    watch_date DATE DEFAULT NULL COMMENT '观看日期',
    watch_place VARCHAR(255) DEFAULT NULL COMMENT '观看地点，如电影院、家、学校、朋友家',
    rating DECIMAL(3,1) DEFAULT NULL COMMENT '0-10分评分',
    review TEXT DEFAULT NULL COMMENT '观后感',
    watch_status TINYINT NOT NULL DEFAULT 1 COMMENT '观看状态：0想看，1已看',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '记录状态：1正常，2删除',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    KEY idx_movie_user_id (user_id),
    KEY idx_movie_user_watch_date (user_id, watch_date),
    KEY idx_movie_user_rating (user_id, rating),
    KEY idx_movie_user_status (user_id, status),
    KEY idx_movie_user_watch_status (user_id, watch_status),
    KEY idx_movie_user_updated_at (user_id, updated_at),

    CONSTRAINT fk_movie_user
        FOREIGN KEY (user_id) REFERENCES sys_user(id)
            ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='观影记录表';
