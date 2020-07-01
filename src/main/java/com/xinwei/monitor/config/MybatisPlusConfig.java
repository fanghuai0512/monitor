package com.xinwei.monitor.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 *	Mybatis-plus 配置文件
 *  @author fangh
 */
@Configuration
public class MybatisPlusConfig implements MetaObjectHandler{

	/**
	 * 分页插件
	 * @return PaginationInterceptor
	 */
	@Bean
	public PaginationInterceptor paginationInterceptor() {
		return new PaginationInterceptor();
	}

	@Override
	public void insertFill(MetaObject metaObject) {
		this.setFieldValByName("createTime",new Date(),metaObject);
		this.setFieldValByName("updateTime",new Date(),metaObject);
	}

	@Override
	public void updateFill(MetaObject metaObject) {
		this.setFieldValByName("updateTime",new Date(),metaObject);
	}


}
