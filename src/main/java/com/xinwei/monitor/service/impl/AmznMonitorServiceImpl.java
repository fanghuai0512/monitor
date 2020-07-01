package com.xinwei.monitor.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.amazonaws.service.products.model.LowestOfferListingType;
import com.amazonaws.service.products.model.Product;
import com.amazonaws.service.products.model.QualifiersType;
import com.xinwei.monitor.config.AmznProductAdjustRuleConfig;
import com.xinwei.monitor.config.AmznProductFilterRuleConfig;
import com.xinwei.monitor.constant.Constant;
import com.xinwei.monitor.po.AmznProductMonitor;
import com.xinwei.monitor.re.PriceRuleRe;
import com.xinwei.monitor.service.AmznMonitorService;
import com.xinwei.monitor.service.RedisService;
import com.xinwei.monitor.util.BigDecimalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import static com.xinwei.monitor.constant.Status.Product.Down;

@Service
@Slf4j
public class AmznMonitorServiceImpl implements AmznMonitorService {

    @Resource private RedisService redisService;
    @Resource private AmznProductFilterRuleConfig amznProductFilterRuleConfig;
    @Resource private AmznProductAdjustRuleConfig amznProductAdjustRuleConfig;

    /**
     * 监控商品
     * @param product
     */
    @Override
    public void getMonitorProduct(Product product,AmznProductMonitor amznProductMonitor){

        //如果获取价格失败，则直接下架
        if(product == null
                || product.getLowestOfferListings().getLowestOfferListing() == null
                || product.getLowestOfferListings().getLowestOfferListing().size() == 0){
            log.info("=====【{}】获取价格失败 =======",amznProductMonitor.getAsin());
            amznProductMonitor.setActionStatus(Down.getStatus());
            return;
        }

        //1.筛选出价格最低的商品
        this.getProductMinPrice(product,amznProductMonitor);

        //如果继续执行逻辑处理
        if(amznProductMonitor.isContinue()){
            //2.无货状态，进行下次监控处理,下架
            if( amznProductMonitor.getFbmPrice() == null){
                log.info("=====【{}】获取价格中fbm价格为空 =======",amznProductMonitor.getAsin());
                amznProductMonitor.setActionStatus(Down.getStatus());
                amznProductMonitor.setContinue(false);
                return;
            }
            //3.采购价低于最低售价，则直接下架
            if(amznProductMonitor.getFbmPrice().compareTo(amznProductFilterRuleConfig.getSellMinPrice()) == -1 ){
                log.info("=====【{}】获取价格中采购价低于最低售价 =======",amznProductMonitor.getAsin());
                amznProductMonitor.setActionStatus(Down.getStatus());
                amznProductMonitor.setContinue(false);
                return;
            }
            //4.如果存在FBA价格，且价格有转售利润（防止直接从fba转售） fbm*0.85-1
            BigDecimal sellPrice = amznProductMonitor.getFbmPrice().multiply(BigDecimal.valueOf(0.85)).subtract(BigDecimal.valueOf(1));
            if(amznProductMonitor.getFbaPrice() != null && sellPrice.compareTo(amznProductMonitor.getFbaPrice()) == 1){
                log.info("=====【{}】获取价格中存在FBA价格，且价格没有转售利润 =======",amznProductMonitor.getAsin());
                amznProductMonitor.setActionStatus(Down.getStatus());
                amznProductMonitor.setContinue(false);
                return;
            }
        }

    }

    @Override
    public void getSellPrice(AmznProductMonitor amznProductMonitor) {
        if(!amznProductMonitor.isContinue()){
            return;
        }
        Object priceRuleJson = redisService.get(Constant.Redis.PriceRule.getKey());
        List<PriceRuleRe> priceRuleReList = JSONArray.parseArray(String.valueOf(priceRuleJson), PriceRuleRe.class);
        //设置实际售价
        priceRuleReList.stream().forEach(priceRuleRe -> {
            // 最低价 < fbm 价格 <= 最高价，符合该条件
            if(amznProductMonitor.getFbmPrice().compareTo(priceRuleRe.getMinGt()) == 1
                    && amznProductMonitor.getFbmPrice().compareTo(priceRuleRe.getMaxLe()) != 1 ){
                //售价 = fbm 价格 * 利润率 + 上下浮动价
                BigDecimal sellPrice = amznProductMonitor.getFbmPrice().multiply(priceRuleRe.getProfitRate()).add(priceRuleRe.getFloatAdd());
                log.info("===========【{}】 售价为 【{}】",amznProductMonitor.getAsin(),sellPrice.setScale(2, BigDecimal.ROUND_UP));
                amznProductMonitor.setSellPrice(sellPrice.setScale(2, BigDecimal.ROUND_UP));
            }
        });

        //设置原售价
        if(amznProductMonitor.getSellPrice() != null){
            //随机利润率
            BigDecimal random = BigDecimalUtils.getRandom(amznProductAdjustRuleConfig.getOriginPriceMinRate(),
                    amznProductAdjustRuleConfig.getOriginPriceMaxRate());
            //实际售价原价 =  售价 * 随机利润率 （向上取两位小数）
            BigDecimal sellOriginPrice = amznProductMonitor.getSellPrice().multiply(random).setScale(2, BigDecimal.ROUND_UP);
            log.info("===========【{}】 原价为 【{}】",amznProductMonitor.getAsin(),sellOriginPrice.setScale(2, BigDecimal.ROUND_UP));
            amznProductMonitor.setSellOriginPrice(sellOriginPrice);
        }

    }

    /**
     * 从其他供应商中获取商品最低价
     * @param product 其他商家的数据信息（最便宜的价格在最上面）
     * @param amznProductMonitor 监控商品信息
     * 1.美国本土发货
     * 2.物流时间不超过14天或者8-13天
     * 3.正反馈评级不包含‘Less than’和‘Just Launched’且数量超过90
     * 4.卖家反馈计数不低于一百
     */
    @Override
    public void getProductMinPrice(Product product,AmznProductMonitor amznProductMonitor){
        //log.info("=====【{}】获取商品最低价开始======",amznProductMonitor.getAsin());
        List<LowestOfferListingType> offerListingTypeList = product.getLowestOfferListings().getLowestOfferListing();
        if(offerListingTypeList == null || offerListingTypeList.size() == 0){
            amznProductMonitor.setContinue(false);
            return;
        }
        offerListingTypeList.stream().forEach(lowestOfferListingType -> {
            // 渠道
            String fulfillmentChannel = lowestOfferListingType.getQualifiers().getFulfillmentChannel();
            QualifiersType qualifiers = lowestOfferListingType.getQualifiers();
            //如果fba 发货价格
            if("Amazon".equalsIgnoreCase(fulfillmentChannel)
                    && amznProductMonitor.getFbaPrice() == null){

                //设置fba价格
                BigDecimal fbaPrice = lowestOfferListingType.getPrice().getLandedPrice().getAmount();
                amznProductMonitor.setFbaPrice(fbaPrice);
                //log.info("【{}】 监控价格中fba价格为 【{}】",amznProductMonitor.getAsin(),fbaPrice.doubleValue());

            //FBM发货、美国发货、fbm价格为空
            }else if("Merchant".equalsIgnoreCase(fulfillmentChannel)
                    //getShipsDomestically返回值为：True 或 Unknown,True 为美国发货
                    && "True".equalsIgnoreCase(qualifiers.getShipsDomestically())
                    && amznProductMonitor.getFbmPrice() == null){

                //设置为美国物流信息
                amznProductMonitor.setUsShipping(true);

                //设置物流时间
                String shippingTime = qualifiers.getShippingTime().getMax();
                amznProductMonitor.setShippingTime(shippingTime);
                //log.info("【{}】 监控价格中fbm 发货时间【{}】",amznProductMonitor.getAsin(),shippingTime);
                if(shippingTime.contains("14 or more days") || shippingTime.contains("8-13")){
                    amznProductMonitor.setContinue(false);
                }

                // 正反馈评级
                String positiveFeedbackRating = qualifiers.getSellerPositiveFeedbackRating();
                amznProductMonitor.setPositiveFeedbackRating(positiveFeedbackRating);
                //log.info("【{}】 监控价格中fbm 正反馈评级为 【{}】",amznProductMonitor.getAsin(),positiveFeedbackRating);
                if(positiveFeedbackRating.contains("-") && positiveFeedbackRating.contains("%")){
                    positiveFeedbackRating=positiveFeedbackRating
                            .substring(positiveFeedbackRating.lastIndexOf("-")+1,
                                    positiveFeedbackRating.lastIndexOf("%"));
                }
                if(positiveFeedbackRating.contains("Less than")
                        || positiveFeedbackRating.contains("Just Launched")
                        || Integer.parseInt(positiveFeedbackRating)< amznProductFilterRuleConfig.getPositiveFeedbackMinRating()){
                    amznProductMonitor.setContinue(false);
                }

                //卖家反馈计数
                int sellerFeedbackCount = lowestOfferListingType.getSellerFeedbackCount();
                //log.info("【{}】 监控价格中fbm 卖家反馈计数为 【{}】",amznProductMonitor.getAsin(),sellerFeedbackCount);
                amznProductMonitor.setSellerFeedbackCount(sellerFeedbackCount);
                if(sellerFeedbackCount < amznProductFilterRuleConfig.getSellerFeedbackMinCount()){
                    amznProductMonitor.setContinue(false);
                }

                //设置fbm价格
                BigDecimal fbmPrice = lowestOfferListingType.getPrice().getLandedPrice().getAmount();
                //log.info("【{}】 监控价格中fbm价格为 【{}】",amznProductMonitor.getAsin(),fbmPrice.doubleValue());
                if(amznProductMonitor.isContinue()){
                    amznProductMonitor.setFbmPrice(fbmPrice);
                }
            }
        });
        //log.info("=====【{}】获取商品最低价结束======",amznProductMonitor.getAsin());
    }


}
