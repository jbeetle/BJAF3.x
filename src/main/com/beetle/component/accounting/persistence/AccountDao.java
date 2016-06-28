package com.beetle.component.accounting.persistence;

import com.beetle.component.accounting.dto.Account;
import com.beetle.framework.persistence.access.operator.DBOperatorException;

public interface AccountDao {
	Account get(Long accountid) throws DBOperatorException;

	Account get(String accountNo) throws DBOperatorException;

	/**
	 * 获取账户并加锁，只有事务结束的时候才释放，调用服务需要在一个事务内进行
	 * 
	 * @param accountid
	 * @return
	 * @throws DBOperatorException
	 */
	Account getAndLock(Long accountid) throws DBOperatorException;

	int insert(Account account) throws DBOperatorException;

	int update(Account account) throws DBOperatorException;

	int updateBalance(long balance, long accountId) throws DBOperatorException;

	int delete(Long accountid) throws DBOperatorException;

}
