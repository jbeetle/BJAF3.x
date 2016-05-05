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
package com.beetle.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.beetle.framework.util.ResourceLoader;

/**
 * <pre>
 * 应用配置文件[application.properties]目录资源读取器
 * 此文件可以通过jvm参数[-Dbeetle.application.home.path=d://xxx//yyy]指定其存放目录
 * 若不显性指定路径，框架会按照以下顺序及方式寻找此文件：
 * 1，从AppContext里面查找定义，如果存在则按照定义路径加载（提供在程序设置路径的接口）
 * 2，在当前应用的工作目录下（相对路径）config子目录下寻找并加载
 * 3，在当前应用的classpath的config子目录下寻找并加载
 * </pre>
 * 
 * @author 余浩东（hdyu@beetlesoft.net）
 * @version 1.0
 */
final public class AppProperties {
	private static Properties appPpt = new Properties();
	private static final String resource_SYSCONFIG_MASK = "resource_CONFIG_MASK";
	static {
		String filenamePath;
		String fp = AppContext.getInstance().getAppHome();
		if (fp != null && fp.trim().length() > 0) {
			filenamePath = fp + "application.properties";
		} else {
			filenamePath = "config/application.properties";
		}
		File f = new File(filenamePath);
		if (f.exists()) {
			FileInputStream bis = null;
			try {
				bis = new FileInputStream(f);
				appPpt.load(bis);
				System.out.println("find  [" + filenamePath + "] and  use it!");
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (bis != null) {
						bis.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				bis = null;
			}
		} else {
			InputStream is = null;
			try {
				is = ResourceLoader.getResAsStream(filenamePath);
				appPpt.load(is);
				System.out.println("find  [" + filenamePath
						+ "] in classpath and  use it!");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				is = null;
			}
		}
	}

	/**
	 * 根据key获取值，并转成int类型返回
	 * 
	 * @param key
	 * @return
	 */
	public static int getAsInt(String key) {
		return Integer.parseInt(get(key).trim());
	}

	public static float getAsFloat(String key) {
		return Float.parseFloat(get(key).trim());
	}

	public static float getAsFloat(String key, float defaultValue) {
		String a = get(key);
		if (a == null || a.length() == 0) {
			return defaultValue;
		}
		return Float.parseFloat(get(key).trim());
	}

	/**
	 * 根据key获取值，如果值不存在，则返回输入默认值
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static int getAsInt(String key, int defaultValue) {
		String a = get(key);
		if (a == null || a.length() == 0) {
			return defaultValue;
		}
		return Integer.parseInt(a.trim());
	}

	/**
	 * 获取整个应用属性文件内容
	 * 
	 * @return
	 */
	public static Properties getProperties() {
		return appPpt;
	}

	/**
	 * 根据key获取文件对应的值，以字符串类型返回
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		return appPpt.getProperty(key);
	}

	/**
	 * 通过字符集编码形式获取值 application.properties文件默认编码为ansi，如果值为中文的话，
	 * 可以charsetName=“8859_1”进行decode
	 * 
	 * @param key
	 * @param charsetName
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getByDecode(String key, String charsetName)
			throws UnsupportedEncodingException {
		String x = get(key);
		if (x == null) {
			return null;
		}
		String xx = new String(x.getBytes(charsetName));
		return xx;
	}

	/**
	 * 根据key获取值，如果这个值不存在，返回默认值
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String get(String key, String defaultValue) {
		String a = get(key);
		if (a == null) {
			return defaultValue;
		} else {
			return a;
		}
	}

	public static boolean getAsBoolean(String key) {
		return Boolean.parseBoolean(get(key).trim());
	}

	public static boolean getAsBoolean(String key, boolean defaultValue) {
		String a = get(key);
		if (a == null || a.length() == 0) {
			return defaultValue;
		}
		return Boolean.parseBoolean(a.trim());
	}

	/**
	 * 根据属性的Key的前缀返回满足此前缀Key所有的值
	 * 
	 * @param prefixName
	 *            --key的前缀名称
	 * @return 满足此前缀Key所有的值列表
	 */
	public static List<String> getByPrefixName(String prefixName) {
		List<String> vl = new ArrayList<String>();
		Iterator<Object> it = appPpt.keySet().iterator();
		while (it.hasNext()) {
			String x = (String) it.next();
			if (x.startsWith(prefixName)) {
				vl.add(get(x));
			}
		}
		return vl;
	}

	public static int getCONFIG_MASK() {
		return AppProperties.getAsInt(resource_SYSCONFIG_MASK, 0);
	}

	public static String getAppHome() {
		return AppContext.getInstance().getAppHome();
	}
}
