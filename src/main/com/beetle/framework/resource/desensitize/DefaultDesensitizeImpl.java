package com.beetle.framework.resource.desensitize;

import java.util.HashSet;
import java.util.Set;

import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;

/**
 * 对所有的字段做默认的处理，如果有需要可自行实现
 * 
 * @author yuhaodong@gmail.com
 *
 */
public class DefaultDesensitizeImpl implements IDesensitize {
	private final Set<String> fields;

	public DefaultDesensitizeImpl() {
		super();
		this.fields = new HashSet<>();
		String xx = AppProperties.get("desensitize_DB_Fileds", "");
		if (xx.length() == 0) {
			throw new AppRuntimeException("desensitize_DB_Fileds in application.properties file not config yet!");
		}
		String xxx[] = xx.split(";");
		for (int i = 0; i < xxx.length; i++) {
			fields.add(xxx[i]);
		}
	}

	@Override
	public String Desensitize(String key, String value) {
		if (fields.contains(key)) {
			return desensitize(value);
		}
		return value;
	}

	private static final int SIZE = AppProperties.getAsInt("desensitize_SIZE", 6);
	private static final String SYMBOL = AppProperties.get("desensitize_SYMBOL", "*");

	/**
	 * 静态脱敏方法（可配置最大脱敏长度和符合，见application.properties）
	 * @param value 要脱敏的值
	 * @return 脱敏后的值
	 */
	public static String desensitize(String value) {
		if (null == value || "".equals(value)) {
			return value;
		}
		int len = value.length();
		int pamaone = len / 2;
		int pamatwo = pamaone - 1;
		int pamathree = len % 2;
		StringBuilder stringBuilder = new StringBuilder();
		if (len <= 2) {
			if (pamathree == 1) {
				return SYMBOL;
			}
			stringBuilder.append(SYMBOL);
			stringBuilder.append(value.charAt(len - 1));
		} else {
			if (pamatwo <= 0) {
				stringBuilder.append(value.substring(0, 1));
				stringBuilder.append(SYMBOL);
				stringBuilder.append(value.substring(len - 1, len));

			} else if (pamatwo >= SIZE / 2 && SIZE + 1 != len) {
				int pamafive = (len - SIZE) / 2;
				stringBuilder.append(value.substring(0, pamafive));
				for (int i = 0; i < SIZE; i++) {
					stringBuilder.append(SYMBOL);
				}
				if ((pamathree == 0 && SIZE / 2 == 0) || (pamathree != 0 && SIZE % 2 != 0)) {
					stringBuilder.append(value.substring(len - pamafive, len));
				} else {
					stringBuilder.append(value.substring(len - (pamafive + 1), len));
				}
			} else {
				int pamafour = len - 2;
				stringBuilder.append(value.substring(0, 1));
				for (int i = 0; i < pamafour; i++) {
					stringBuilder.append(SYMBOL);
				}
				stringBuilder.append(value.substring(len - 1, len));
			}
		}
		return stringBuilder.toString();

	}
}
