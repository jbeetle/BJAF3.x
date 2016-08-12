package com.beetle.framework.persistence.nosql.redis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;

import com.beetle.framework.resource.mask.IPasswordMask;
import com.beetle.framework.util.ResourceLoader;
import com.beetle.framework.util.file.XMLReader;

class RedisConfig {
	private final static Map<String, Map<String, String>> dbPool = new HashMap<String, Map<String, String>>();

	public static List<String> getAllDatasourcename() {
		Document doc = null;
		try {
			List<String> myList = new ArrayList<String>();
			doc = XMLReader.getXmlDoc(AppProperties.getAppHome() + "RedisConfig.xml");
			Element root = doc.getRootElement();
			for (Iterator<?> i = root.elementIterator("DataSources"); i.hasNext();) {
				Element e = (Element) i.next();
				@SuppressWarnings("unchecked")
				Iterator<Node> n = e.nodeIterator();
				while (n.hasNext()) {
					Node at = n.next();
					if (at.getName() != null) {
						myList.add(at.getName());
					}
				}
			}
			return myList;
		} catch (Exception e) {
			throw new AppRuntimeException(e);
		} finally {
			if (doc != null) {
				doc.clearContent();
			}
		}
	}

	public final static String decodeDatasourcePassword(String dataSourceName) {
		String pwd = getFrameworkDS(dataSourceName, "password");
		if (pwd == null) {
			throw new AppRuntimeException(dataSourceName + "'s [password] can not be null! ");
		}
		if (pwd.startsWith("mask{") && pwd.endsWith("}")) {
			String imp = getFrameworkDS(dataSourceName, "mask-imp");
			if (imp == null || imp.trim().length() == 0) {
				throw new AppRuntimeException(dataSourceName + "'s [mask-imp] can not be null,must be setted! ");
			}
			try {
				pwd = pwd.substring(5, pwd.length() - 1);
				pwd = ((IPasswordMask) Class.forName(imp).newInstance()).decode(pwd);
			} catch (Exception e) {
				throw new AppRuntimeException(e);
			}
		}
		return pwd;
	}

	private final static Map<String, String> readeConfig(String v1, String v2, String v3) {
		Map<String, String> m = null;
		File f = null;
		try {
			String filename = AppProperties.getAppHome() + "RedisConfig.xml";
			f = new File(filename);
			if (f.exists()) {
				m = XMLReader.getProperties(filename, v1, v2, v3);
				AppLogger.getInstance(RedisConfig.class).info("from file:[" + f.getPath() + "]");
			} else {
				m = XMLReader.getProperties(ResourceLoader.getResAsStream(filename), v1, v2, v3);
				AppLogger.getInstance(RedisConfig.class)
						.info("from jar:[" + ResourceLoader.getClassLoader().toString() + "]");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (f != null) {
				f = null;
			}
		}
		return m;
	}

	public static String getFrameworkDS(String dsName, String keyName) {
		if (!dbPool.containsKey(dsName)) {
			if (dsName.trim().length() == 0) {
				throw new AppRuntimeException("err,must set a datasource first!");
			}
			Map<String, String> m = readeConfig("Config.DataSources." + dsName.trim(), "name", "value");
			dbPool.put(dsName, m);
			// m.clear();
		}
		Map<String, String> m = dbPool.get(dsName);
		return m.get(keyName);
	}

	public static void main(String[] args) {
		System.out.println(getAllDatasourcename());
		System.out.println(getFrameworkDS("default","server-address"));
		System.out.println(getFrameworkDS("xxx","test-on-borrow"));
		System.out.println(decodeDatasourcePassword("default"));
	}

}
