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
package com.beetle.framework.persistence.access;

import com.beetle.framework.AppContext;
import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.define.CfgFileInfo;
import com.beetle.framework.resource.mask.IPasswordMask;
import com.beetle.framework.util.OtherUtil;
import com.beetle.framework.util.ResourceLoader;
import com.beetle.framework.util.file.XMLReader;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DBConfig {

	private final static Map<String, Map<String, String>> dbPool = new HashMap<String, Map<String, String>>();
	private final static Map<String, Map<String, String>> extensionCache = new HashMap<String, Map<String, String>>();
	static final String pool_imp = "pool-imp";
	static final String use_mode = "use-mode";
	static final String jndi_name = "jndi-name";

	public static void resetDBConfig() {
		dbPool.clear();
		extensionCache.clear();
	}

	public static List<String> getAllDatasourcename() {
		Document doc = null;
		try {
			List<String> myList = new ArrayList<String>();
			doc = XMLReader.getXmlDoc(AppProperties.getAppHome()
					+ "DBConfig.xml");
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

	private static void markCfgInfo(File f, String filename) {
		String smfn = OtherUtil.removePath(filename);
		AppContext ctx = AppContext.getInstance();
		try {
			if (ctx.lookup(smfn) == null) {
				CfgFileInfo cfi = new CfgFileInfo();
				cfi.setFilename(smfn);
				cfi.setLastFileModifiedTime(f.lastModified());
				cfi.setLastReadTime(System.currentTimeMillis());
				cfi.setModifyCount(0);
				cfi.setPath(filename);
				ctx.bind(smfn, cfi);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getExtensionValue(String dsName, String keyName) {
		if (!extensionCache.containsKey(dsName)) {
			if (dsName.trim().length() == 0) {
				throw new ConnectionException(
						"err,must set a datasource first!");
			}
			Map<String, String> m = readeConfig(
					"Config.Extensions." + dsName.trim(), "name", "value");
			extensionCache.put(dsName, m);
			// m.clear();
		}
		Map<String, String> m = extensionCache.get(dsName);
		return m.get(keyName);
	}

	public static String getFrameworkDS(String dsName, String keyName) {
		if (!dbPool.containsKey(dsName)) {
			if (dsName.trim().length() == 0) {
				throw new ConnectionException(
						"err,must set a datasource first!");
			}
			Map<String, String> m = readeConfig(
					"Config.DataSources." + dsName.trim(), "name", "value");
			dbPool.put(dsName, m);
			// m.clear();
		}
		Map<String, String> m = dbPool.get(dsName);
		return m.get(keyName);
	}

	private final static String xaimp = "com.beetle.framework.persistence.access.datasource.XaPool";
	private final static String noxaimp = "com.beetle.framework.persistence.access.datasource.NonXaPool";

	public static boolean isAtomikosSupport(String dataSourceName) {
		String xa = getFrameworkDS(dataSourceName, pool_imp);
		if (xa.equals(xaimp) || xa.equals(noxaimp)) {
			return true;
		}
		return false;
	}

	public final static String decodeDatasourcePassword(String dataSourceName) {
		String pwd = DBConfig.getFrameworkDS(dataSourceName, "password");
		if (pwd == null) {
			throw new AppRuntimeException(dataSourceName
					+ "'s [password] can not be null! ");
		}
		if (pwd.startsWith("mask{") && pwd.endsWith("}")) {
			String imp = DBConfig.getFrameworkDS(dataSourceName, "mask-imp");
			if (imp == null || imp.trim().length() == 0) {
				throw new AppRuntimeException(dataSourceName
						+ "'s [mask-imp] can not be null,must be setted! ");
			}
			try {
				pwd = pwd.substring(5, pwd.length() - 1);
				pwd = ((IPasswordMask) Class.forName(imp).newInstance())
						.decode(pwd);
			} catch (Exception e) {
				throw new AppRuntimeException(e);
			}
		}
		return pwd;
	}

	private final static Map<String, String> readeConfig(String v1, String v2,
			String v3) {
		Map<String, String> m = null;
		File f = null;
		try {
			String filename = AppProperties.getAppHome() + "DBConfig.xml";
			f = new File(filename);
			if (f.exists()) {
				//
				markCfgInfo(f, filename);
				//
				m = XMLReader.getProperties(filename, v1, v2, v3);
				AppLogger.getInstance(DBConfig.class).info(
						"from file:[" + f.getPath() + "]");
			} else {
				m = XMLReader.getProperties(
						ResourceLoader.getResAsStream(filename), v1, v2, v3);
				AppLogger.getInstance(DBConfig.class).info(
						"from jar:["
								+ ResourceLoader.getClassLoader().toString()
								+ "]");
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

	// public static final String SYSDATASOURCE_DEFAULT =
	// "SYSDATASOURCE_DEFAULT";
	static final String emptyStr = "";

}
