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

import java.beans.Introspector;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.beetle.framework.AppRuntimeException;

public class ObjectUtil {

	private final static Map<Class<?>, Method[]> cache = new WeakHashMap<Class<?>, Method[]>();

	static String decapitalize(String fieldName) {
		return Introspector.decapitalize(fieldName);
	}

	public final static String objToBase64Str(Object obj) {
		org.apache.commons.codec.binary.Base64 b64 = new org.apache.commons.codec.binary.Base64();
		byte[] bytes = b64.encode(objToBytes(obj));
		b64 = null;
		return new String(bytes);
	}

	public final static Object base64StrToObject(String b64Str) {
		org.apache.commons.codec.binary.Base64 b64 = new org.apache.commons.codec.binary.Base64();
		byte[] bytes = b64.decode(b64Str.getBytes());
		return bytesToObj(bytes);
	}

	public final static Map<String, Object> describe(Object target) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<String> retList = new LinkedList<String>();
		Method[] methods = (Method[]) cache.get(target.getClass());
		if (methods == null) {
			methods = target.getClass().getMethods();
			cache.put(target.getClass(), methods);
		}
		for (int i = 0; i < methods.length; i++) {
			String method = methods[i].getName();
			if (method.indexOf("set") == 0 || method.indexOf("get") == 0) {
				retList.add(method.substring(3, method.length()));
			} else if (method.indexOf("is") == 0) {
				retList.add(method.substring(2, method.length()));
			}
		}
		Collections.sort(retList);
		Object[] props = retList.toArray();
		retList.clear();
		for (int i = 0; i < props.length - 1; i++) {
			if (props[i].equals(props[i + 1])) {
				String prop = decapitalize(props[i].toString());
				retMap.put(prop, getValue(prop, target));
			}
		}
		retList = null;
		return retMap;
	}

	public final static void populate(Object obj, Map<String, Object> map) {
		Set<?> s = map.entrySet();
		Iterator<?> it = s.iterator();
		while (it.hasNext()) {
			String key = "";
			Object o = null;
			@SuppressWarnings("rawtypes")
			Map.Entry me = (Map.Entry) it.next();
			try {
				key = me.getKey().toString();
				o = me.getValue();
				if (o == null) {
					continue;
				}
				setValue(key, obj, o);
			} catch (IllegalArgumentException e) {
				Class<Object> type = ObjectUtil.getType(key, obj);
				String tstr = type.toString();
				if (tstr.equals(Integer.class.toString())) {
					ObjectUtil
							.setValue(key, obj, Integer.valueOf(o.toString()));
				} else if (tstr.equals(Long.class.toString())) {
					ObjectUtil.setValue(key, obj, Long.valueOf(o.toString()));
				} else if (tstr.equals(Float.class.toString())) {
					ObjectUtil.setValue(key, obj, Float.valueOf(o.toString()));
				} else if (tstr.equals(Double.class.toString())) {
					ObjectUtil.setValue(key, obj, Double.valueOf(o.toString()));
				} else if (tstr.equals(Short.class.toString())) {
					ObjectUtil.setValue(key, obj, Short.valueOf(o.toString()));
				} else if (tstr.equals(Byte.class.toString())) {
					ObjectUtil.setValue(key, obj, Byte.valueOf(o.toString()));
				} else if (tstr.equals(Date.class.toString())) {
					if (o instanceof Date) {
						ObjectUtil.setValue(key, obj, (Date) o);
					} else {
						long time = ((Double) o).longValue();
						ObjectUtil.setValue(key, obj, new Date(time));
					}
				} else if (tstr.equals(java.sql.Timestamp.class.toString())) {
					if (o instanceof java.sql.Timestamp) {
						ObjectUtil.setValue(key, obj, (Date) o);
					} else {
						long time = ((Double) o).longValue();
						ObjectUtil.setValue(key, obj, new java.sql.Timestamp(
								time));
					}
				} else {
					throw e;
				}
				tstr = null;
				type = null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public final static Class<Object> getType(String property, Object target) {

		Class<Object> ret = Object.class;
		property = "set" + property;

		Method[] methods = (Method[]) cache.get(target.getClass());
		if (methods == null) {
			methods = target.getClass().getMethods();
			cache.put(target.getClass(), methods);
		}

		for (int i = 0; i < methods.length; i++) {

			if (property.equalsIgnoreCase(methods[i].getName())) {

				@SuppressWarnings("rawtypes")
				Class[] paramClass = methods[i].getParameterTypes();
				if (paramClass.length == 1) {
					return paramClass[0];
				}

			}

		}

		return ret;

	}

	public final static Object getValue(String property, Object target) {

		String get = "get" + property;
		String is = "is" + property;

		Method[] methods = (Method[]) cache.get(target.getClass());
		if (methods == null) {
			methods = target.getClass().getMethods();
			cache.put(target.getClass(), methods);
		}

		for (int i = 0; i < methods.length; i++) {

			if (get.equalsIgnoreCase(methods[i].getName())
					|| is.equalsIgnoreCase(methods[i].getName())) {

				try {
					return methods[i].invoke(target, (Object[]) null);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		}
		return null;

	}

	private static class ObjectMapperCreator {
		private volatile org.codehaus.jackson.map.ObjectMapper objectMapper;
		private static ObjectMapperCreator instance = new ObjectMapperCreator();

		private ObjectMapperCreator() {

		}

		public static ObjectMapperCreator getInstance() {
			return instance;
		}

		public org.codehaus.jackson.map.ObjectMapper getObjectMapper() {
			if (objectMapper == null) {
				objectMapper = new org.codehaus.jackson.map.ObjectMapper();
			}
			return objectMapper;
		}

	}

	/**
	 * 使用jackson包解析
	 * 
	 * @param json
	 * @param daoImpClass
	 * @return
	 */
	public static <T> T jsonToObjectWithJackson(String json,
			Class<T> daoImpClass) {
		try {
			return ObjectMapperCreator.getInstance().getObjectMapper()
					.readValue(json, daoImpClass);
		} catch (Exception e) {
			throw new AppRuntimeException(e);
		}
	}

	public static String objectToJsonWithJackson(Object dataObject) {

		try {
			return ObjectMapperCreator.getInstance().getObjectMapper()
					.writeValueAsString(dataObject);
		} catch (Exception e) {
			throw new AppRuntimeException(e);
		}
	}

	/**
	 * 设置某个对象的字段的值（支持私有属性）
	 * @param target
	 * @param field
	 * @param value
	 */
	public final static void setFieldValue(Object target, String field, Object value) {
		try {
			Class<?> obj = target.getClass();
			Field[] fields = obj.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				if (field.equals(fields[i].getName())) {
					fields[i].set(target, value);
					break;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public final static void setValue(String property, Object target,
			Object value) {
		property = "set" + property;
		Method[] methods = (Method[]) cache.get(target.getClass());
		if (methods == null) {
			methods = target.getClass().getMethods();
			cache.put(target.getClass(), methods);
		}
		for (int i = 0; i < methods.length; i++) {
			if (property.equalsIgnoreCase(methods[i].getName())) {
				@SuppressWarnings("rawtypes")
				Class[] paramClass = methods[i].getParameterTypes();
				if (paramClass.length == 1) {
					try {
						methods[i].invoke(target, value);
					} catch (IllegalArgumentException ille) {
						throw ille;
					} catch (Exception ex) {
						throw new RuntimeException(ex);
					}
				}
			}
		}
	}

	/**
	 * 对象转换成字节数组
	 * 
	 * @param obj
	 * @return
	 */
	public final static byte[] objToBytes(Object obj) {
		ByteArrayOutputStream bao = null;
		ObjectOutputStream oos;
		try {
			bao = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bao);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			return bao.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (bao != null) {
					bao.close();
					bao = null;
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 字节数组转成对象
	 * 
	 * @param bytes
	 * @return
	 */
	public final static Object bytesToObj(byte[] bytes) {
		ByteArrayInputStream bai = null;
		ObjectInputStream ois;
		try {
			bai = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bai);
			Object obj = ois.readObject();
			ois.close();
			ois = null;
			return obj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (bai != null) {
					bai.close();
					bai = null;
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 对象深度克隆
	 * 
	 * @param originObj
	 * @return
	 */
	public final static Object objectClone(Object originObj) {
		ByteArrayOutputStream bao = null;
		ByteArrayInputStream bai = null;
		ObjectOutputStream oos;
		ObjectInputStream ois;
		try {
			bao = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bao);
			oos.writeObject(originObj);
			oos.flush();
			oos.close();
			bai = new ByteArrayInputStream(bao.toByteArray());
			ois = new ObjectInputStream(bai);
			Object obj = ois.readObject();
			ois.close();
			oos = null;
			ois = null;
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (bao != null) {
					bao.close();
					bao = null;
				}
				if (bai != null) {
					bai.close();
					bai = null;
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 返回此对象序列化后的大小（字节数）
	 * 
	 * @param obj
	 * @return
	 */
	public final static int sizeOf(Object obj) {
		ByteArrayOutputStream bao = null;
		ObjectOutputStream oos;
		try {
			bao = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bao);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			return bao.size();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			try {
				if (bao != null) {
					bao.close();
					bao = null;
				}
			} catch (IOException e) {
			}
		}
	}

	private static class XStreamCreater {
		private static XStreamCreater instance = new XStreamCreater();

		private XStreamCreater() {

		}

		public static XStreamCreater getInstance() {
			return instance;
		}

		public String objectToXml(Object dataObject) {
			com.thoughtworks.xstream.XStream xtm = new com.thoughtworks.xstream.XStream();
			final String dd;
			dd = xtm.toXML(dataObject);
			return dd;
		}

		public Object xmlToObject(String xml) {
			com.thoughtworks.xstream.XStream xtm = new com.thoughtworks.xstream.XStream();
			Object o = xtm.fromXML(xml);
			return o;
		}

		public String objectToJsonWithXStream(Object dataObject) {
			com.thoughtworks.xstream.XStream xtm = new com.thoughtworks.xstream.XStream(
					new com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver());
			xtm.setMode(com.thoughtworks.xstream.XStream.NO_REFERENCES);
			final String dd;
			dd = xtm.toXML(dataObject);
			return dd;
		}

		public Object jsonToObjectWithXStream(String json) {
			com.thoughtworks.xstream.XStream xtm = new com.thoughtworks.xstream.XStream(
					new com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver());
			Object o = xtm.fromXML(json);
			return o;
		}
	}

	public static String objectToXml(Object dataObject) {
		return XStreamCreater.getInstance().objectToXml(dataObject);
	}

	public static Object xmlToObject(String xml) {
		return XStreamCreater.getInstance().xmlToObject(xml);
	}

	/**
	 * 使用XStream包解析
	 * 
	 * @param dataObject
	 * @return
	 */
	public static String objectToJsonWithXStream(Object dataObject) {
		return XStreamCreater.getInstance().objectToJsonWithXStream(dataObject);
	}

	/**
	 * 使用XStream包解析
	 * 
	 * @param json
	 * @return
	 */
	public static Object jsonToObjectWithXStream(String json) {
		return XStreamCreater.getInstance().jsonToObjectWithXStream(json);
	}
}
