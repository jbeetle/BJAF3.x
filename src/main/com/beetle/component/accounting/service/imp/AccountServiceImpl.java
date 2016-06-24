package com.beetle.component.accounting.service.imp;

import java.sql.Timestamp;
import java.util.UUID;

import org.slf4j.Logger;

import com.beetle.component.accounting.dto.Account;
import com.beetle.component.accounting.dto.TallyRequest;
import com.beetle.component.accounting.dto.TallyResponse;
import com.beetle.component.accounting.dto.enums.AccountStatus;
import com.beetle.component.accounting.persistence.AccountDao;
import com.beetle.component.accounting.service.AccountService;
import com.beetle.component.accounting.service.AccountingServiceException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.resource.dic.def.InjectField;

public class AccountServiceImpl implements AccountService {
	private Logger logger = AppLogger.getLogger(AccountServiceImpl.class);
	@InjectField
	private AccountDao accDao;

	@Override
	public Account openAccount(Account account) throws AccountingServiceException {
		String accountNo = UUID.randomUUID().toString().replace("-", "").toUpperCase();
		account.setAccountNo(accountNo);
		account.setCreateTime(null);// use db time
		account.setUpdateTime(null);// use db time
		account.setBalance(0l);// 开户余额为0，任何余额的增减都是通过交易体现
		account.setAccountStatus(AccountStatus.NORMAL.toInteger());
		account.setCreateTime(new Timestamp(System.currentTimeMillis()));
		account.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		logger.debug("account-fore:{}", account);
		try {
			accDao.insert(account);
			account = accDao.get(accountNo);
			logger.debug("account-aft:{}", account);
			return account;
		} catch (DBOperatorException e) {
			throw new AccountingServiceException(e);
		}
	}

	@Override
	public TallyResponse tally(TallyRequest tallyRequest) throws AccountingServiceException {
		TallyResponse res=new TallyResponse();
		
		return res;
	}

}
