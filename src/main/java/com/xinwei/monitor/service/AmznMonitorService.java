package com.xinwei.monitor.service;

import com.amazonaws.service.products.model.LowestOfferListingType;
import com.amazonaws.service.products.model.Product;
import com.xinwei.monitor.po.AmznProductMonitor;

import java.util.List;

public interface AmznMonitorService {

    /**
     * 监控产品信息
     * @param product
     * @param amznProductMonitor
     */
    void getMonitorProduct(Product product,AmznProductMonitor amznProductMonitor);


    /**
     * 获取产品最低价
     * @param product
     * @param amznProductMonitor
     */
    void getProductMinPrice(Product product, AmznProductMonitor amznProductMonitor);

    /**
     * 获取产品售价
     * @param amznProductMonitor
     */
    void getSellPrice(AmznProductMonitor amznProductMonitor);



}
