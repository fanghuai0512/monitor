package com.xinwei.monitor;

import com.amazonaws.service.products.model.Product;
import com.google.common.collect.Lists;
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
import com.xinwei.monitor.util.FileUtils;
import com.xinwei.monitor.walmart.WalmartClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
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
        List<AmznProduct> monitorList = amznProductService.findMonitorList(1,20);
        List<String> asinList = monitorList.subList(1,20).stream().map(AmznProduct::getAsin).collect(Collectors.toList());
        //List<String> asinList = Arrays.asList("B01N650V28");
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

    public static void main(String[] args) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        RequestBuilder requestBuilder = RequestBuilder.create(HttpGet.METHOD_NAME).setUri("http://kjt.hubei.gov.cn/fbjd/tzgg/index.shtml");
        HttpUriRequest request = requestBuilder.build();
        request.setHeader("Cookie","Secure; FSSBBIl1UgzbN7N80S=bqOfVDhHlyqRQBHECUobHxbMUr3aet.VD6PQ1A.ZonlyJqnE1uEhHpsmTAazkqdu; dataHide2=a5efa680-d38e-46cd-8129-a85c70263435; _trs_uv=kc1cg1qk_3003_hzph; _trs_ua_s_1=kc1fhdix_3003_k38u; FSSBBIl1UgzbN7N80T=4bm3mMF3.myUBvUrU2RWDJevf2ipEnM13KZzokHwxSfMFtah1kE6unutrSUZnehruUvVvYLILhGZd3NAdUzJ880oead8F5KQrLL1s84SX.7v8zW3KO7jEyuNyFIDg8.jhWp0l3pgJwGO9jyNs95SdWf8bflH2CNQKlIY5V_hQbL_.QwLDQP2J3ZAY3_Igy1b2QT_1duoEI1quYD_w5PWSxl83f_.UOo8GV9GjYK5DeX1UromD3fBTiL_ZeZltgtbb1KY73Mo5sJMTEm.nY6Ou3d86ZELV.1Wtr605SF84l5fAZHtRdlOzzSfoo.ibH1g7TDfTApLTzMc1GQBWELk4GeZbfyh_WvfP7tisE2xsHS_2HR9DyQcVUIfkabPL0phe.x7FdG4RasUyK.o07jUl6z_z");
        request.setHeader("Host","kjt.hubei.gov.cn");
        request.setHeader("Upgrade-Insecure-Requests","1");
        request.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36");
        HttpResponse httpResponse = httpClient.execute(request);
        int responseStatus = httpResponse.getStatusLine().getStatusCode();
        String string = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
        FileUtils.writeFile("d://tset.txt",string);
    }

}

