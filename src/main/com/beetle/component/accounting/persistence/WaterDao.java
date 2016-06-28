package com.beetle.component.accounting.persistence;



import com.beetle.component.accounting.dto.Water;
import com.beetle.framework.persistence.access.operator.DBOperatorException;

public interface WaterDao {
	Water get(Long waterid) throws DBOperatorException;

	/**
	 * 插入一条流水（使用数据库系统时间）
	 * @param water
	 * @return 流水的编号
	 * @throws DBOperatorException
	 */
	long insert(Water water) throws DBOperatorException;

}
