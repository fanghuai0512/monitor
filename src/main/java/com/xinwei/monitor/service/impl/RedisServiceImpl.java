package com.xinwei.monitor.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xinwei.monitor.service.RedisService;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


/**
 * @author xinwei_02
 */
@Service
public class RedisServiceImpl implements RedisService {

	@Resource
	protected RedisTemplate<String, Object> redisTemplate;

	@Override
	public void put(String keys,String hashKey, Object data, long expire) {
		if (expire != -1) {
			redisTemplate.expire(keys, expire, TimeUnit.SECONDS);
		}
		HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
		opsForHash.put(keys, hashKey, data);

	}

	@Override
	public void remove(String key,String hashKey) {
		HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
		opsForHash.delete(key, hashKey);
	}

	@Override
	public Object get(String key,String hashKey) {
		 HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
		return opsForHash.get(key,hashKey);
	}

	@Override
	public boolean isKeyExists(String key,String hashKey) {
		HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
		return opsForHash.hasKey(key, hashKey);
	}

	@Override
	public void put(String key, Object value, long expire) {
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		if(expire == -1){
			operations.set(key,value);
		}else{
			operations.set(key,value,expire,TimeUnit.SECONDS);
		}
	}


	@Override
	public Object get(String key) {
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		return operations.get(key);
	}

	@Override
	public boolean remove(String key) {
		return redisTemplate.delete(key);
	}

	@Override
	public boolean isKeyExists(String key) {
		return redisTemplate.hasKey(key);
	}
}
