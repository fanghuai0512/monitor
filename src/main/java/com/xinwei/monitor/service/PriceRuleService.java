package com.xinwei.monitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinwei.monitor.po.PriceRule;
import com.xinwei.monitor.re.PriceRuleRe;

import java.util.List;

public interface PriceRuleService extends IService<PriceRule> {

    List<PriceRuleRe> findList();
}
