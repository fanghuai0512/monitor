package com.xinwei.monitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinwei.monitor.po.Store;

public interface StoreService extends IService<Store> {

    Store findOne(String name);
}
