package com.lifeviews;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.lifeviews.mapper")
@SpringBootApplication
public class LifeViewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifeViewsApplication.class, args);
    }
}
