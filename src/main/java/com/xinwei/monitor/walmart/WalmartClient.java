package com.xinwei.monitor.walmart;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.xinwei.monitor.config.WalmartApiConfig;
import com.xinwei.monitor.po.Store;
import com.xinwei.monitor.pojo.WalmartProduct;
import com.xinwei.monitor.pojo.WalmartApi;
import com.xinwei.monitor.service.RedisService;
import com.xinwei.monitor.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import static com.xinwei.monitor.constant.Constant.Redis.WalmartStoreAccessToken;
import static com.xinwei.monitor.constant.Constant.WalmartApi.*;

@Slf4j
@Component
public class WalmartClient {

    @Resource WalmartApiConfig walmartApiConfig;
    @Resource private RedisService redisService;

    /**
     * 更新库存
     * @param store 店铺信息
     * @return
     */
    public boolean updateStock( Store store ,String sku ,Integer amount)  {
        //获取更新库存信息
        WalmartApi walmartApi = walmartApiConfig.getApi().get(UpdateStock.getName());
        //json数据
        String inventory = JSONObject.toJSONString(new WalmartProduct(sku,amount));
        //获取token
        String assessToken = this.getToken(store);
        log.info("======================= Walmart 开始获取更新商品库存 =============================");
        String result = HttpClientUtils.walmartRequest(walmartApi.getUrl(),
                walmartApi.getRequestType(),
                walmartApi.getContentType(), assessToken, store.getClientId(), store.getClientSecret(), null,inventory);
        if(StringUtils.isEmpty(result)){
            log.error("更新库存===================请求沃尔玛服务器异常==========================");
            return false;
        }
        log.info("======================= Walmart 更新库存成功 =============================");
        return true;
    }

    /**
     * 获取token
     * @param store 店铺信息
     * @return
     */
    public String getToken(Store store){
        Object token = redisService.get(WalmartStoreAccessToken.getKey()+store.getName());
        if(token  != null  ){
            return String.valueOf(token);
        }
        log.info("======================= Walmart 开始获取accessToken =============================");

        Map<String,String> paramMap = Maps.newHashMap();
        paramMap.put("grant_type","client_credentials");
        WalmartApi walmartApi = walmartApiConfig.getApi().get(GetToken.getName());
        String result = HttpClientUtils.walmartRequest(walmartApi.getUrl(),
                walmartApi.getRequestType(),
                walmartApi.getContentType(), null, store.getClientId(), store.getClientSecret(), paramMap,null);
        if(StringUtils.isEmpty(result)){
            log.error("获取token===================请求沃尔玛服务器异常==========================");
            return null;
        }
        Map map = JSONObject.parseObject(result, Map.class);
        String accessToken = String.valueOf(map.get("access_token"));
        log.info("=========== Walmart 获取token成功 【%tT%n】 【%s】=================",new Date(),accessToken);

        //将accessToken 放入 redis 中
        redisService.put(WalmartStoreAccessToken.getKey()+store.getName(),accessToken,WalmartStoreAccessToken.getExpire());
        return accessToken;
    }

    /**
     * 更新价钱
     * @param store 商店
     * @param sku sku
     * @param sellPrice 售价
     * @param originPrice 原价
     * @return
     */
    public boolean updatePrice(Store store, String sku, BigDecimal sellPrice,BigDecimal originPrice){
        WalmartApi walmartApi = walmartApiConfig.getApi().get(UpdatePrice.getName());
        String productPrice = JSONObject.toJSONString(new WalmartProduct(sku,sellPrice,originPrice));
        String assessToken = this.getToken(store);
        String result = HttpClientUtils.walmartRequest(walmartApi.getUrl(),
                walmartApi.getRequestType(),
                walmartApi.getContentType(), assessToken, store.getClientId(), store.getClientSecret(), null,productPrice);
        if(StringUtils.isEmpty(result)){
            log.error("更新商品价格失败===================请求沃尔玛服务器异常==========================");
            return false;
        }
        return true;
    }

}
