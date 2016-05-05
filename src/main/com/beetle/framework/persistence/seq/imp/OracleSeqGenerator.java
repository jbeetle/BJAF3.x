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
package com.beetle.framework.persistence.seq.imp;

import com.beetle.framework.persistence.access.operator.QueryOperator;
import com.beetle.framework.persistence.access.operator.RsDataSet;
import com.beetle.framework.persistence.seq.ISequence;
import com.beetle.framework.persistence.seq.SeqType;
import com.beetle.framework.persistence.seq.SeqType.SeqImpType;

public class OracleSeqGenerator implements ISequence {
	private static OracleSeqGenerator generator = new OracleSeqGenerator();

	private OracleSeqGenerator() {

	}

	public static ISequence getInstance() {
		return generator;
	}

	/**
	 * nextSequenceNumber
	 * 
	 * @param seqtype
	 *            SeqType
	 * @return long
	 * @todo Implement this com.beetle.framework.persistence.dao.ISequence
	 *       method
	 */
	public long nextSequenceNum(SeqType seqtype) {
		long r;
		QueryOperator qo = new QueryOperator();
		qo.setDataSourceName(seqtype.getDataSourceName());
		qo.setSql("SELECT " + seqtype.getSequenceName() + ".nextval FROM dual");
		try {
			qo.access();
			RsDataSet rs = new RsDataSet(qo.getSqlResultSet());
			try {
				r = rs.getFieldValueAsLong(0).longValue();
			} catch (java.lang.ClassCastException cce) {
				r = rs.getFieldValueAsInteger(0).longValue();
			}
			rs.clearAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new java.lang.RuntimeException("get oracle sequence err", e);
		}
		return r;
	}

	public SeqImpType getImpType() {
		return SeqType.SeqImpType.Oracle;
	}

	public void initSequenceValue(int initValue, SeqType seqtype) {
		throw new com.beetle.framework.AppRuntimeException("没有实现");
	}

}
