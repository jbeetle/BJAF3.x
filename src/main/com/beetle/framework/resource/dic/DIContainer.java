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
package com.beetle.framework.resource.dic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.beetle.framework.AppProperties;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.dic.ReleBinder.BeanVO;
import com.beetle.framework.resource.dic.ReleBinder.FieldVO;
import com.beetle.framework.util.ClassUtil;
import com.beetle.framework.util.ObjectUtil;
import com.beetle.framework.util.ResourceLoader;
import com.beetle.framework.util.file.DirUtil;

/*
 * 依赖注入容器
 * 一个应用只有一个容器实例。
 * init-初始化容器
 * close-关闭容器释放资源，关闭容器后必须重新初始化才能使用
 * retrieve-获取容器中的对应的对象实例,在此方法中会判断容器没有初始化会自动初始化，会判断此类是否已经在容器里，没有存在会尝试已经绑定，如果失败会抛出一个运行时异常
 * exist-查询某个对象实例是否存在容器中
 */
public class DIContainer {
	private final static Map<String, Object> DI_BEAN_CACHE = new ConcurrentHashMap<String, Object>();
	private static DIContainer instance = new DIContainer();
	private static ReleBinder binder;
	// private ReleBinder binder;
	private static final AppLogger logger = AppLogger.getInstance(DIContainer.class);
	private boolean initFlag;

	private DIContainer() {
		initFlag = false;
	}

	public static DIContainer getInstance() {
		return instance;
	}

	public Object retrieve(String key) {
		if (!initFlag) {
			this.init();
		}
		// try {
		// initOne(Class.forName(key));
		// } catch (ClassNotFoundException e) {
		// throw new DependencyInjectionException(e);
		// }
		Object o = ReleBinder.getProxyFromCache(key);
		if (o == null) {
			o = getBean(key);
		}
		return o;
	}

	@SuppressWarnings("unchecked")
	public <T> T retrieve(Class<T> face) {
		if (!initFlag) {
			this.init();
		}
		// initOne(face);
		String key = face.getName();
		Object o = ReleBinder.getProxyFromCache(key);
		if (o == null) {
			o = getBean(key);
		}
		return (T) o;
	}

	// 单独绑定无法保证bean接口引用之间的依赖先后顺序，此方案无用，先保留
	<T> void initOne(Class<T> face) {
		String key = face.getName();
		if (!this.exist(key)) {
			synchronized (logger) {
				if (this.exist(key)) {
					return;
				}
				String pn = face.getPackage().getName();
				Map<Class<?>, Class<?>> kvs = ClassUtil.getPackAllInterfaceImplMap(pn);
				Class<?> iv = kvs.get(face);
				if (iv == null) {
					throw new DependencyInjectionException("[" + key + "]'s implments class not found!");
				}
				if (binder == null) {
					throw new DependencyInjectionException("call init() first!");
				}
				binder.bind(face, iv, true);
				binder.bindProperties();
				initBean();
				initInject();
				binder.getBeanVoList().clear();
			}
		}
	}

	void createInstance(String beanName, Class<?> impl) {
		try {
			if (!DI_BEAN_CACHE.containsKey(beanName)) {
				Object bean = ClassUtil.newInstance(impl);
				DI_BEAN_CACHE.put(beanName, bean);
			}
		} catch (Exception e) {
			throw new DependencyInjectionException("Failed to initialize the Bean", e);
		}
	}

	void keepInstance(String key, Object obj) {
		if (!DI_BEAN_CACHE.containsKey(key)) {
			DI_BEAN_CACHE.put(key, obj);
		}
	}

	public boolean exist(String key) {
		return DI_BEAN_CACHE.containsKey(key);
	}

	public boolean exist(Class<?> face) {
		return DI_BEAN_CACHE.containsKey(face.getName());
	}

	public static class Inner {
		public static Object getBeanFromDIBeanCache(String name) {
			return getBean(name);
		}

		public static ReleBinder getReleBinder() {
			if (binder == null) {
				DIContainer.getInstance().init();
			}
			return binder;
		}
	}

	static Object getBean(String name) {
		return DI_BEAN_CACHE.get(name);
	}

	private void initConfigBinder() {
		binder = new ReleBinder();
		// loadXmlFile(rb, "DAOConfig.xml");
		// loadXmlFile(rb, "ServiceConfig.xml");
		// loadXmlFile(rb, "AOPConfig.xml");
		logger.info("load from xml files...");
		String files[] = AppProperties
				.get("resource_DI_CONTAINER_FILES", "DAOConfig.xml;ServiceConfig.xml;AOPConfig.xml").split(";");
		for (int i = 0; i < files.length; i++) {
			loadXmlFile(binder, files[i]);
		}
		logger.info("load by auto scan...");
		// auto scan
		String daoPackPath = AppProperties.get("resource_DI_DAO_PACK_PATH");
		if (daoPackPath != null && daoPackPath.trim().length() > 0) {
			// xxx;yyy;zzz
			String packs[] = daoPackPath.split(";");
			for (int i = 0; i < packs.length; i++) {
				String pn = packs[i];
				logger.info("dao scan:{}", pn);
				if (pn != null && pn.trim().length() > 0) {
					autoScanClass(binder, pn);
				}
			}
		}
		String srvPackPath = AppProperties.get("resource_DI_SERVICE_PACK_PATH");
		if (srvPackPath != null && srvPackPath.trim().length() > 0) {
			// ...
			// autoScanClass(binder,srvPackPath);
			String packs[] = srvPackPath.split(";");
			for (int i = 0; i < packs.length; i++) {
				String pn = packs[i];
				logger.info("service scan:{}", pn);
				if (pn != null && pn.trim().length() > 0) {
					autoScanClass(binder, pn);
				}
			}
		}
		//
		binder.bindProperties();
	}

	private void autoScanClass(ReleBinder rb, String packname) {
		logger.info("autoScanInClassPathDir:{}", packname);
		Map<Class<?>, Class<?>> kvs = ClassUtil.getPackAllInterfaceImplMap(packname);
		logger.debug("[{}]kvs:{}", packname, kvs.size());
		if (!kvs.isEmpty()) {
			Iterator<?> it = kvs.entrySet().iterator();
			while (it.hasNext()) {
				@SuppressWarnings("unchecked")
				Map.Entry<Class<?>, Class<?>> kv = (Map.Entry<Class<?>, Class<?>>) it.next();
				rb.bind(kv.getKey(), kv.getValue(), true);
			}
		} else {
			logger.info("autoScanInClassPathLibs:{}", packname);
			Map<Class<?>, Class<?>> kvs2 = scanInClassPathLibs(packname);
			logger.debug("[{}]kvs2:{}", packname, kvs2.size());
			if (!kvs2.isEmpty()) {
				Iterator<?> it = kvs2.entrySet().iterator();
				while (it.hasNext()) {
					@SuppressWarnings("unchecked")
					Map.Entry<Class<?>, Class<?>> kv = (Map.Entry<Class<?>, Class<?>>) it.next();
					rb.bind(kv.getKey(), kv.getValue(), true);
				}
			}
		}
	}

	private static Map<Class<?>, Class<?>> scanInClassPathLibs(String packname) {
		logger.info("scan system[java.class.path]");
		Map<Class<?>, Class<?>> allKvs = new HashMap<Class<?>, Class<?>>();
		String x = System.getProperties().getProperty("java.class.path");
		String[] jars = x.split(System.getProperties().getProperty("path.separator"));
		for (int i = 0; i < jars.length; i++) {
			String jar = jars[i];
			if (jar.endsWith(".jar") || jar.endsWith(".JAR")) {
				Map<Class<?>, Class<?>> kvs = ClassUtil.getPackAllInterfaceImplMap(packname, jar);
				if (!kvs.isEmpty()) {
					allKvs.putAll(kvs);
				}
			}
		}
		if (allKvs.isEmpty()) {// java.class.path没有，找web容器底下的
			String webClassPath = DIContainer.class.getClassLoader().getResource("/").getPath();
			logger.debug("webClassPath:{}", webClassPath);
			int i = webClassPath.lastIndexOf("classes");
			if (i > 1) {
				String webLibPath = webClassPath.substring(0, i) + "/lib/";
				logger.debug("webLibPath:{}", webLibPath);
				List<String> jars2 = DirUtil.getCurrentDirectoryFileNames(webLibPath, true, ".jar");
				for (String jar : jars2) {
					//logger.debug("jar:{}",jar);
					Map<Class<?>, Class<?>> kvs = ClassUtil.getPackAllInterfaceImplMap(packname, jar);
					if (!kvs.isEmpty()) {
						allKvs.putAll(kvs);
					}
				}
			}
		}
		return allKvs;
	}

	private void loadXmlFile(ReleBinder rb, String xmlname) {
		String filename = AppProperties.getAppHome() + xmlname;
		File f = new File(filename);
		if (f.exists()) {
			rb.bindFromConfig(f);
			logger.info("loaded {} from file", filename);
		} else {
			InputStream is = null;
			try {
				is = ResourceLoader.getResAsStream(filename);
				rb.bindFromConfig(is);
				logger.info("loaded {} from resource", filename);
			} catch (Exception e) {
				// throw new DependencyInjectionException(e);
				logger.warn("load [{}] err", e.getMessage());
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		}
	}

	private void initBean() {
		for (BeanVO bvo : binder.getBeanVoList()) {
			if (bvo != null) {
				if (bvo.getIface() != null && bvo.getImp() != null) {
					createInstance(bvo.getIface().getName(), bvo.getImp());
				}

			}
		}
		logger.debug("di cache keys:{}", DI_BEAN_CACHE.keySet());
	}

	private void initInject() {
		for (BeanVO bvo : binder.getBeanVoList()) {
			final String key;
			if (bvo.getIface() != null) {
				key = bvo.getIface().getName();
			} else {
				continue;
			}
			List<FieldVO> pl = bvo.getProperties();
			if (pl != null && !pl.isEmpty()) {
				// logger.debug("bean:{}", bean);
				Object bean = getBean(key);
				// Object bean = ReleBinder.getProxyFromCache(key);
				// if (bean == null) {
				// bean = getBean(key);
				// }
				for (FieldVO pvo : pl) {
					String name = pvo.getName();
					String ref = pvo.getRef();
					logger.debug("inject property:{}", pvo);
					if (ref != null && ref.length() > 0) {
						Object propvalue = ReleBinder.getProxyFromCache(ref);
						if (propvalue == null) {
							propvalue = getBean(ref);
						}
						logger.debug("set :{} value", name);
						ObjectUtil.setFieldValue(bean, name, propvalue);
					}
				}
			}
		}

	}

	public synchronized void init() {
		if (!initFlag) {
			initConfigBinder();
			initBean();
			initInject();
			//
			if (binder != null) {
				binder.getBeanVoList().clear();
			}
			initFlag = true;
		}
	}

	/*
	 * public synchronized void reset() { close(); init(); }
	 */
	public synchronized void close() {
		if (binder != null) {
			binder.getBeanVoList().clear();
			binder = null;
		}
		DI_BEAN_CACHE.clear();
		initFlag = false;
	}
}
