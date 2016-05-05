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

public class DIContainer {
	private final static Map<String, Object> DI_BEAN_CACHE = new ConcurrentHashMap<String, Object>();
	private static DIContainer instance = new DIContainer();
	private static ReleBinder binder;
	private static final AppLogger logger = AppLogger
			.getInstance(DIContainer.class);
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
		String key = face.getName();
		Object o = ReleBinder.getProxyFromCache(key);
		if (o == null) {
			o = getBean(key);
		}
		return (T) o;
	}

	void createInstance(String beanName, Class<?> impl) {
		try {
			Object bean = ClassUtil.newInstance(impl);
			DI_BEAN_CACHE.put(beanName, bean);
		} catch (Exception e) {
			throw new DependencyInjectionException(
					"Failed to initialize the Bean", e);
		}
	}

	void keepInstance(String key, Object obj) {
		DI_BEAN_CACHE.put(key, obj);
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
			return binder;
		}
	}

	static Object getBean(String name) {
		return DI_BEAN_CACHE.get(name);
	}

	private void initConfigBinder() {
		if (binder == null) {
			synchronized (logger) {
				if (binder != null)
					return;
				binder = new ReleBinder();
				// loadXmlFile(rb, "DAOConfig.xml");
				// loadXmlFile(rb, "ServiceConfig.xml");
				// loadXmlFile(rb, "AOPConfig.xml");
				String files[] = AppProperties.get(
						"resource_DI_CONTAINER_FILES",
						"DAOConfig.xml;ServiceConfig.xml;AOPConfig.xml").split(
						";");
				for (int i = 0; i < files.length; i++) {
					loadXmlFile(binder, files[i]);
				}
				//auto scan
				String daoPackPath=AppProperties.get("resource_DI_DAO_PACK_PATH");
				if(daoPackPath!=null&&daoPackPath.trim().length()>0){
					autoScanClass(binder,daoPackPath);
				}
				String srvPackPath=AppProperties.get("resource_DI_SERVICE_PACK_PATH");
				if(srvPackPath!=null&&srvPackPath.trim().length()>0){
					//...
					autoScanClass(binder,srvPackPath);
				}
				//
				binder.bindProperties();
			}
		}
	}
	private void autoScanClass(ReleBinder rb, String packname) {
		 Map<Class<?>, Class<?>> kvs=ClassUtil.getPackAllInterfaceImplMap(packname);
		 if(!kvs.isEmpty()){
			 Iterator<?> it=kvs.entrySet().iterator();
			 while(it.hasNext()){
				 @SuppressWarnings("unchecked")
				Map.Entry<Class<?>, Class<?>> kv=(Map.Entry<Class<?>, Class<?>>) it.next(); 
				 rb.bind(kv.getKey(), kv.getValue(), true);
			 }						 
		 }
		 logger.info("autoScanClass:{}",packname);
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
