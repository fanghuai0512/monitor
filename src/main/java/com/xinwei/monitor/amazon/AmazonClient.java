package com.xinwei.monitor.amazon;


import com.amazonaws.service.products.MarketplaceWebServiceProductsAsyncClient;
import com.amazonaws.service.products.MarketplaceWebServiceProductsConfig;
import com.amazonaws.service.products.model.*;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.xinwei.monitor.config.AmznClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Component
public class AmazonClient {

    @Resource
    private AmznClientConfig awsConfig ;

    private final static RateLimiter competitivePriceLimiter = RateLimiter.create(20);

    /**
     * 根据asin 获取价格最低的在售商品的价格信息
     * @limit 限制访问每秒20个请求
     * @param asinList asin集合,最多二十个asin
     * @return  价格最低的在售商品的价格信息
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<Product> getProductPriceByAsin(List<String> asinList)  {
        List<Product> productList = Lists.newArrayList();
        //配置
        MarketplaceWebServiceProductsConfig config = new MarketplaceWebServiceProductsConfig();
        config.setServiceURL(awsConfig.getEndpoint());
        competitivePriceLimiter.acquire();
        //创建产品异步客户端,获取api连接
        MarketplaceWebServiceProductsAsyncClient client = new MarketplaceWebServiceProductsAsyncClient(awsConfig.getAccessKeyId(),
                awsConfig.getSecretAccessKey(), awsConfig.getAppName(), awsConfig.getAppVersion(), config);
        //创建请求
        GetLowestOfferListingsForASINRequest request = new GetLowestOfferListingsForASINRequest();
        request.setSellerId(awsConfig.getSellerId());
        request.setMWSAuthToken(awsConfig.getMwsAuthToken());
        request.setMarketplaceId(awsConfig.getMarketplaceId());
        ASINListType asinListType = new ASINListType();
        asinListType.setASIN(asinList);
        request.setASINList(asinListType);
        request.setItemCondition(awsConfig.getItemCondition());
        request.setExcludeMe(true);
        //根据 ASIN，返回价格最低的在售商品的价格信息。
        Future<GetLowestOfferListingsForASINResponse> futureList = client.getLowestOfferListingsForASINAsync(request);
        try {
            GetLowestOfferListingsForASINResponse response = futureList.get();
            List<GetLowestOfferListingsForASINResult> resultList = response.getGetLowestOfferListingsForASINResult();
            for (GetLowestOfferListingsForASINResult result : resultList) {
                String status = result.getStatus();
                if (status.equalsIgnoreCase("Success")) {
                    productList.add(result.getProduct());
                }else {
                    log.error( ExceptionUtils.getStackTrace(result.getError().getCause()));
                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        } catch (ExecutionException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return productList;
    }

}
