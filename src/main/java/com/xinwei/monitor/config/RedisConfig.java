package com.xinwei.monitor.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import lombok.Data;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

@Data
@Aspect
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    @Autowired
    private RedisProperties redisProperties;

    private String password;

    @Before("com.xinwei.monitor.pointcut.CommonPointCut.getRedisConnectionPwd()")
    public void setRedisProperties() throws UnsupportedEncodingException {
        log.info("login redis to decode password of project");
        String pwdStr =new String(Base64.getDecoder().decode(password), "UTF-8");
        log.info("redis login password = {}", pwdStr);
        redisProperties.setPassword(pwdStr);
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<Object, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        //设置自定义的序列化方式（fastjson） ，不然key会出现 \xac\xed\x00\x05t\x00\tb
        template.setDefaultSerializer(new FastJsonRedisSerializer(Object.class));
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }



}
