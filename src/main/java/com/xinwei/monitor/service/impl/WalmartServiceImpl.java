package com.xinwei.monitor.service.impl;

import com.xinwei.monitor.cache.ProductCache;
import com.xinwei.monitor.po.AmznProduct;
import com.xinwei.monitor.po.AmznProductMonitor;
import com.xinwei.monitor.service.WalmartService;
import org.springframework.stereotype.Service;

@Service
public class WalmartServiceImpl implements WalmartService {

    @Override
    public void changeProduct(AmznProductMonitor amznProductMonitor) {
        AmznProduct amznProduct = ProductCache.get(amznProductMonitor.getAsin());

    }
}
