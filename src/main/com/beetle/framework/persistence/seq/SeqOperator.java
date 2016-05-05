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

import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.persistence.access.DBConfig;
import com.beetle.framework.persistence.seq.SeqType.SeqImpType;
import com.beetle.framework.persistence.seq.imp.CommonSeqGenerator;
import com.beetle.framework.persistence.seq.imp.DB2SeqGenerator;
import com.beetle.framework.persistence.seq.imp.H2SeqGenerator;
import com.beetle.framework.persistence.seq.imp.MySqlSeqGenerator;
import com.beetle.framework.persistence.seq.imp.OracleSeqGenerator;
import com.beetle.framework.persistence.seq.imp.PostgreSqlSeqGenerator;

/**
 * <p>
 * Title: BeetleSoft Framework
 * </p>
 * 
 * <p>
 * Description: 序列生产工厂
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
final public class SeqOperator {
	private SeqOperator() {
	}

	/**
	 * 返回ISequence对象
	 * 
	 * @param 序列号输入参数对象
	 * 
	 *            SeqType
	 * @return ISequence
	 */
	private static ISequence getSequence(SeqType seqtype) {
		ISequence seq = null;
		String v = seqtype.getImpType().getValue();
		if (v.equalsIgnoreCase("oracle")) {
			seq = OracleSeqGenerator.getInstance();
		} else if (v.equalsIgnoreCase("common")) {
			seq = CommonSeqGenerator.getInstance();
		} else if (v.equalsIgnoreCase("db2")) {
			seq = DB2SeqGenerator.getInstance();
		} else if (v.equalsIgnoreCase("postgresql")) {
			seq = PostgreSqlSeqGenerator.getInstance();
		} else if (v.equalsIgnoreCase("mysql")) {
			seq = MySqlSeqGenerator.getInstance();
		} else if (v.equalsIgnoreCase("h2")) {
			seq = H2SeqGenerator.getInstance();
		} else {
			throw new AppRuntimeException("not support yet[" + v + "]");
		}
		return seq;
	}

	/**
	 * 根据DBConfig.xml中Extensions对应数据源的“seq-type”配置生成序列号
	 * 
	 * @param dataSourceName
	 *            --数据源名称
	 * @param sequenceName
	 *            --序列名称
	 * @return
	 */
	public static long nextSequenceNum(String dataSourceName,
			String sequenceName) {
		String v = DBConfig.getExtensionValue(dataSourceName, "seq-type");
		SeqImpType n = null;
		SeqImpType[] m = SeqType.SeqImpType.values();
		for (int i = 0; i < m.length; i++) {
			if (m[i].getValue().equalsIgnoreCase(v)) {
				n = m[i];
				break;
			}
		}
		if (n == null) {
			throw new AppRuntimeException("not support yet[" + v
					+ "],check DBConfig.xml file(Extensions->" + dataSourceName
					+ "->seq-type value)");
		}
		SeqType st = new SeqType(sequenceName, dataSourceName, n);
		return nextSequenceNum(st);
	}

	/**
	 * 直接返回数据库下一个序列号码
	 * 
	 * 
	 * @param 序列号输入参数对象
	 * 
	 *            SeqType
	 * @return long
	 */
	public static long nextSequenceNum(SeqType seqtype) {
		return getSequence(seqtype).nextSequenceNum(seqtype);
	}

	/**
	 * 初始化一个序列的值
	 * 
	 * 
	 * @param initValue
	 *            int
	 * @param seqtype
	 *            SeqType
	 */
	public static void initSequenceValue(int initValue, SeqType seqtype) {
		getSequence(seqtype).initSequenceValue(initValue, seqtype);
	}
}
