package com.xinwei.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinwei.monitor.mapper.StoreMapper;
import com.xinwei.monitor.po.Store;
import com.xinwei.monitor.service.StoreService;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {
    @Override
    public Store findOne(String name) {
        QueryWrapper<Store> storeQueryWrapper = new QueryWrapper<>();
        storeQueryWrapper.eq("name",name);
        return this.getOne(storeQueryWrapper);
    }

}
