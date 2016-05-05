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
package com.beetle.framework.web.controller.upload;

import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * Title: BeetleWeb
 * </p>
 * 
 * <p>
 * Description: 文件对象
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * </p>
 * 
 * @author 余浩东(hdyu@beetlesoft.net)
 * @version 1.0
 */
public class FileObj {
	private FileItem fi;

	public FileObj(FileItem fi) {
		this.fi = fi;
	}

	/**
	 * 获取文件名（含路径）
	 * 
	 * 
	 * @return String
	 */
	public String getFileName() {
		return fi.getName();
	}

	/**
	 * 获取文件名（不带路径）
	 * 
	 * @return
	 */
	public String getNameWithoutPath() {
		String fileNameWithPath = getFileName();
		if (fileNameWithPath != null) {
			int t = fileNameWithPath.lastIndexOf("\\");
			if (t == -1) {
				t = fileNameWithPath.lastIndexOf("/");
			}
			String fileName = fileNameWithPath.substring(t + 1);
			return fileName;
		}
		return null;
	}

	/**
	 * Returns the contents of the file item as an array of bytes.
	 * 
	 * @return byte[]
	 */
	public byte[] get() {
		return fi.get();
	}

	/**
	 * Deletes the underlying storage for a file item, including deleting any
	 * associated temporary disk file.
	 */
	public void delete() {
		fi.delete();
	}

	/**
	 * A convenience method to write an uploaded item to disk
	 * 
	 * @param file
	 *            File
	 * @throws Exception
	 */
	public void write(File file) throws java.lang.Exception {
		fi.write(file);
	}

	/**
	 * Returns an InputStream that can be used to retrieve the contents of the
	 * file.
	 * 
	 * @return InputStream
	 * @throws IOException
	 */
	public InputStream getInputStream() throws java.io.IOException {
		return fi.getInputStream();
	}

	/**
	 * 返回文件大小
	 * 
	 * @return long
	 */
	public long getFileSize() {
		return fi.getSize();
	}

	/**
	 * Returns an OutputStream that can be used for storing the contents of the
	 * file.
	 * 
	 * @return OutputStream
	 * @throws IOException
	 */
	public java.io.OutputStream getOutputStream() throws java.io.IOException {
		return fi.getOutputStream();
	}
}
