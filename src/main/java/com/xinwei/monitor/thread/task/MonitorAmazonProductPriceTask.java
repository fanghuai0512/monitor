package com.xinwei.monitor.thread.task;

import com.xinwei.monitor.service.TaskService;
import com.xinwei.monitor.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 亚马逊价格监控任务
 * 此线程任务主要是获取监控商品价格列表，其他数据分析交给其他线程池处理
 */
@Slf4j
public  class MonitorAmazonProductPriceTask implements Runnable {

    @Override
    public void run() {
        try {
            TaskService taskService = SpringUtil.getBean(TaskService.class);
            taskService.monitorAmazonProductPrice();
        }catch (Exception e){
            log.error("监控亚马逊商品价格发生异常："+e.getMessage());
        }
    }
}
