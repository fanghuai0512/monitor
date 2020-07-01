package com.xinwei.monitor.thread.task;

import com.xinwei.monitor.cache.ProductCache;
import com.xinwei.monitor.po.AmznProductMonitor;
import com.xinwei.monitor.service.TaskService;
import com.xinwei.monitor.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 处理沃尔玛商品任务
 */
@Slf4j
public class ManageWalmartProductTask implements Runnable{


    @Override
    public void run() {
        try {
            TaskService taskService = SpringUtil.getBean(TaskService.class);
            List<AmznProductMonitor> amznProductMonitorList = ProductCache.getAmznProductList(20);
            if(amznProductMonitorList != null && amznProductMonitorList.size() != 0){
                taskService.manageWalmartProduct(amznProductMonitorList);
            }
        }catch (Exception e){
            log.error("处理沃尔玛商品发生异常"+e.getMessage());
        }
    }
}
