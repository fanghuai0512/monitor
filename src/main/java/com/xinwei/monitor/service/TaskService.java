package com.xinwei.monitor.service;

import com.amazonaws.service.products.model.Product;
import com.sun.istack.internal.NotNull;
import com.xinwei.monitor.po.AmznProduct;
import com.xinwei.monitor.po.AmznProductMonitor;

import java.util.List;

public interface TaskService {


    List<AmznProduct> monitorProductToCache();

    /**
     * 监控商品价格
     */
    void monitorAmazonProductPrice();

    /**
     * 处理监控商品信息
     * @param product
     */
    void manageMonitorProduct(@NotNull List<Product> product);

    /**
     * 处理沃尔玛商品
     * <a>逻辑如下:</a>
     * <p>
     * -- 处理状态：下架
     *        --之前上架，则下架，库存设置为0
     *        --之前下架或者不监控，不做处理
     * -- 处理状态：上架
     *        --之前上架，库存是否不足，
     *              --不足 》》》补充库存
     *              --充足 》》》不作处理
     *        --之前下架，则上架
     * </p>
     */
    void manageWalmartProduct(@NotNull List<AmznProductMonitor> amznProductMonitorList);




}
