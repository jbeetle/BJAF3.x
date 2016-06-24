package com.beetle.component.accounting.service;

import com.beetle.component.accounting.dto.Account;
import com.beetle.component.accounting.dto.TallyRequest;
import com.beetle.component.accounting.dto.TallyResponse;

public interface AccountService {
	/**
	 * 开户
	 * 
	 * @param account
	 * @return 返回开户后数据库存储的完整的数据记录对象
	 * @throws AccountingServiceException
	 */
	Account openAccount(Account account) throws AccountingServiceException;
	/**
	 * 记账
	 * @param tallyRequest
	 * @return
	 * @throws AccountingServiceException
	 */
	TallyResponse tally(TallyRequest tallyRequest) throws AccountingServiceException;
}
