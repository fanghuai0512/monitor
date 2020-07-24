package com.xinwei.monitor;

import com.amazonaws.service.products.model.Product;
import com.google.common.util.concurrent.RateLimiter;
import com.xinwei.monitor.amazon.AmazonClient;
import com.xinwei.monitor.cache.ProductCache;
import com.xinwei.monitor.monogo.AmznProductMonitorRepository;
import com.xinwei.monitor.po.AmznProduct;
import com.xinwei.monitor.po.AmznProductMonitor;
import com.xinwei.monitor.po.Store;
import com.xinwei.monitor.service.AmznMonitorService;
import com.xinwei.monitor.service.AmznProductService;
import com.xinwei.monitor.service.StoreService;
import com.xinwei.monitor.thread.task.MonitorAmazonProductPriceTask;
import com.xinwei.monitor.walmart.WalmartClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
class MonitorApplicationTests {

    @Resource private AmznProductService amznProductService;
    @Resource private AmznMonitorService amznMonitorService;
    @Resource private AmazonClient awsClient;
    @Resource private StoreService storeService;
    @Resource private WalmartClient walmartClient;
    @Resource private AmznProductMonitorRepository monitorRepository;

    @Test
    void contextLoads() {
        //List<AmznProduct> monitorList = amznProductService.findMonitorList(1,20);
        //List<String> asinList = monitorList.subList(1,20).stream().map(AmznProduct::getAsin).collect(Collectors.toList());
        List<String> asinList = Arrays.asList("B00WGMRD2S");
        List<Product> productList = awsClient.getProductPriceByAsin(asinList);
        productList.stream().forEach(product -> {
            //asin值
            String asin = product.getIdentifiers().getMarketplaceASIN().getASIN();
            //设置商品fba和fbm价格
            AmznProductMonitor amznProductMonitor =  ProductCache.get(asin).of();
            amznMonitorService.getProductMinPrice(product,amznProductMonitor);
            amznMonitorService.getMonitorProduct(product,amznProductMonitor);
            amznMonitorService.getSellPrice(amznProductMonitor);
            monitorRepository.save(amznProductMonitor);
            log.debug(amznProductMonitor.getAsin() + "----->"+amznProductMonitor.getSellPrice());
        });

    }

    @Test
    void testWalmart(){
        Store store = storeService.findOne("Walmart-Luckystar");
        walmartClient.updatePrice(store,"Lucky-star-118808-7131", BigDecimal.valueOf(20.89), BigDecimal.valueOf(30));
    }



    @Test
    public void testThreadPool(){
        RateLimiter rateLimiter = RateLimiter.create(19.99);
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        boolean flag = true;
        while (flag){
            System.out.println("等待时间"+rateLimiter.acquire());
            executorService.execute(new MonitorAmazonProductPriceTask());
        }
    }

}

