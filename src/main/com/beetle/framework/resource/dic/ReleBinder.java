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
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.dic.aop.AopInterceptor;
import com.beetle.framework.resource.dic.aop.InnerHandler;
import com.beetle.framework.resource.dic.def.Aop;
import com.beetle.framework.resource.dic.def.AsyncMethodCallback;
import com.beetle.framework.resource.dic.def.InjectField;
import com.beetle.framework.resource.dic.def.ServiceTransaction;
import com.beetle.framework.util.ClassUtil;

/**
 * 关系依赖管理
 * 
 * @author HenryYu
 * 
 */
public class ReleBinder {
	private final List<BeanVO> beanVoList;
	private static AppLogger logger = AppLogger.getInstance(ReleBinder.class);
	private final static Map<String, Object> DI_AOP_PROXY_CACHE = new ConcurrentHashMap<String, Object>();
	private final static Set<String> DAO_CHECK = new java.util.HashSet<String>();

	public ReleBinder() {
		super();
		this.beanVoList = new ArrayList<BeanVO>();
	}

	public static Object getProxyFromCache(String key) {
		return DI_AOP_PROXY_CACHE.get(key);
	}

	public List<BeanVO> getBeanVoList() {
		return beanVoList;
	}

	public static class FieldVO {

		private String name;

		private String ref;

		public FieldVO(String name, String ref) {
			this.name = name;
			this.ref = ref;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getRef() {
			return ref;
		}

		public void setRef(String ref) {
			this.ref = ref;
		}

		@Override
		public String toString() {
			return "PropertyVO [name=" + name + ", ref=" + ref + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((ref == null) ? 0 : ref.hashCode());
			return result;
		}

	}

	public static class BeanVO {
		private static final Map<Method, ServiceTransaction.Manner> trans = new HashMap<Method, ServiceTransaction.Manner>(
				1334);
		private static final Map<Method, Integer> async = new HashMap<Method, Integer>();

		public static Map<Method, ServiceTransaction.Manner> getTrans() {
			return trans;
		}

		public static Map<Method, Integer> getAsync() {
			return async;
		}

		public static boolean existInTrans(Method key) {
			return trans.containsKey(key);
		}

		public static boolean existInAsync(Method key) {
			return async.containsKey(key);
		}

		public static int getFromAsync(Method key) {
			return async.get(key);
		}

		public static ServiceTransaction.Manner getFromTrans(Method key) {
			return trans.get(key);
		}

		public BeanVO(Class<?> iface, Class<?> imp, boolean single, int flag) {
			super();
			this.iface = iface;
			this.imp = imp;
			this.single = single;
			this.flag = flag;
			this.properties = new ArrayList<FieldVO>();
		}

		public BeanVO(String aopId, AopInterceptor interceptor) {
			super();
			this.aopId = aopId;
			this.interceptor = interceptor;
			this.properties = new ArrayList<FieldVO>();
		}

		private String aopId;
		private AopInterceptor interceptor;
		private Method aopMethod;
		private Class<?> iface;
		private Class<?> imp;
		private boolean single;
		private int flag;
		private final List<FieldVO> properties;// for inject

		// private Method transMethod;
		// private ServiceTransaction.Manner transManner;

		public Method getAopMethod() {
			return aopMethod;
		}

		public void setAopMethod(Method aopMethod) {
			this.aopMethod = aopMethod;
		}

		public List<FieldVO> getProperties() {
			return properties;
		}

		public String getAopId() {
			return aopId;
		}

		public AopInterceptor getInterceptor() {
			return interceptor;
		}

		@SuppressWarnings("rawtypes")
		public Class getIface() {
			return iface;
		}

		public Class<?> getImp() {
			return imp;
		}

		public int getFlag() {
			return flag;
		}

		public boolean isSingle() {
			return single;
		}

		@Override
		public String toString() {
			return "BeanVO [aopId=" + aopId + ", interceptor=" + interceptor + ", aopMethod=" + aopMethod + ", iface="
					+ iface + ", imp=" + imp + ", single=" + single + ", flag=" + flag + ", properties=" + properties
					+ "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((aopId == null) ? 0 : aopId.hashCode());
			result = prime * result + ((aopMethod == null) ? 0 : aopMethod.hashCode());
			result = prime * result + flag;
			result = prime * result + ((iface == null) ? 0 : iface.hashCode());
			result = prime * result + ((imp == null) ? 0 : imp.hashCode());
			result = prime * result + ((interceptor == null) ? 0 : interceptor.hashCode());
			result = prime * result + ((properties == null) ? 0 : properties.hashCode());
			result = prime * result + (single ? 1231 : 1237);
			return result;
		}

	}

	protected void bindAopInterceptor(String aopid, AopInterceptor interceptor) {
		BeanVO bvo = new BeanVO(aopid, interceptor);
		if (!beanVoList.contains(bvo)) {
			beanVoList.add(bvo);
		}
	}

	/**
	 * 自我绑定
	 * 
	 * @param clazz
	 *            实现类
	 * @param singleton
	 *            是否单例（true--单例）
	 */
	public void bind(Class<?> clazz, boolean singleton) {
		BeanVO bvo = new BeanVO(null, clazz, singleton, 1);
		if (!beanVoList.contains(bvo)) {
			beanVoList.add(bvo);
		}
	}

	/**
	 * 接口-实现（包括：抽象--扩展）绑定
	 * 
	 * @param interfaceClazz
	 *            接口类（或抽象类）
	 * @param implementClazz
	 *            实现类（扩展类）
	 * @param singleton
	 */
	public void bind(Class<?> interfaceClazz, Class<?> implementClazz, boolean singleton) {
		if (!interfaceClazz.isAssignableFrom(implementClazz)) {
			throw new AppRuntimeException("err:" + interfaceClazz.getSimpleName() + " must be "
					+ implementClazz.getSimpleName() + "'s father or interface!");
		}
		BeanVO bvo = new BeanVO(interfaceClazz, implementClazz, singleton, 2);
		if (!beanVoList.contains(bvo)) {
			beanVoList.add(bvo);
		}
		// beanVoList.add(new BeanVO(interfaceClazz, implementClazz, singleton,
		// 2));
	}

	/**
	 * 从配置进行绑定
	 * 
	 * @param xmlFileInputStream
	 *            --配置文件输入流
	 */
	public void bindFromConfig(InputStream xmlFileInputStream) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(xmlFileInputStream);
			gendoc(doc);
			// bindProperties();
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

	/**
	 * 从配置进行绑定 文件为XML，格式如下：<br>
	 * 
	 * <pre>
	 *  <binder>
	 *     <!-- 
	 * interface-服务接口或抽象类，没有接口可不定义此属性
	 * implement-接口实现类，抽象的扩展类，或提供者类，或非接口实现类（此时不定义interface属性）
	 * singleton-是否为单例（针对implement而已），不定义此属性，默认为true
	 * 	-->
	 * 	<item interface="com.beetle.framework.util.pattern.di.IService" implement="com.beetle.framework.util.pattern.di.IServiceImp" singleton="true"/>
	 * </binder>
	 * </pre>
	 * 
	 * <br>
	 * 
	 * @param f
	 *            --指定配置文件
	 */
	public void bindFromConfig(File f) {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(f);
			gendoc(doc);
			// bindProperties();
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

	private void gendoc(Document doc) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Node node = doc.selectSingleNode("binder");
		if (node != null) {
			Iterator<?> it = node.selectNodes("item").iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next();
				String aopid = e.valueOf("@aopid");
				if (aopid != null && aopid.trim().length() > 0) {
					String inerceptor = e.valueOf("@interceptor");
					this.bindAopInterceptor(aopid, (AopInterceptor) Class.forName(inerceptor.trim()).newInstance());
				} else {
					dealInject(e);
				}
			}
		}
	}

	private void dealInject(Element e) throws ClassNotFoundException {
		String face = e.valueOf("@interface");
		String imp = e.valueOf("@implement");
		boolean f = face.toLowerCase().matches(".*\\.dao\\..*");
		if (f) {
			DAO_CHECK.add(face);
		}
		String singleton = e.valueOf("@singleton");
		boolean sf = false;
		if (singleton == null || singleton.trim().length() == 0) {
			sf = true;
		} else {
			if (singleton.trim().equalsIgnoreCase("true")) {
				sf = true;
			}
		}
		if (imp == null || imp.trim().length() == 0) {
			throw new DependencyInjectionException("config content formate err,implement can't be null");
		}
		if (face == null || face.trim().length() == 0) {
			bind(Class.forName(imp.trim()), sf);
		} else {
			if (Class.forName(face.trim()).isAssignableFrom(Class.forName(imp.trim()))) {
				bind(Class.forName(face.trim()), Class.forName(imp.trim()), sf);
			} else {
				throw new DependencyInjectionException("not support this case!");
			}
		}
	}

	void bindProperties() {
		logger.debug("beanVoList size:{}", beanVoList.size());
		logger.debug("beanVoList:{}", beanVoList);
		for (BeanVO vo : beanVoList) {
			setBeanVoProperty(vo);
		}
	}
	
	private void setBeanVoProperty(BeanVO bvo) {
		logger.debug("bvo:{}", bvo);
		Class<?> imp = bvo.getImp();
		if (imp == null) {// aop case
			return;
		}
		Field[] fields = imp.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field f : fields) {
				if (f.isAnnotationPresent(InjectField.class)) {
					if (DAO_CHECK.contains(bvo.getIface().getName())) {
						throw new DependencyInjectionException(
								"DAO cannot use injectField statement, do not conform to the programming paradigm");
					}
					FieldVO pvo = new FieldVO(f.getName(), f.getType().getName());
					bvo.getProperties().add(pvo);
					logger.debug("pvo:{}", pvo);
				}
			}
		}
		Method[] methods = imp.getDeclaredMethods();
		for (Method m : methods) {
			if (m.isAnnotationPresent(Aop.class)) {
				dealAop(bvo, m);
			}
			if (m.isAnnotationPresent(ServiceTransaction.class)) {
				if (DAO_CHECK.contains(bvo.getIface().getName())) {
					throw new DependencyInjectionException(
							"DAO cannot use transaction statement, do not conform to the programming paradigm");
				}
				dealTrans(bvo, m);
			}
			// async call
			Class<?> pts[] = m.getParameterTypes();
			for (int i = 0; i < pts.length; i++) {
				if (pts[i].isAssignableFrom(AsyncMethodCallback.class)) {
					dealAsyncCall(bvo, m, i);
					break;
				}
			}
		}
	}

	private void dealAsyncCall(BeanVO bvo, Method m, int pos) {
		initProxyAndCache(bvo);
		String f2 = ClassUtil.genMethodKey(m);
		Method[] fms = bvo.getIface().getDeclaredMethods();
		for (Method fm : fms) {
			String f1 = ClassUtil.genMethodKey(fm);
			if (f1.equals(f2)) {
				// bvo.getTrans().put(fm, manner);
				BeanVO.getAsync().put(fm, pos);
				break;
			}
		}
	}

	private void dealTrans(BeanVO bvo, Method m) {
		ServiceTransaction.Manner manner = m.getAnnotation(ServiceTransaction.class).manner();
		initProxyAndCache(bvo);
		String f2 = ClassUtil.genMethodKey(m);
		Method[] fms = bvo.getIface().getDeclaredMethods();
		for (Method fm : fms) {
			String f1 = ClassUtil.genMethodKey(fm);
			if (f1.equals(f2)) {
				// bvo.getTrans().put(fm, manner);
				BeanVO.getTrans().put(fm, manner);
				break;
			}
		}
	}

	private void dealAop(BeanVO bvo, Method m) {
		String aopId_m = m.getAnnotation(Aop.class).id();
		if (aopId_m == null || aopId_m.length() == 0) {
			throw new DependencyInjectionException(
					logger.strFormat("Initialization error of AOP,the method[{}]'s aop id must be setted!", m));
		}
		initProxyAndCache(bvo);
		Method[] fms = bvo.getIface().getDeclaredMethods();
		String f2 = ClassUtil.genMethodKey(m);
		for (Method fm : fms) {
			String f1 = ClassUtil.genMethodKey(fm);
			if (f1.equals(f2)) {
				matchAopMethod(fm, aopId_m);
				break;
			}
		}
	}

	private void initProxyAndCache(BeanVO bvo) {
		try {
			if (!DI_AOP_PROXY_CACHE.containsKey(bvo.getIface().getName())) {
				Object proxy = Proxy.newProxyInstance(bvo.getIface().getClassLoader(),
						new Class<?>[] { bvo.getIface() }, new InnerHandler(bvo.getIface()));
				DI_AOP_PROXY_CACHE.put(bvo.getIface().getName(), proxy);
				// logger.debug("bind proxy:{},bvo:{}", proxy, bvo);
			}
		} catch (Exception e) {
			throw new DependencyInjectionException("Initialization error of AOP", e);
		}
	}

	private void matchAopMethod(Method m, String aopId_m) {
		for (BeanVO votmp : beanVoList) {
			String aid = votmp.getAopId();
			if (aid == null) {
				continue;
			}
			if (aid.equals(aopId_m)) {
				votmp.setAopMethod(m);
				break;
			}
		}
	}
}
