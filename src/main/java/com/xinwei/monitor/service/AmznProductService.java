package com.xinwei.monitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinwei.monitor.po.AmznProduct;

import java.util.List;

public interface AmznProductService extends IService<AmznProduct> {

    /**
     * 查询监控数据
     * @return
     */
    List<AmznProduct> findMonitorList(long pageNum,long pageSize);
}
