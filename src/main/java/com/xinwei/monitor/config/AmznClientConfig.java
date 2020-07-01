package com.xinwei.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Data
@Component
@PropertySource("classpath:properties/amzn-client.properties")
@ConfigurationProperties(prefix = "amazon.client")
public class AmznClientConfig {

    /** 您的访问密钥编码来标识，亚马逊MWS 使用该编码来查找您的访问密钥。*/
    private String accessKeyId;

    private String secretAccessKey;

    /** 卖家编号*/
    private String sellerId;

    /** 亚马逊卖家对某个开发商的授权 token*/
    private String mwsAuthToken;

    private String appName;

    /** 调用的 API 部分的版本*/
    private String appVersion;

    private String endpoint;

    private String marketplaceId;

    private String itemCondition;

}
