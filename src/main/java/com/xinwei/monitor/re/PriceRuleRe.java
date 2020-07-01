package com.xinwei.monitor.re;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 价格规则
 */
@Data
public class PriceRuleRe {

    /**
     * 采购价大于最小值
     */
    private BigDecimal minGt;

    /**
     * 采购价小于最大值
     */
    private BigDecimal maxLe;

    /**
     * 利润值
     */
    private BigDecimal profitRate;

    /**
     * 上下浮动值
     */
    private BigDecimal floatAdd ;

}
