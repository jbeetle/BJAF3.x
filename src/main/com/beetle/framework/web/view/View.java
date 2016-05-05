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
package com.beetle.framework.web.view;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title: BeetleWeb
 * </p>
 * 
 * <p>
 * Description: 显示视图类
 * 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * 
 * </p>
 * 
 * @author 余浩东(hdyu@beetlesoft.net)
 * @version 1.0
 */
public class View {
	private Map<String, Object> data;
	private String viewname;
	private ModelData md;

	/**
	 * View
	 * 
	 * @param viewname
	 *            视图名称，对应WebView.xml;或具体的视图文件的url
	 */
	public View(String viewname) {
		this.viewname = viewname;
		this.data = null;
	}

	/**
	 * View
	 * 
	 * @param viewname
	 *            视图名称，对应WebView.xml;或具体的视图文件的url
	 * @param modelData
	 *            视图数据对象
	 */
	public View(String viewname, ModelData modelData) {
		this.viewname = viewname;
		this.md = modelData;
		this.data = new HashMap<String, Object>(modelData.getDataMap());
	}

	/**
	 * 返回视图名称
	 * 
	 * @return String
	 */
	public String getViewname() {
		return viewname;
	}

	/**
	 * 返回视图数据
	 * 
	 * @return Map
	 */
	public Map<String, Object> getData() {
		return data;
	}

	/**
	 * 通过名称获取视图数据
	 * 
	 * @param keyName
	 *            视图返回数据的存放名称
	 * 
	 * @return 视图数据
	 */
	public Object getDataByName(String keyName) {
		return data.get(keyName);
	}

	public ModelData getMd() {
		return md;
	}

	public void clear() {
		if (data != null) {
			data.clear();
			md.getDataMap().clear();
		}
	}
}
