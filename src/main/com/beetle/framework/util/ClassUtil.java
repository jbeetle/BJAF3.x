package com.beetle.framework.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;

public final class ClassUtil {

	public static ClassLoader getClassLoader() {
		ClassLoader classLoader = null;
		try {
			classLoader = Thread.currentThread().getContextClassLoader();
		} catch (Exception t1) {
			try {
				classLoader = ClassUtil.class.getClassLoader();
			} catch (Exception t2) {
				t2.printStackTrace();
			}
		}
		return classLoader;
	}

	public static Class<?> loadClass(final String className) {
		Class<?> clazz;
		try {
			clazz = getClassLoader().loadClass(className);
		} catch (Exception t1) {
			try {
				clazz = Class.forName(className);
			} catch (Exception t2) {
				t2.printStackTrace();
				return null;
			}
		}
		return clazz;
	}

	/**
	 * 找出某个接口或抽象类在某个jar中所有的实现类
	 * 
	 * @param clazz
	 * @param jarfile
	 * @return
	 * @throws IOException
	 */
	public static Class<?>[] findImpClass(Class<?> clazz, String jarfile, ClassLoader loader) throws IOException {
		JarFile jarFile = new JarFile(jarfile);
		Enumeration<?> ee = jarFile.entries();
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		try {
			while (ee.hasMoreElements()) {
				String entry = ee.nextElement().toString();
				if (entry.endsWith(".class") && entry.indexOf('$') == -1) {
					String clazzName = entry.replace('/', '.');
					clazzName = clazzName.substring(0, clazzName.length() - 6);
					Class<?> testClass = loadclass(loader, clazzName);
					if (testClass == null) {
						continue;
					}
					if (clazz.isInterface()) {
						if (hasInterface(testClass, clazz))
							result.add(testClass);
					} else {
						if (isSubClassOf(testClass, clazz))
							result.add(testClass);

					}
				}
			}
			Class<?>[] cls = (Class[]) result.toArray(new Class[result.size()]);
			return cls;
		} finally {
			result.clear();
			jarFile.close();
		}
	}

	private static Class<?> loadclass(ClassLoader loader, String clazzName) {
		Class<?> testClass = null;
		try {
			testClass = loader.loadClass(clazzName);
		} catch (Error e) {
			// e.printStackTrace();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return testClass;
	}

	/**
	 * 根据包路径返回其所有的类
	 * 
	 * @param packageName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = ClassUtil.class.getClassLoader().getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * @param jarFileName
	 * @param packageName
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static Class[] findClassesInJar(String jarFileName, String packageName) throws IOException {
		JarFile jarFile = new JarFile(jarFileName);
		ArrayList<Class> result = new ArrayList<Class>();
		try {
			Enumeration ee = jarFile.entries();
			while (ee.hasMoreElements()) {
				String entry = ee.nextElement().toString();
				if (entry.endsWith(".class") && entry.indexOf('$') == -1) {
					String clazzName = entry.replace('/', '.');
					clazzName = clazzName.substring(0, clazzName.length() - 6);
					if (!clazzName.startsWith(packageName)) {
						continue;
					}
					Class testClass = null;
					try {
						testClass = Class.forName(clazzName);
						result.add(testClass);
					} catch (Error e) {

					} catch (ClassNotFoundException e) {
						// e.printStackTrace();
					}
					if (testClass == null) {
						continue;
					}
				}
			}
			Class[] cls = result.toArray(new Class[result.size()]);
			return cls;
		} finally {
			result.clear();
			jarFile.close();
		}
	}

	@SuppressWarnings("rawtypes")
	public static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		if (files == null) {
			return classes;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(
						Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

	/**
	 * 找出某个接口或抽象类在某个jar中所有的实现类
	 * 
	 * @param clazz
	 * @param jarfile
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static Class[] findImpClass(Class clazz, String jarfile) throws IOException {
		JarFile jarFile = new JarFile(jarfile);
		ArrayList<Class> result = new ArrayList<Class>();
		try {
			Enumeration ee = jarFile.entries();
			while (ee.hasMoreElements()) {
				String entry = ee.nextElement().toString();
				if (entry.endsWith(".class") && entry.indexOf('$') == -1) {
					String clazzName = entry.replace('/', '.');
					clazzName = clazzName.substring(0, clazzName.length() - 6);
					Class testClass = null;
					try {
						testClass = Class.forName(clazzName);
					} catch (Error e) {

					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					if (testClass == null) {
						continue;
					}
					if (clazz.isInterface()) {
						if (hasInterface(testClass, clazz)) {
							result.add(testClass);
						}
					} else {
						if (isSubClassOf(testClass, clazz)) {
							result.add(testClass);
						}
					}
				}
			}
			Class[] cls = result.toArray(new Class[result.size()]);
			return cls;
		} finally {
			result.clear();
			jarFile.close();
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public static boolean isSubClassOf(Class clazz, Class superClass) {
		return clazz.getSuperclass().equals(superClass);
	}

	public static boolean isRootSubClassOf(Class<?> clazz, Class<?> superClass) {
		// return clazz.getSuperclass().equals(superClass);
		Class<?> c = clazz.getSuperclass();
		if (c.equals(superClass)) {
			return true;
		} else if (c.equals(Object.class)) {
			return false;
		}
		return isRootSubClassOf(c, superClass);
	}

	@SuppressWarnings({ "rawtypes" })
	private static boolean hasInterface(Class clazz, Class theInterface) {
		Class cc[] = clazz.getInterfaces();
		for (int i = 0; i < cc.length; i++) {
			if (cc[i].equals(theInterface)) {
				return true;
			}
		}
		return false;
	}

	public static <T> T newInstance(Class<T> clazz) {
		if (!clazz.isInterface()) {
			try {
				return clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes" })
	public static Object newInstance(final String className, final Class[] constrParamTypes,
			final Object[] constrParamValues) throws Exception {
		Object instance;
		try {
			instance = loadClass(className).getConstructor(constrParamTypes).newInstance(constrParamValues);

			if (instance == null) {
				return null;
			}
		} catch (InvocationTargetException ite) {
			Throwable t = ite.getTargetException();

			if (t instanceof Exception) {
				throw (Exception) t;
			}

			throw ite;
		} catch (Exception t) {
			throw t;
		}
		return instance;
	}

	@SuppressWarnings({ "rawtypes" })
	public static Object invoke(final Object object, final String methodName, final Class[] methodParamTypes,
			final Object[] methodParamValues) throws Exception {
		Object result = null;
		try {
			result = object.getClass().getMethod(methodName, methodParamTypes).invoke(object, methodParamValues);
		} catch (InvocationTargetException ite) {
			Throwable t = ite.getTargetException();
			if (t instanceof Exception) {
				throw (Exception) t;
			}
			throw ite;
		} catch (Exception t) {
			t.printStackTrace();
		}
		return result;
	}

	public static Method getClassMethod(Class<?> c, String methodName) {
		Method[] ms = c.getMethods();
		for (int i = 0; i < ms.length; i++) {
			Method m = ms[i];
			if (m.getName().equals(methodName)) {
				return m;
			}
		}
		return null;
	}

	public static Method getMethod(Object o, String methodName) {
		if ((methodName == null) || (o == null)) {
			return null;
		}
		Method[] ms = o.getClass().getMethods();
		for (int i = 0; i < ms.length; i++) {
			Method m = ms[i];
			if (m.getName().equals(methodName)) {
				return m;
			}
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes" })
	public static Object invoke(Object obj, String method, Object[] params, Class[] param_types)
			throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Method m = obj.getClass().getMethod(method, param_types);
		return m.invoke(obj, params);
	}

	public static Object invokeSimple(Object obj, String methodName, Object[] params)
			throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Method m = getClassMethod(obj.getClass(), methodName);
		if (m == null) {
			throw new NoSuchMethodException(obj.getClass().getName() + "." + methodName + ",not found!");
		}
		return m.invoke(obj, params);
	}

	@SuppressWarnings({ "rawtypes" })
	public static Object invoke(Object obj, String method, Object[] params)
			throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Method[] all_methods = obj.getClass().getMethods();
		Method method2invoke = null;
		// find method with the same name and matching argument types
		for (int i = 0; i < all_methods.length; i++) {
			Method m = all_methods[i];
			if (m.getName().equals(method)) {
				Class[] pt = m.getParameterTypes();
				boolean match = true;
				int match_loops = pt.length;
				if (match_loops != params.length) {
					continue;
				}
				for (int j = 0; j < match_loops; j++) {
					if (pt[i].isInstance(params[i]) == false) {
						match = false;
						break;
					}
				}
				if (match == true) {
					method2invoke = m;
				}
			}
		}

		// throw an exception if no method to invoke
		if (method2invoke == null) {
			String t = "(";
			for (int i = 0; i < params.length; i++) {
				if (i != 0) {
					t += ", ";
				}
				t += params[i].getClass().getName();
			}
			t += ")";
			throw new NoSuchMethodException(obj.getClass().getName() + "." + method + t);
		}

		// finally, invoke founded method
		return method2invoke.invoke(obj, params);
	}

	@SuppressWarnings({ "rawtypes" })
	public static boolean isContainMethod(Class c, String methodName) {
		boolean bl = false;
		Method ms[] = c.getDeclaredMethods();
		for (int i = 0; i < ms.length; i++) {
			if (ms[i].getName().equals(methodName)) {
				bl = true;
				break;
			}
		}
		return bl;
	}

	@SuppressWarnings({ "rawtypes" })
	public static boolean isThreadSafe(Class c) {
		boolean bl = true;
		Field[] fs = c.getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			int r = fs[i].getModifiers();
			if (r >= 0 && r <= 2) {
				bl = false;
				break;
			}
		}
		return bl;
	}

	public static final String genMethodKey(Method fm) {
		String a = fm.toGenericString();
		int ii = a.indexOf('(');
		String b = a.substring(ii);
		String c = a.substring(0, ii);
		int jj = c.lastIndexOf('.');
		String d = c.substring(jj);
		String f = d + b;
		return f;
	}

	/**
	 * 获取某个包下面所有的接口及其实现类（只能返回一个实现）
	 * 
	 * @param packName
	 *            包名
	 * @param jarFilename
	 * @return
	 */
	public static Map<Class<?>, Class<?>> getPackAllInterfaceImplMap(String packName, String jarFilename) {
		Map<Class<?>, Class<?>> classkvMap = new HashMap<Class<?>, Class<?>>();
		List<Class<?>> interfaceList = new ArrayList<Class<?>>();
		List<Class<?>> impList = new ArrayList<Class<?>>();
		try {
			@SuppressWarnings("rawtypes")
			Class[] ca = findClassesInJar(jarFilename,packName);
			for (int i = 0; i < ca.length; i++) {
				// System.out.println(ca[i]);
				if (ca[i].isInterface()) {
					interfaceList.add(ca[i]);
				} else {
					impList.add(ca[i]);
				}
			}
			for (int i = 0; i < impList.size(); i++) {
				Class<?> imp = impList.get(i);
				@SuppressWarnings("rawtypes")
				Class[] ff = imp.getInterfaces();
				if (ff == null || ff.length == 0) {
					continue;
				}
				for (int j = 0; j < interfaceList.size(); j++) {
					Class<?> face = interfaceList.get(j);
					// System.out.println(ff[0]);
					// System.out.println(face);
					if (ff[0].equals(face)) {
						classkvMap.put(face, imp);
						break;
					}
				}
			}
			// System.out.println(classkvMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			interfaceList.clear();
			impList.clear();
		}
		return classkvMap;
	}

	/**
	 * 获取某个包下面所有的接口及其实现类（只能返回一个实现）
	 * 
	 * @param packName
	 *            包名
	 * @return
	 */
	public static Map<Class<?>, Class<?>> getPackAllInterfaceImplMap(String packName) {
		Map<Class<?>, Class<?>> classkvMap = new HashMap<Class<?>, Class<?>>();
		List<Class<?>> interfaceList = new ArrayList<Class<?>>();
		List<Class<?>> impList = new ArrayList<Class<?>>();
		try {
			@SuppressWarnings("rawtypes")
			Class[] ca = getClasses(packName);
			for (int i = 0; i < ca.length; i++) {
				// System.out.println(ca[i]);
				if (ca[i].isInterface()) {
					interfaceList.add(ca[i]);
				} else {
					impList.add(ca[i]);
				}
			}
			for (int i = 0; i < impList.size(); i++) {
				Class<?> imp = impList.get(i);
				@SuppressWarnings("rawtypes")
				Class[] ff = imp.getInterfaces();
				if (ff == null || ff.length == 0) {
					continue;
				}
				for (int j = 0; j < interfaceList.size(); j++) {
					Class<?> face = interfaceList.get(j);
					// System.out.println(ff[0]);
					// System.out.println(face);
					if (ff[0].equals(face)) {
						classkvMap.put(face, imp);
						break;
					}
				}
			}
			// System.out.println(classkvMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			interfaceList.clear();
			impList.clear();
		}
		return classkvMap;
	}
}
