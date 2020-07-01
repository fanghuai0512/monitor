package com.xinwei.monitor.pointcut;

import org.aspectj.lang.annotation.Pointcut;

/**
 * @author xinwei_02
 */
public class CommonPointCut {

    @Pointcut("execution(* org.springframework.boot.autoconfigure.data.redis.RedisProperties.getPassword())")
    public void getRedisConnectionPwd(){}

}
