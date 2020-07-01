package com.xinwei.monitor.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

import static com.xinwei.monitor.constant.Status.Product.Down;

/**
 * 亚马逊监控产品信息
 */
@Data
@NoArgsConstructor
@Document(collection = "monitor_amzn_product_note")
public class AmznProductMonitor {

    @Indexed
    private String sku ;

    @Indexed
    private String asin;

    private String shopname;

    private BigDecimal beforeSellPrice;

    private Integer beforeStock ;

    private Integer beforeStatus;

    /** 是否继续*/
    @Transient
    private boolean isContinue = true;
    /** 美国物流*/
    @Field("is_us_shipping")
    private boolean usShipping = false ;
    /** 物流时间*/
    @Field("shipping_time")
    private String shippingTime;
    /** fba价格*/
    @Field("fba_price")
    private BigDecimal fbaPrice;
    /** fbm价格*/
    @Field("fbm_price")
    private BigDecimal fbmPrice;
    /** 评级*/
    @Field("positive_rate")
    private String positiveFeedbackRating;
    /** 卖家反馈数 */
    @Field("seller_comment")
    private Integer sellerFeedbackCount;
    /** 售价*/
    @Field("sell_price")
    private BigDecimal sellPrice;
    /** 原售价*/
    @Field("sell_origin_price")
    private BigDecimal sellOriginPrice;
    @Field("sell_stock")
    private Integer sellStock;
    /**
     * 评级筛选后的状态
     */
    @Field("monitor_status")
    private Integer actionStatus = Down.getStatus();

    @Field("monitor_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss:SS")
    private Date monitorTime;

}
