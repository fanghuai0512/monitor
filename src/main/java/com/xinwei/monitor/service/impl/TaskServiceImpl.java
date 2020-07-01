package com.xinwei.monitor.service.impl;

import com.amazonaws.service.products.model.Product;
import com.xinwei.monitor.amazon.AmazonClient;
import com.xinwei.monitor.cache.ProductCache;
import com.xinwei.monitor.monogo.AmznProductMonitorRepository;
import com.xinwei.monitor.po.AmznProduct;
import com.xinwei.monitor.po.AmznProductMonitor;
import com.xinwei.monitor.po.Store;
import com.xinwei.monitor.pojo.WalmartProduct;
import com.xinwei.monitor.service.AmznMonitorService;
import com.xinwei.monitor.service.AmznProductService;
import com.xinwei.monitor.service.StoreService;
import com.xinwei.monitor.service.TaskService;
import com.xinwei.monitor.util.BigDecimalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.xinwei.monitor.constant.Status.Product.Down;
import static com.xinwei.monitor.constant.Status.Product.Up;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Resource private AmznMonitorService amznMonitorService;
    @Resource private StoreService storeService;
    @Resource private AmznProductMonitorRepository monitorRepository;
    @Resource private AmazonClient amazonClient;
    @Resource private AmznProductService productService;
    private final AtomicInteger atomicInteger =  new AtomicInteger(1);

    @Override
    public List<AmznProduct> monitorProductToCache() {
        List<AmznProduct> monitorList = productService.findMonitorList(atomicInteger.getAndIncrement(), 1000);
        monitorList.stream().forEach(amznProduct -> {
            ProductCache.addMonitorProduct(amznProduct);
        });
        return monitorList;
    }

    @Override
    public void monitorAmazonProductPrice() {
        //从监控产品内存中获取20个监控产品的asin值
        List<AmznProduct> amznProducts = ProductCache.getMonitorProduct(10);
        List<String> asinList = amznProducts.stream().map(AmznProduct::getAsin).collect(Collectors.toList());
        //调取接口获取监控价格列表
        List<Product> productList = amazonClient.getProductPriceByAsin(asinList);
        //将商品放入监控商品处理内存中，交给 ProductManageTask 去处理
        ProductCache.addManageProduct(productList);
    }

    @Override
    public void manageMonitorProduct(List<Product> productList) {
        productList.stream().forEach(product -> {
            //asin值
            String asin = product.getIdentifiers().getMarketplaceASIN().getASIN();
            //设置商品fba和fbm价格
            AmznProductMonitor amznProductMonitor =  ProductCache.get(asin).of();
            //获取最低价
            amznMonitorService.getProductMinPrice(product,amznProductMonitor);
            //筛选监控商品信息
            amznMonitorService.getMonitorProduct(product,amznProductMonitor);
            //根据规则获取售价
            amznMonitorService.getSellPrice(amznProductMonitor);
            log.info("asin值为【{}】监控前情况==》监控前状态为【{}】，售价为【{}】，库存数【{}】",
                    amznProductMonitor.getAsin(),
                    amznProductMonitor.getBeforeStatus() == 1 ?"上架":"下架",
                    amznProductMonitor.getBeforeSellPrice(),
                    amznProductMonitor.getBeforeStock());
            log.info("asin值为【{}】监控后情况==》监控状态为【{}】，售价为【{}】，原价【{}】，监控fbm价格为【{}】，fba价格为【{}】，发货时时【{}】，好评率【{}】，评论数【{}】",
                    amznProductMonitor.getAsin(),
                    amznProductMonitor.getActionStatus() == 1 ?"上架":"下架",
                    amznProductMonitor.getSellPrice(),
                    amznProductMonitor.getSellOriginPrice(),
                    amznProductMonitor.getFbmPrice(),
                    amznProductMonitor.getFbaPrice(),
                    amznProductMonitor.getShippingTime(),
                    amznProductMonitor.getPositiveFeedbackRating(),
                    amznProductMonitor.getSellerFeedbackCount());
            //添加到沃尔玛商品处理中心线程池中
            ProductCache.addWalmartProduct(amznProductMonitor);
        });
    }

    @Override
    public void manageWalmartProduct(List<AmznProductMonitor> amznProductMonitorList) {
        amznProductMonitorList.stream().forEach(amznProductMonitor -> {
            Store store = storeService.findOne(amznProductMonitor.getShopname());
            //监控之后下架，则下架
            if(Down.getStatus() == amznProductMonitor.getActionStatus()){
                //如果之前商品是上架状态，设置下架，并将库存设置为 0
                if(Up.getStatus() == amznProductMonitor.getBeforeStatus()){
                    //boolean result = walmartClient.updateStock(store, amznProductMonitor.getSku(), 0);
                    //String msg = result == true ? "成功" : "失败";
                    String msg = "成功";
                    log.info("===========================【{}】下架【{}】================================",amznProductMonitor.getAsin(),msg);
                }

                //如果监控之后上架，进行上架
            }else if (Up.getStatus() == amznProductMonitor.getActionStatus()){
                //如果之前商品是下架状态或者库存不足，都需要更新库存
                if(Down.getStatus() == amznProductMonitor.getBeforeStatus()
                        || amznProductMonitor.getBeforeStock() < 6){
                    BigDecimal stock = BigDecimalUtils.getRandom(BigDecimal.valueOf(36), BigDecimal.valueOf(60)).setScale(0);
                    amznProductMonitor.setSellStock(stock.intValue());
                    //boolean result = walmartClient.updateStock(store,amznProductMonitor.getSku(),stock.intValue());
                    //String msg = result == true ? "成功" : "失败";
                    String msg = "成功";
                    log.info("===========================【{}】更新库存【{}】{}================================",amznProductMonitor.getAsin(),stock.intValue(),msg);
                }
            }
        });
        monitorRepository.saveAll(amznProductMonitorList);
    }
}
