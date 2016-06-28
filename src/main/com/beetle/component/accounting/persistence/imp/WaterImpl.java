package com.beetle.component.accounting.persistence.imp;

import com.beetle.component.accounting.dto.Water;
import com.beetle.component.accounting.persistence.WaterDao;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.TableOperator;
import com.beetle.framework.persistence.access.operator.UpdateOperator;

public class WaterImpl implements WaterDao {

	// fieldsNames:[accountId,amount,orderNo,subjectNo,directFlag,createTime,accountNo,foreBalance,aftBalance,waterId"]
	private TableOperator<Water> operator;

	public WaterImpl() {
		operator = new TableOperator<Water>(PsHelper.DATASOURCE, "water", Water.class, "waterId");
	}

	public Water get(Long id) throws DBOperatorException {
		return operator.selectByPrimaryKey(id);
	}

	public long insert(Water w) throws DBOperatorException {
		String sql = "insert into water (accountId,amount,orderNo,subjectNo,directFlag,accountNo,foreBalance,aftBalance) values (?,?,?,?,?,?,?,?)";
		// return operator.insertAndRetrievePK(water);
		UpdateOperator u = PsHelper.createUpdateOperator(sql);
		u.setReturnGeneratedKeys(true);
		u.addParameter(w.getAccountId());
		u.addParameter(w.getAmount());
		u.addParameter(w.getOrderNo());
		u.addParameter(w.getSubjectNo());
		u.addParameter(w.getDirectFlag());
		u.addParameter(w.getAccountNo());
		u.addParameter(w.getForeBalance());
		u.addParameter(w.getAftBalance());
		u.access();
		return u.getGeneratedKeyValue();
	}

}