package com.beetle.component.accounting.persistence.imp;

import com.beetle.component.accounting.dto.Account;
import com.beetle.component.accounting.persistence.AccountDao;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.QueryOperator;
import com.beetle.framework.persistence.access.operator.TableOperator;
import com.beetle.framework.persistence.access.operator.UpdateOperator;

public class AccountImpl implements AccountDao {

	// fieldsNames:[subjectNo,accountName,accountType,updateTime,accountStatus,accountId,memberNo,password,balance,createTime,accountNo,passwordCheck,subjectDirect"]
	private TableOperator<Account> tableOperator;

	public AccountImpl() {
		// operator = new TableOperator<Account>(PsHelper.DATASOURCE, "account",
		// Account.class);
		//// 针对包含自增字段的表，使用下面的构造函数
		tableOperator = new TableOperator<Account>(PsHelper.DATASOURCE, "account", Account.class, "accountId");
	}

	public Account get(Long id) throws DBOperatorException {
		return tableOperator.selectByPrimaryKey(id);
	}

	public int insert(Account account) throws DBOperatorException {
		return tableOperator.insert(account);
	}

	public int update(Account account) throws DBOperatorException {
		return tableOperator.update(account);
	}

	public int delete(Long id) throws DBOperatorException {
		return tableOperator.deleteByPrimaryKey(id);
	}

	@Override
	public Account get(String accountNo) throws DBOperatorException {
		String sql = "select * from account where accountNo = ?";
		QueryOperator qo = PsHelper.createQueryOperator(sql);
		qo.addParameter(accountNo);
		qo.access();
		return qo.getResultAsObject(Account.class);
	}

	@Override
	public Account getAndLock(Long accountid) throws DBOperatorException {
		String sql = "select * from account where accountId = ? for update";
		QueryOperator qo = PsHelper.createQueryOperator(sql);
		qo.addParameter(accountid);
		qo.access();
		return qo.getResultAsObject(Account.class);
	}

	@Override
	public int updateBalance(long balance, long accountId) throws DBOperatorException {
		String sql = "update account set balance=?,updateTime=sysdate() where accountId=?";
		UpdateOperator u = PsHelper.createUpdateOperator(sql);
		u.addParameter(balance);
		u.addParameter(accountId);
		u.access();
		return u.getEffectCounts();
	}

}