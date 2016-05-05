package com.beetle.framework.persistence.seq.imp;

import com.beetle.framework.persistence.access.operator.QueryOperator;
import com.beetle.framework.persistence.access.operator.RsDataSet;
import com.beetle.framework.persistence.seq.ISequence;
import com.beetle.framework.persistence.seq.SeqType;
import com.beetle.framework.persistence.seq.SeqType.SeqImpType;

/**
 * <pre>
 *  DROP TABLE IF EXISTS `Sequences`;
 * CREATE TABLE IF NOT EXISTS `Sequences` (
 *   `name` varchar(50) NOT NULL,
 *   `current_value` bigint NOT NULL,
 *   `increment` bigint NOT NULL DEFAULT '1',
 *   PRIMARY KEY (`name`)
 * );
 * DROP FUNCTION IF EXISTS `seqCurrval`;
 * DELIMITER $
 * CREATE FUNCTION `seqCurrval`(`seq_name` VARCHAR(50)) RETURNS bigint
 * BEGIN   
 * 	  DECLARE value INTEGER;   
 * 	  SET value = 0;   
 * 	  SELECT current_value INTO value   
 * 	  FROM Sequences   
 * 	  WHERE name = seq_name;   
 * 	  RETURN value;   
 * END$
 * DELIMITER ;
 * DROP FUNCTION IF EXISTS `seqNextval`;
 * DELIMITER $
 * CREATE FUNCTION `seqNextval`(`seq_name` VARCHAR(50)) RETURNS bigint
 * BEGIN   
 * 	   UPDATE Sequences   
 * 	   SET          current_value = current_value + increment   
 * 	   WHERE name = seq_name;   
 * 	   RETURN seqCurrval(seq_name);   
 * END$
 * DELIMITER ;
 * </pre>
 * 
 */
public class MySqlSeqGenerator implements ISequence {
	private static MySqlSeqGenerator instance = new MySqlSeqGenerator();

	public static MySqlSeqGenerator getInstance() {
		return instance;
	}

	private MySqlSeqGenerator() {
		super();
	}

	@Override
	public long nextSequenceNum(SeqType seqtype) {
		long r;
		QueryOperator qo = new QueryOperator();
		qo.setDataSourceName(seqtype.getDataSourceName());
		qo.setSql("select seqnextval(?)");
		// qo.setSql("SELECT " + seqtype.getSequenceName() +
		// ".nextval FROM dual");
		qo.addParameter(seqtype.getSequenceName());
		RsDataSet rs = null;
		try {
			qo.access();
			rs = new RsDataSet(qo.getSqlResultSet());
			try {
				r = rs.getFieldValueAsLong(0).longValue();
			} catch (java.lang.ClassCastException cce) {
				r = rs.getFieldValueAsInteger(0).longValue();
			}
		} catch (Exception e) {
			throw new java.lang.RuntimeException("get mysql sequence err", e);
		} finally {
			if (rs != null) {
				rs.clearAll();
			}
		}
		return r;
	}

	@Override
	public SeqImpType getImpType() {
		return SeqType.SeqImpType.MySql;
	}

	@Override
	public void initSequenceValue(int initValue, SeqType seqtype) {
		throw new com.beetle.framework.AppRuntimeException(
				"Unrealized, please execute mysqlseq.sql script by manual");
	}

}
