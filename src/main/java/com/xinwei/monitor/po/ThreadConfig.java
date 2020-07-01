package com.xinwei.monitor.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
@TableName("tb_sys_thread_config")
public class ThreadConfig {

    private Integer id;

    private Integer type;

    private Integer poolCount;

    private Integer initialDelay;

    private Integer period;

    private TimeUnit unit;

}
