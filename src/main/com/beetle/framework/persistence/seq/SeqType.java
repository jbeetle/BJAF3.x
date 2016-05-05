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
package com.beetle.framework.persistence.seq;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * 
 * <p>
 * Description: 序列号输入参数对象
 * 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件
 * 
 * </p>
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */
public class SeqType {

	public enum SeqImpType {
		Common("common"), Oracle("oracle"), DB2("db2"), PostgreSql("postgresql"), MySql(
				"mysql"), H2("H2"), Other("other");
		public String getValue() {
			return value;
		}

		private String value;

		private SeqImpType(String value) {
			this.value = value;
		}

	};

	private String sequenceName;

	private String dataSourceName;
	private SeqImpType sit;

	/**
	 * SeqType
	 * 
	 * @param sequenceName
	 *            序列名称
	 * @param dataSourceName
	 *            数据源名称
	 * 
	 * @param impType
	 *            实现类型(在ISequence接口定义)
	 */
	public SeqType(String sequenceName, String dataSourceName,
			SeqImpType impType) {
		this.sequenceName = sequenceName;
		this.sit = impType;
		this.dataSourceName = dataSourceName;
	}

	public String getSequenceName() {
		return sequenceName.trim();
	}

	public String getDataSourceName() {
		return dataSourceName.trim();
	}

	public SeqImpType getImpType() {
		return sit;
	}

}
