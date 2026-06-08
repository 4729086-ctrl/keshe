package com.shanxi.shopping.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 配置 — 扫描 Mapper 接口
 */
@Configuration
@MapperScan("com.shanxi.shopping.mapper")
public class MyBatisConfig {
}
