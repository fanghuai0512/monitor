package com.xinwei.monitor.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_biz_store")
public class Store {

    @TableId(type = IdType.AUTO)
    private Integer id ;

    private Integer type;

    private String name ;

    /**
     * @see {@link com.xinwei.monitor.constant.Status.Product}
     */
    private Integer status;

    private String clientId;

    private String clientSecret;

    private Integer managerId;

    private String manager;
}
