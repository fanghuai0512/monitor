package com.xinwei.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.math.BigDecimal;

/**
 * 亚马逊价格调整规则
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "amzn.adjust.rule")
@PropertySource("classpath:properties/amzn-product-adjust-rule.properties")
public class AmznProductAdjustRuleConfig {

    /**
     * 产品上架最小库存
     */
    private Integer productMinStock;

    /**
     * 产品上架最大库存
     */
    private Integer productMaxStock;

    /**
     * 产品售价原价最小利率（售价原价 = 售价 * 利率）
     */
    private BigDecimal originPriceMinRate;

    /**
     * 产品售价原价最大利率（售价原价 = 售价 * 利率）
     */
    private BigDecimal originPriceMaxRate;


}
