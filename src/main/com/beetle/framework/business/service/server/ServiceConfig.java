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
package com.beetle.framework.business.service.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.dic.DIContainer;
import com.beetle.framework.util.ResourceLoader;

class ServiceConfig {
	private final static Map<String, ServiceDef> scache = new ConcurrentHashMap<String, ServiceConfig.ServiceDef>();
	private final static AppLogger logger = AppLogger
			.getInstance(ServiceConfig.class);

	static {
		String filename = AppProperties.getAppHome() + "ServiceConfig.xml";
		File f = new File(filename);
		if (f.exists()) {
			loadFromConfig(f);
			logger.info("load services from file[{}]", filename);
		} else {
			try {
				loadFromConfig(ResourceLoader.getResAsStream(filename));
				logger.info("load services from resourceloader[{}]", filename);
			} catch (IOException e) {
				logger.warn("no [{}] file found,not load service define data",
						filename);
			}
		}
		// ServiceTransactionRegister.register();
		// command组件内置rpc服务
		ServiceDef cmd = new ServiceDef();
		cmd.setEnabled("true");
		cmd.setIface("com.beetle.framework.business.command.imp.rpc.ICmdService");
		cmd.setImp("com.beetle.framework.business.command.imp.rpc.ServiceCmdImp");
		register(cmd);
		//
	}

	private static void loadFromConfig(InputStream xmlFileInputStream) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(xmlFileInputStream);
			gendoc(doc);
		} catch (Exception de) {
			throw new AppRuntimeException(de);
		} finally {
			if (doc != null) {
				doc.clearContent();
				doc = null;
			}
			reader = null;
		}
	}

	private static void loadFromConfig(File f) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(f);
			gendoc(doc);
		} catch (Exception de) {
			throw new AppRuntimeException(de);
		} finally {
			if (doc != null) {
				doc.clearContent();
				doc = null;
			}
			reader = null;
		}
	}

	private static void gendoc(Document doc) throws ClassNotFoundException {
		Node node = doc.selectSingleNode("binder");
		if (node != null) {
			Iterator<?> it = node.selectNodes("item").iterator();
			while (it.hasNext()) {
				ServiceDef sdf = new ServiceDef();
				Element e = (Element) it.next();
				String face = e.valueOf("@interface");
				String imp = e.valueOf("@implement");
				String enabled = e.valueOf("@enabled");
				sdf.setIface(face);
				sdf.setImp(imp);
				sdf.setEnabled(enabled);
				@SuppressWarnings("unchecked")
				Iterator<Attribute> ait = e.attributeIterator();
				while (ait.hasNext()) {
					Attribute at = ait.next();
					sdf.addExtension(at.getName(), at.getValue());
				}
				register(sdf);
			}
		}
	}

	public static ServiceDef lookup(String iface) {
		return scache.get(iface);
	}

	public static void register(ServiceDef sdf) {
		scache.put(sdf.getIface(), sdf);
	}

	public static class ServiceDef {
		public static class MethodEx {

			private Method method;

			public Method getMethod() {
				return method;
			}

			public void setMethod(Method method) {
				this.method = method;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result
						+ ((method == null) ? 0 : method.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				MethodEx other = (MethodEx) obj;
				if (method == null) {
					if (other.method != null)
						return false;
				} else if (!method.equals(other.method))
					return false;
				return true;
			}

		}

		public MethodEx getMethodEx(String mkey, String methodName,
				Class<?>[] parameterTypes) throws Exception {
			MethodEx m = this.methodCache.get(mkey);
			if (m != null) {
				return m;
			}
			synchronized (this) {
				if (!methodCache.containsKey(mkey)) {
					Object impl = this.getServiceImpInstanceRef();
					Method tm = impl.getClass().getDeclaredMethod(methodName,
							parameterTypes);
					MethodEx mex = new MethodEx();
					mex.setMethod(tm);
					methodCache.put(mkey, mex);
				}
				return methodCache.get(mkey);
			}
		}

		public Object getServiceImpInstanceRef() {
			// return BusinessContext.serviceLookup(this.iface);
			return DIContainer.getInstance().retrieve(this.iface);
		}

		public ServiceDef() {
			this.extensions = new HashMap<String, String>();
			this.methodCache = new HashMap<String, MethodEx>();
		}

		private String iface;
		private String imp;
		private String enabled;
		private final Map<String, MethodEx> methodCache;

		public String getEnabled() {
			return enabled;
		}

		public void setEnabled(String enabled) {
			this.enabled = enabled;
		}

		private Map<String, String> extensions;

		public String getIface() {
			return iface;
		}

		public void setIface(String iface) {
			this.iface = iface;
		}

		public String getImp() {
			return imp;
		}

		public void setImp(String imp) {
			this.imp = imp;
		}

		public Map<String, String> getExtensions() {
			return extensions;
		}

		public void addExtension(String key, String value) {
			extensions.put(key, value);
		}

		public String getExtensionValue(String key) {
			return extensions.get(key);
		}

		@Override
		public String toString() {
			return "ServiceDef [" + extensions + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((iface == null) ? 0 : iface.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ServiceDef other = (ServiceDef) obj;
			if (iface == null) {
				if (other.iface != null)
					return false;
			} else if (!iface.equals(other.iface))
				return false;
			return true;
		}
	}
}
