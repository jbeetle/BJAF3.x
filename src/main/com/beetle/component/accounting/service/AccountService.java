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
	 * 记账<br>
	 * 注：不参与调用方使用，每次使用新数据库连接启动新事物<br>
	 * 使用框架公共线程池，并发性能通过application.properties文件[resource]相关参数调优
	 * @param tallyRequest
	 * @return
	 * @throws AccountingServiceException
	 */
	TallyResponse tally(TallyRequest tallyRequest) throws AccountingServiceException;
}
