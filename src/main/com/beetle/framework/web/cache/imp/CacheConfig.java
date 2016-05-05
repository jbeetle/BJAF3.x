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
package com.beetle.framework.web.cache.imp;

/**
 * @author Henry Yu 2005-9-27
 *
 */

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CacheConfig {

	private static Map<String, CacheAttr> url_cacheAttr = new HashMap<String, CacheAttr>();
	private static Property config = new Property();
	private static boolean read_flag = false;
	private static CacheConfig instance = new CacheConfig();

	private CacheConfig() {
	}

	static class Property {
		private int maxElementsInMemory;
		private String diskStorePath;
		private String memoryStoreEvictionPolicy;

		public Property() {
		}

		public int getMaxElementsInMemory() {
			return maxElementsInMemory;
		}

		public void setMaxElementsInMemory(int maxElementsInMemory) {
			this.maxElementsInMemory = maxElementsInMemory;
		}

		public String getDiskStorePath() {
			return diskStorePath;
		}

		public void setDiskStorePath(String diskStorePath) {
			this.diskStorePath = diskStorePath;
		}

		public String getMemoryStoreEvictionPolicy() {
			return memoryStoreEvictionPolicy;
		}

		public void setMemoryStoreEvictionPolicy(
				String memoryStoreEvictionPolicy) {
			this.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;
		}
	}

	private static String convertPath(String aStr) {
		return "//".concat(aStr.replace('.', '/'));
	}

	public synchronized void readCacheURLs(InputStream xmlIs) {
		url_cacheAttr.clear();
		if (xmlIs == null) {
			return;
		}
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(xmlIs);
			Node node = doc.selectSingleNode(convertPath("mappings.caches"));
			if (node != null) {
				config.setDiskStorePath(node.valueOf("@diskStorePath"));
				config.setMaxElementsInMemory(toInt(node
						.valueOf("@maxElementsInMemory")));
				config.setMemoryStoreEvictionPolicy(node
						.valueOf("memoryStoreEvictionPolicy"));
				Iterator<?> it = node.selectNodes("cItem").iterator();
				while (it.hasNext()) {
					CacheAttr attr = new CacheAttr();
					Element e = (Element) it.next();
					attr.setUrl(e.valueOf("@name"));
					attr.setScope(e.valueOf("@scope"));
					attr.setTime(toInt(e.valueOf("@time")));
					url_cacheAttr.put(attr.getUrl(), attr);
				}
			}
		} catch (Exception de) {
			de.printStackTrace();
		} finally {
			if (doc != null) {
				doc.clearContent();
			}
			reader = null;
		}
	}

	private int toInt(String a) {
		return Integer.parseInt(a);
	}

	public static Property getConfig() {
		return config;
	}

	public static Map<String, CacheAttr> getCacheURLs(InputStream xmlIs) {
		if (!read_flag) {
			instance.readCacheURLs(xmlIs);
			read_flag = true;
		}
		return url_cacheAttr;
	}
}
/*
 * public static Map getCacheURLs() { if (!read_flag) { File f = new File(
 * ResourceReader.getAPP_HOME() + "WebController.xml"); if (f.exists()) {
 * CacheConfig.getInstance().readCacheURLs(f); } read_flag = true; } return
 * url_cacheAttr; }
 */
