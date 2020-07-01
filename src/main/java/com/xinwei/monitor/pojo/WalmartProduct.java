package com.xinwei.monitor.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Data
public class WalmartProduct {
    private String sku ;
    private ProductQuantity quantity;
    private List<ProductPrice> pricing;

    /**
     * 商品库存（上架和调整库存）
     * @param sku sku
     * @param amount 数量
     */
    public WalmartProduct(String sku, Integer amount ){
        this.sku = sku ;
        this.quantity = new ProductQuantity(amount);
    }

    /**
     * 调整商品价格
     * @param sku sku
     * @param sellPrice 售价
     * @param originPrice 原价
     */
    public WalmartProduct(String sku, BigDecimal sellPrice,BigDecimal originPrice){
        this.sku = sku ;
        this.pricing = Arrays.asList(new ProductPrice(sellPrice,originPrice));
    }
}
