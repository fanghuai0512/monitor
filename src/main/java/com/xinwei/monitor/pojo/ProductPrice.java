package com.xinwei.monitor.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductPrice {

    /**
     * 当前价格类型
     */
    private String currentPriceType;

    /**
     * 当前价格
     */
    private CurrentPrice currentPrice;

    /**
     * 比较价格类型
     */
    private String comparisonPriceType;

    /**
     * 比较价格
     */
    private CurrentPrice comparisonPrice;

    /**
     *
     * @param sellPrice 售价
     * @param originPrice 原价
     */
    public ProductPrice(BigDecimal sellPrice, BigDecimal originPrice){

        this.currentPriceType = "REDUCED";
        this.currentPrice = new CurrentPrice(sellPrice);
        this.comparisonPriceType = "BASE";
        this.comparisonPrice = new CurrentPrice(originPrice);
    }
}
