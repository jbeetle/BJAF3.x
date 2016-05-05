package com.beetle.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourceLoader {
	/**
	 * 从一个jar文件里面获取资源
	 * 
	 * @param jarFileName
	 *            --jar文件（含路径）
	 * @param resUrl
	 *            --资源url
	 * @return 输入流
	 * @throws IOException
	 */
	public static InputStream getResourceFromJarFile(String jarFileName,
			String resUrl) throws IOException {
		JarFile jarFile = new JarFile(jarFileName);
		JarEntry je = jarFile.getJarEntry(resUrl);
		if (je == null) {
			jarFile.close();
			throw new IOException("resur not fount");
		}
		try {
			return jarFile.getInputStream(je);
		} finally {
			// jarFile.close();
		}
	}

	/**
	 * 获取jar文件中文本性资源的内容
	 * 
	 * @param jarFileName
	 *            (eg:D:\\tmp\\test.jar)
	 * @param resUrl
	 *            (eg:config/job.properties)
	 * @return
	 * @throws IOException
	 */
	public static String getResourceContentFromJarFile(String jarFileName,
			String resUrl) throws IOException {
		StringBuffer sb = new StringBuffer();
		JarFile jarFile = new JarFile(jarFileName);
		JarEntry je = jarFile.getJarEntry(resUrl);
		try {
			if (je != null) {

				readJar(sb, jarFile, je);
			}
		} finally {
			jarFile.close();
		}
		return sb.toString();
	}

	private static void readJar(StringBuffer sb, JarFile jarFile, JarEntry je)
			throws IOException {
		InputStream is = null;
		BufferedReader br = null;
		try {
			is = jarFile.getInputStream(je);
			br = new BufferedReader(new InputStreamReader(is));
			while (true) {
				String s = br.readLine();
				if (s == null) {
					break;
				} else {
					sb.append(s);
					sb.append('\n');
				}
			}
		} finally {
			if (br != null) {
				br.close();
			}
			if (is != null) {
				is.close();
			}
			je = null;
		}
	}

	/**
	 * 从一个jar文件里面获取资源
	 * 
	 * @param jarFileName
	 *            (eg:D:\\tmp\\test.jar)
	 * @param resUrl
	 *            (eg:config/job.properties)
	 * @return
	 */
	public static Properties getPropertiesResourceFromJarFile(
			String jarFileName, String resUrl) {
		InputStream is = null;
		try {
			is = getResourceFromJarFile(jarFileName, resUrl);
			Properties p = new Properties();
			p.load(is);
			return p;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static ClassLoader defaultClassLoader;

	public static URL getResURL(String resource) throws IOException {
		return getResURL(getClassLoader(), resource);
	}

	public static URL getResURL(ClassLoader loader, String resource)
			throws IOException {
		URL url = null;
		if (loader != null) {
			url = loader.getResource(resource);
		}
		if (url == null) {
			url = ClassLoader.getSystemResource(resource);
		}
		if (url == null) {
			throw new IOException("Could not find resource " + resource);
		}
		return url;
	}

	public static ClassLoader getClassLoader() {
		if (defaultClassLoader != null) {
			return defaultClassLoader;
		} else {
			return Thread.currentThread().getContextClassLoader();
		}
	}

	public static InputStream getResAsStream(String resource)
			throws IOException {
		return getResAsStream(getClassLoader(), resource);
	}

	public static InputStream getResAsStream(ClassLoader loader, String resource)
			throws IOException {
		InputStream in = null;
		if (loader != null) {
			in = loader.getResourceAsStream(resource);
		}
		if (in == null) {
			in = ClassLoader.getSystemResourceAsStream(resource);
		}
		if (in == null) {
			URL url = new URL(resource);
			in = url.openStream();
		}
		if (in == null) {
			throw new IOException("not found resource: [" + resource + "]");
		}
		return in;
	}

	public static void setDefaultClassLoader(ClassLoader dcl) {
		defaultClassLoader = dcl;
	}

}
