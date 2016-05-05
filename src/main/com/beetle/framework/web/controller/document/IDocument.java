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
package com.beetle.framework.web.controller.document;

import com.beetle.framework.web.controller.ControllerException;
import com.beetle.framework.web.controller.WebInput;

/**
 * <p>
 * Title: BeetleWeb
 * </p>
 * 
 * <p>
 * Description: MVC Web Framework
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
public interface IDocument {
	public static final int TYPE_PDF = 10;

	public static final int TYPE_MS_EXCEL = 11;

	public static final int TYPE_MS_WORD = 12;

	/**
	 * 建立文档内容
	 * 
	 * @param webInput
	 *            页面参数输入对象
	 * @param doc
	 *            文档信息对象
	 */
	void createContent(WebInput webInput, DocInfo doc)
			throws ControllerException;

	/**
	 * 建立文档属性信息(例如：Author/Title/CreationDate)
	 * 
	 * @param webInput
	 *            页面参数输入对象
	 * @param doc
	 *            文档信息对象
	 */
	void createAttribute(WebInput webInput, DocInfo doc)
			throws ControllerException;

	/**
	 * 获取此控制器生成文档的类型
	 * @return[TYPE_PDF\TYPE_MS_EXCEL\TYPE_MS_WORD...]
	 */
	int getDocumentType();
}
