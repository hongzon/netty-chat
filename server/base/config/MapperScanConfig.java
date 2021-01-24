package com.nettychat.server.base.config;

import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.annotation.MapperScan;


@Configuration
@MapperScan(basePackages = "com.nettychat.server.mapper")
public class MapperScanConfig {
}
