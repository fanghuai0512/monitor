package com.xinwei.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinwei.monitor.mapper.PriceRuleMapper;
import com.xinwei.monitor.po.PriceRule;
import com.xinwei.monitor.re.PriceRuleRe;
import com.xinwei.monitor.service.PriceRuleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceRuleServiceImpl extends ServiceImpl<PriceRuleMapper, PriceRule> implements PriceRuleService {

    @Override
    public List<PriceRuleRe> findList() {
        QueryWrapper<PriceRule> priceRuleQueryWrapper = new QueryWrapper<>();
        priceRuleQueryWrapper.orderByAsc("minGt");
        List<PriceRule> priceRuleList = this.list(priceRuleQueryWrapper);
        return priceRuleList.stream().map(priceRule -> priceRule.of()).collect(Collectors.toList());
    }
}
