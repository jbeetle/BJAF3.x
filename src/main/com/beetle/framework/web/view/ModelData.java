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

import com.beetle.framework.AppRuntimeException;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * 
 * <p>
 * Description: 视图数据对象类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * </p>
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */
public class ModelData {
	private Map<String, Object> dataMap;
	private DataType dataType;
	private static final String dataKey = "DATA_KEY_2009_05_21";

	public enum DataType {
		JAVA, XML, JSON;
	}

	public ModelData() {
		this.dataMap = new HashMap<String, Object>();
		this.dataType = DataType.JAVA;
	}

	/**
	 * 以xml格式返回，针对web service
	 * 
	 * @return
	 */
	public ModelData asXML() {
		this.dataType = DataType.XML;
		return this;
	}

	public DataType getDataType() {
		return dataType;
	}

	public ModelData asJAVA() {
		this.dataType = DataType.JAVA;
		return this;
	}

	/**
	 * 以json格式返回，针对web service
	 * 
	 * @return
	 */
	public ModelData asJSON() {
		this.dataType = DataType.JSON;
		return this;
	}

	/**
	 * 加入一个返回数据,此方法与setData方法互斥
	 * 
	 * 
	 * @param key
	 *            数据的标识
	 * 
	 * @param ObjData
	 *            需要返回的数据对象
	 */
	public void put(String key, Object ObjData) {
		if (dataMap.containsKey(dataKey)) {
			throw new AppRuntimeException(
					"The 'setData' method is called, and no longer allowed to use 'put' method to return data");
		}
		dataMap.put(key, ObjData);
	}

	/**
	 * 设置返回数据对象，此方法与put方法互斥
	 * 
	 * @param dataBean
	 */
	public void setData(Object dataBean) {
		if (!dataMap.isEmpty()) {
			throw new AppRuntimeException(
					"The 'put' method is called, and no longer allowed to use 'setData' method to return data");
		}
		dataMap.put(dataKey, dataBean);
	}

	public Object getData() {
		return dataMap.get(dataKey);
	}

	public boolean isDataBeanFormatReturn() {
		return dataMap.containsKey(dataKey);
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

}
