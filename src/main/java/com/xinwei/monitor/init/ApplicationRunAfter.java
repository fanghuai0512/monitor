package com.xinwei.monitor.init;

import com.alibaba.fastjson.JSONObject;
import com.xinwei.monitor.cache.ProductCache;
import com.xinwei.monitor.cache.StoreCache;
import com.xinwei.monitor.po.AmznProduct;
import com.xinwei.monitor.po.Store;
import com.xinwei.monitor.re.PriceRuleRe;
import com.xinwei.monitor.service.AmznProductService;
import com.xinwei.monitor.service.PriceRuleService;
import com.xinwei.monitor.service.RedisService;
import com.xinwei.monitor.service.StoreService;
import com.xinwei.monitor.util.ThreadPoolUtils;
import lombok.SneakyThrows;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.xinwei.monitor.constant.Constant.Redis.PriceRule;

/**
 * 容器运行之后执行方法
 * @author fangh
 */
@Component
public class ApplicationRunAfter implements ApplicationRunner {

    @Resource private AmznProductService amznProductService;
    @Resource private PriceRuleService priceRuleService;
    @Resource private RedisService redisService;
    @Resource private StoreService storeService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
       /* //获取监控数据,将监控数据存放在内存中
        List<AmznProduct> monitorList = amznProductService.findMonitorList();
        if(monitorList != null && monitorList.size() != 0 ){
            monitorList.stream().forEach(amznProduct -> {
                ProductCache.addMonitorProduct(amznProduct);
            });
        }*/

        //将价格规则放入redis中
        List<PriceRuleRe> priceRuleReList = priceRuleService.findList();
        String jsonString = JSONObject.toJSONString(priceRuleReList);
        redisService.put(PriceRule.getKey(), jsonString ,PriceRule.getExpire());

        //将商店放入缓存中
        List<Store> storeList = storeService.list();
        storeList.stream().forEach(store -> {
            StoreCache.add(store);
        });

        //启动线程池
        new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadPoolUtils.startProductToCache();
            }
        }).start();

        //启动线程池
        new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadPoolUtils.startMonitorProduct();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadPoolUtils.startManageProduct();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadPoolUtils.startWalmartProduct();
            }
        }).start();

    }
}
