package com.xinwei.monitor.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrentPrice {

    /**
     * 货币
     */
    private String currency;

    /**
     * 数量（单价）
     */
    private BigDecimal amount;

    public CurrentPrice(BigDecimal price){
        this.currency = "USD";
        this.amount = price;
    }
}
