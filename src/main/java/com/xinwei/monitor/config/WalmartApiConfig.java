package com.xinwei.monitor.config;

import com.google.common.collect.Maps;
import com.xinwei.monitor.pojo.WalmartApi;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "walmart")
@PropertySource("classpath:properties/walmart-api.properties")
public class WalmartApiConfig {

    private Map<String, WalmartApi> api = Maps.newHashMap();

}
