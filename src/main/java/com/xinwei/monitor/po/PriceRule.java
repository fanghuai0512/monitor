package com.xinwei.monitor.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinwei.monitor.re.PriceRuleRe;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
@TableName("tb_sys_price_rule")
public class PriceRule extends PriceRuleRe {

    @TableId(type = IdType.AUTO)
    private Integer id;

    public PriceRuleRe of(){
        PriceRuleRe priceRuleRe = new PriceRuleRe();
        BeanUtils.copyProperties(this,priceRuleRe);
        return priceRuleRe;
    }

}
