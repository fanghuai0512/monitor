package com.xinwei.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix = "monitor")
@PropertySource("classpath:properties/monitor.properties")
public class MonitorConfig {

    /**
     * 监控商品的起始页
     */
    private Integer startNum;

    /**
     * 监控商品的结束页
     */
    private Integer endNum;
}
