package com.xinwei.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.math.BigDecimal;

/**
 * 亚马逊过滤规则
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "amazon.filter.rule")
@PropertySource("classpath:properties/amzn-product-filter-rule.properties")
public class AmznProductFilterRuleConfig {

    /**
     * 售价最低价
     */
    private BigDecimal sellMinPrice;

    /**
     * 正反馈最低评级
     */
    private Integer positiveFeedbackMinRating;

    /**
     * 卖家反馈最低计数
     */
    private Integer sellerFeedbackMinCount;
}
