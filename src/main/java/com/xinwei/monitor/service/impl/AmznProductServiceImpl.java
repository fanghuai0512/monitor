package com.xinwei.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinwei.monitor.mapper.AmznProductMapper;
import com.xinwei.monitor.po.AmznProduct;
import com.xinwei.monitor.service.AmznProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmznProductServiceImpl extends ServiceImpl<AmznProductMapper,AmznProduct> implements AmznProductService {

    @Override
    public List<AmznProduct> findMonitorList(long pageNum,long pageSize) {
        QueryWrapper<AmznProduct> productQueryWrapper = new QueryWrapper<>();
        productQueryWrapper.in("status",1,2);
        IPage<AmznProduct> page = this.page(new Page<>(pageNum,pageSize), productQueryWrapper);
        return page.getRecords();
    }
}
