package com.xinwei.monitor.service;

/**
 * @author xinwei_02
 */
public interface RedisService {

	/**
	 * 添加
	 * 
	 * @param key    key
	 * @param data 对象
	 * @param expire 过期时间(单位:秒),传入 -1 时表示不设置过期时间
	 */
	public void put(String keys, String key, Object data, long expire);

	/**
	 * 删除
	 * @param key 传入key的名称
	 */
	public void remove(String key, String hashKey);

	/**
	 * 查询
	 * @param key 查询的key
	 * @return
	 */
	public Object get(String key, String hashKey);

	/**
	 * 判断key是否存在redis中
	 * @param key 传入key的名称
	 * @return
	 */
	public boolean isKeyExists(String key, String hashKey);


	/**
	 * 存放
	 * @param key
	 * @param value
	 * @param expire
	 */
	public void put(String key,Object value,long expire);


	/**
	 * 获取
	 * @param key
	 * @return
	 */
	public Object get(String key);

	/**
	 * 移出
	 * @param key
	 * @return
	 */
	public boolean remove(String key);

	/**
	 * 是否存在
	 * @param key
	 * @return
	 */
	public boolean isKeyExists(String key);

	

}
