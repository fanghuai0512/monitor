package com.xinwei.monitor.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 亚马逊商品
 */
@Data
@TableName("order_skumanager")
public class AmznProduct implements Serializable {

    @TableId(type = IdType.AUTO)
    private int id;

    @Indexed
    private String sku ;

    @Indexed
    private String asin;

    private String shopname;

    private BigDecimal newPrice;

    private Integer stock;

    private Integer status;

    public AmznProductMonitor of(){
        AmznProductMonitor amznProductMonitor = new AmznProductMonitor();
        BeanUtils.copyProperties(this,amznProductMonitor);
        amznProductMonitor.setMonitorTime(new Date());
        amznProductMonitor.setBeforeStock(this.getStock());
        amznProductMonitor.setBeforeSellPrice(this.getNewPrice());
        amznProductMonitor.setBeforeStatus(this.getStatus());
        return amznProductMonitor;
    }

}
