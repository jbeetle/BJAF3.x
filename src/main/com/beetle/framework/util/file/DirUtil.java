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
package com.beetle.framework.util.file;

import com.beetle.framework.AppRuntimeException;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class DirUtil {

	/**
	 * 列出某个目录下所有的文件名
	 * 
	 * 
	 * @param dirPath
	 * @param includePath
	 *            -true文件名包含路径，false不带路径
	 * @return
	 */
	public final static List<String> getCurrentDirectoryFileNames(
			String dirPath, boolean includePath) {
		File file = new File(dirPath);
		if (!file.isDirectory()) {
			throw new AppRuntimeException(dirPath
					+ " is not a directory,can't deal!");
		}
		List<String> l = new ArrayList<String>();
		File[] fs = file.listFiles();
		if (fs == null) {
			return l;
		}
		for (int i = 0; i < fs.length; i++) {
			File f = fs[i];
			if (f.isFile()) {
				if (includePath) {
					l.add(f.getPath());
				} else {
					l.add(f.getName());
				}
			}
		}
		return l;
	}

	private static class FF implements FileFilter {
		private String suffixname;

		public FF(String suffixname) {
			super();
			this.suffixname = suffixname;
		}

		public boolean accept(File arg0) {
			if (arg0.isDirectory()) {
				return false;
			} else {
				String name = arg0.getName();
				int k = name.lastIndexOf('.');
				if (k <= 0) {
					return false;
				}
				name = name.substring(k);
				if (name.equalsIgnoreCase(suffixname)) {
					return true;
				} else {
					return false;
				}
			}
		}

	}

	/**
	 * 删除文件名中的路径，只返回文件名
	 * 
	 * @param filename
	 * @return
	 */
	public final static String removePath(String filename) {
		int i = filename.lastIndexOf('/');
		if (i == -1) {
			// return filename;
			i = filename.lastIndexOf('\\');
			if (i == -1) {
				return filename;
			} else {
				return filename.substring(i + 1);
			}
		} else {
			return filename.substring(i + 1);
		}
	}

	/**
	 * 文件最后一次被修改的时间
	 * 
	 * 
	 * @param filename
	 * @return 0-表示文件不存在或无权限读取，大于0为修改时间
	 */
	public static long getFileLastModified(String filename) {
		long l = 0;
		File f = new File(filename);
		if (f.exists()) {
			try {
				l = f.lastModified();
			} catch (SecurityException se) {
				l = 0;
				se.printStackTrace();
			}
		}
		return l;
	}

	/**
	 * 列出某个目录下所有的文件名
	 * 
	 * 
	 * @param dirPath
	 * @param includePath
	 *            '.txt'
	 * @return
	 */
	public final static List<String> getCurrentDirectoryFileNames(
			String dirPath, boolean includePath, String suffixname) {
		File file = new File(dirPath);
		if (!file.isDirectory()) {
			throw new AppRuntimeException(dirPath
					+ " is not a directory,can't deal!");
		}
		List<String> l = new ArrayList<String>();
		File[] fs = file.listFiles(new FF(suffixname));
		if (fs == null) {
			return l;
		}
		for (int i = 0; i < fs.length; i++) {
			File f = fs[i];
			if (f.isFile()) {
				if (includePath) {
					l.add(f.getPath());
				} else {
					l.add(f.getName());
				}
			}
		}
		return l;
	}
}
