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

    /**
     * 店铺类型
     * @see {@linkplain com.xinwei.monitor.constant.Constant.StoreType}
     */
    private Integer type;

    /**
     * 店铺名称
     */
    private String name ;

    /**
     * @see {@link com.xinwei.monitor.constant.Status.Product}
     */
    private Integer status;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户端授权码
     */
    private String clientSecret;

    /**
     * 管理员id
     */
    private Integer managerId;

    /**
     * 管理员姓名
     */
    private String manager;
}
