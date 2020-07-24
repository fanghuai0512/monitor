package com.xinwei.monitor.thread.task;

import com.xinwei.monitor.po.AmznProduct;
import com.xinwei.monitor.service.TaskService;
import com.xinwei.monitor.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
public class MonitorProductToCacheTask implements Runnable{

    private static Boolean initComplete  =  false;

    @Override
    public void run() {
        try {
            if(!initComplete){
                TaskService taskService = SpringUtil.getBean(TaskService.class);
                List<AmznProduct> amznProducts = taskService.monitorProductToCache();
                if(amznProducts == null || amznProducts.size() == 0 ){
                    initComplete =true;
                    log.info("加载商品完成");
                }
            }
        }catch (Exception e){
            log.error("将监控商品放入缓存发生异常："+e.getMessage());
        }
    }
}
