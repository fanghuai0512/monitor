package com.xinwei.monitor.thread.task;

import com.amazonaws.service.products.model.Product;
import com.xinwei.monitor.cache.ProductCache;
import com.xinwei.monitor.service.TaskService;
import com.xinwei.monitor.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;

@Slf4j
public class ManageMonitorProductTask implements Runnable{

    @Override
    public void run() {
        try{
            TaskService taskService = SpringUtil.getBean(TaskService.class);
            List<Product> manageProductList = ProductCache.getManageProduct(20);
            if(manageProductList != null && manageProductList.size() != 0){
                taskService.manageMonitorProduct(manageProductList);
            }
        }catch(Exception e){
            log.error("处理监控商品发生异常："+ ExceptionUtils.getStackTrace(e));
        }
    }

}
