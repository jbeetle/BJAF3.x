/*
 * BJAF - Beetle J2EE Application Framework
 * 甲壳虫J2EE企业应用开发框架
 * 版权所有2003-2015 余浩东 (www.beetlesoft.net)
 * 
 * 这是一个免费开源的软件，您必须在
 *<http://www.apache.org/licenses/LICENSE-2.0>
 *协议下合法使用、修改或重新发布。
 *
 * 感谢您使用、推广本框架，若有建议或问题，欢迎您和我联系。
 * 邮件： <yuhaodong@gmail.com/>.
 */
package com.beetle.framework.util;

import java.util.UUID;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * 
 * <p>
 * Description: UUID生成器
 * 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * 
 * </p>
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */
public class UUIDGenerator {
	private final static String chars64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789^~abcdefghijklmnopqrstuvwxyz";

	private static String convert(long x, int n, String d) {
		if (x == 0) {
			return "0";
		}
		String r = "";
		int m = 1 << n;
		m--;
		while (x != 0) {
			r = d.charAt((int) (x & m)) + r;
			x = x >>> n;
		}
		return r;
	}

	/**
	 * 返回一个唯一的16位字符串。 基于： 32位当前时间，32位对象的identityHashCode和32位随机数
	 * 
	 * @param o
	 *            Object对象
	 * @return uuid 一个16位的字符串
	 */
	public final static String generateCharUUID(Object o) {
		long id1 = System.currentTimeMillis() & 0xFFFFFFFFL;
		long id2 = System.identityHashCode(o);
		long id3 = OtherUtil.randomLong(-0x80000000L, 0x80000000L) & 0xFFFFFFFFL;
		id1 <<= 16;
		id1 += (id2 & 0xFFFF0000L) >> 16;
		id3 += (id2 & 0x0000FFFFL) << 32;
		String out = convert(id1, 6, chars64) + convert(id3, 6, chars64);
		out = out.replaceAll(" ", "o");
		return out;
	}

	public final static String generateNumberUUID(Object o) {
		long id1 = System.currentTimeMillis() & 0xFFFFFFFFL;
		long id2 = System.identityHashCode(o);
		long id3 = OtherUtil.randomLong(-0x80000000L, 0x80000000L) & 0xFFFFFFFFL;
		id1 <<= 16;
		id1 += (id2 & 0xFFFF0000L) >> 16;
		id3 += (id2 & 0x0000FFFFL) << 32;
		return "" + id1 + id3;
	}

	public final static String generatePrefixHostUUID() {
		return OtherUtil.getLocalHostName() + "@" + generateUUID();
	}

	/**
	 * 返回10个随机字符（基于目前时间和一个随机字符串）
	 * 
	 * 
	 * @return String
	 */
	public final static String generateUUID() {
		return UUID.randomUUID().toString();
	}

}
