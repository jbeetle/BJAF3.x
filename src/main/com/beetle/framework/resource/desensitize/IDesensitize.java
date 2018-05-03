package com.beetle.framework.resource.desensitize;

public interface IDesensitize {
	/**
	 * 脱敏
	 * @param key 根据输入的字段名
	 * @param value 本来的值
	 * @return 返回脱敏后的值
	 */
	String Desensitize(String key,String value);
}
