package com.beetle.component.accounting.persistence.imp;

import com.beetle.component.accounting.dto.Water;
import com.beetle.component.accounting.persistence.WaterDao;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.TableOperator;

public class WaterImpl implements WaterDao {

	// fieldsNames:[accountId,amount,orderNo,subjectNo,directFlag,createTime,accountNo,foreBalance,aftBalance,waterId"]
	private TableOperator<Water> operator;

	public WaterImpl() {
		operator = new TableOperator<Water>(PsHelper.DATASOURCE, "water", Water.class, "waterId");
	}

	public Water get(Long id) throws DBOperatorException {
		return operator.selectByPrimaryKey(id);
	}

	public long insert(Water water) throws DBOperatorException {
		return operator.insertAndRetrievePK(water);
	}

}