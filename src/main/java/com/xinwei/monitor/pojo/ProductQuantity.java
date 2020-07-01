package com.xinwei.monitor.pojo;

import lombok.Data;

@Data
public class ProductQuantity {

    private String unit;
    private Integer amount;


    public ProductQuantity(Integer amount){
        this.unit = "EACH";
        this.amount = amount;
    }
}
