package com.xinwei.monitor.cache;

import com.amazonaws.service.products.model.Product;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xinwei.monitor.po.AmznProduct;
import com.xinwei.monitor.po.AmznProductMonitor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class ProductCache {

    public final static Map<String, AmznProduct> productMap = Maps.newHashMap();
    /** 商品监控列表，主要是需要监控的商品列表*/
    public final static LinkedList<AmznProduct> productMonitorList = Lists.newLinkedList();
    /** 商品处理列表，主要是用来提取监控价格商品信息筛选*/
    public final static LinkedList<Product> productManageList =  Lists.newLinkedList();
    /** 沃尔玛商品列表，主要是监控商品赛选完成后需要上下架或者需要修改库存的商品*/
    public final static LinkedList<AmznProductMonitor> productWalmartList = Lists.newLinkedList();

    /**
     * 添加需要监控的商品
     * @param amznProduct
     */
    public final static void addMonitorProduct(AmznProduct amznProduct){
        productMap.put(amznProduct.getAsin(),amznProduct);
        productMonitorList.add(amznProduct);
    }

    /**
     * 通过asin 获取监控商品信息
     * @param asin
     * @return
     */
    public final static AmznProduct get(String asin){
        if(productMap != null){
            return productMap.get(asin);
        }
        return null;
    }

    /**
     * 获取一定数量的监控商品，先进先出
     * @param count
     * @return
     */
    public final static synchronized List<AmznProduct> getMonitorProduct(Integer count){
        List<AmznProduct> amznProductList = Lists.newLinkedList();
        if(productMonitorList == null){
            return amznProductList;
        }
        if(productMonitorList.size() <= count ){
            amznProductList.addAll(productMonitorList);
            return amznProductList;
        }else{
            for(int i =  0 ; i < count ; i++){
                AmznProduct monitorListFirst = productMonitorList.getFirst();
                productMonitorList.removeFirst();
                productMonitorList.addLast(monitorListFirst);
                amznProductList.add(monitorListFirst);
            }
        }
        return amznProductList;
    }


    /**
     * 获取要处理的商品
     * @param count
     * @return
     */
    public final static synchronized List<Product> getManageProduct(Integer count){
        List<Product> amznProductList = Lists.newLinkedList();
        if(productManageList == null){
            return amznProductList;
        }
        if( productManageList.size() <= count){
            amznProductList.addAll(productManageList);
            productManageList.removeAll(amznProductList);
            return amznProductList;
        }
        for(int i =  0 ; i < count ; i++) {
            if(count < productManageList.size()){
                Product product = productManageList.getFirst();
                productManageList.removeFirst();
                amznProductList.add(product);
            }
        }
        return amznProductList;
    }

    /**
     * 移出监控商品信息
     * @param asin
     */
    public final static void remove(String asin){
        if(productMap != null){
            AmznProduct amznProduct = get(asin);
            productMonitorList.remove(amznProduct);
            productMap.remove(asin);
        }
    }


    /**
     * 添加需要处理的监控商品
     * @param productList
     */
    public final static synchronized void addManageProduct(List<Product> productList){
        productList.stream().forEach(product -> {
            productManageList.add(product);
        });
    }


    /**
     * 添加沃尔玛商品
     * @param amznProductMonitor
     */
    public final static void addWalmartProduct(AmznProductMonitor amznProductMonitor){
        productWalmartList.add(amznProductMonitor);
    }

    /**
     * 获取沃尔玛商品信息
     * @return
     */
    public final static synchronized List<AmznProductMonitor> getAmznProductList(Integer count){
        List<AmznProductMonitor> amznProductMonitorList = Lists.newLinkedList();
        if(productWalmartList ==null || productWalmartList.size() ==0){
            return amznProductMonitorList;
        }
        if(productWalmartList.size() <= count){
            amznProductMonitorList.addAll(productWalmartList);
            productWalmartList.removeAll(amznProductMonitorList);
            return amznProductMonitorList;
        }
        for (int i = 0; i < count; i++) {
            AmznProductMonitor amznProductMonitor = productWalmartList.getFirst();
            productWalmartList.removeFirst();
            amznProductMonitorList.add(amznProductMonitor);
        }
        return amznProductMonitorList;
    }
}
