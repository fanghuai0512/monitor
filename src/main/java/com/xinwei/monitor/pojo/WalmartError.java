package com.xinwei.monitor.pojo;

import lombok.Data;

@Data
public class WalmartError {

    /**
     * 状态码
     */
    private String code ;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 信息
     */
    private String info;

    /**
     * 错误级别
     */
    private String severity;

    /**
     * 分类
     */
    private String category;
}
