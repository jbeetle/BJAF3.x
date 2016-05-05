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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Title: BeetleWeb
 * </p>
 * 
 * <p>
 * Description:文件上传页面form 数据对象类
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
public class UploadForm {
	private List<FileObj> fileList;
	private Map<String, String> fieldMap;
	private HttpServletRequest request;
	private HttpServletResponse response;

	public UploadForm(List<FileObj> fileList, Map<String, String> fieldMap,
			HttpServletRequest request, HttpServletResponse response) {
		this.fileList = fileList;
		this.fieldMap = fieldMap;
		this.request = request;
		this.response = response;
	}

	public void addCookie(Cookie cookie) {
		response.addCookie(cookie);
	}

	public String getHeader(String name) {
		return request.getHeader(name);
	}

	public HttpSession getSession() {
		return request.getSession();
	}

	public Object getDataFromSession(String valueName) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		} else {
			return session.getAttribute(valueName);
		}
	}

	public HttpSession getSession(boolean create) {
		return request.getSession(create);
	}

	public Cookie[] getCookies() {
		return request.getCookies();
	}

	/**
	 * 返回上传的文件对象
	 * 
	 * 
	 * @param i
	 *            int
	 * @return FileObj
	 */
	public FileObj getFileObj(int i) {
		return fileList.get(i);
	}

	/**
	 * 文件对象数量
	 * 
	 * @return int
	 */
	public int fileObjCount() {
		return fileList.size();
	}

	public Set<String> getAllFieldNames() {
		return fieldMap.keySet();
	}

	public Collection<String> getAllFieldValues() {
		return fieldMap.values();
	}

	/**
	 * 返回字段的值
	 * 
	 * 
	 * @param fieldName
	 *            String
	 * @return String
	 */
	public String getFieldValue(String fieldName) {
		return (String) fieldMap.get(fieldName);
	}

	/**
	 * 字段数量
	 * 
	 * @return int
	 */
	public int fieldCount() {
		return fieldMap.size();
	}

	public void clear() {
		if (!fileList.isEmpty()) {
			fileList.clear();
		}
		if (!fieldMap.isEmpty()) {
			fieldMap.clear();
		}
	}

}
