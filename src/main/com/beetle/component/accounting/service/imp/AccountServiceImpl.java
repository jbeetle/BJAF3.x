package com.beetle.component.accounting.service.imp;

import java.sql.Timestamp;
import java.util.UUID;

import org.slf4j.Logger;

import com.beetle.component.accounting.dto.Account;
import com.beetle.component.accounting.dto.TallyRequest;
import com.beetle.component.accounting.dto.TallyResponse;
import com.beetle.component.accounting.dto.enums.AccountStatus;
import com.beetle.component.accounting.dto.enums.DirectFlag;
import com.beetle.component.accounting.dto.enums.PasswordCheck;
import com.beetle.component.accounting.persistence.AccountDao;
import com.beetle.component.accounting.service.AccountService;
import com.beetle.component.accounting.service.AccountingServiceException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.resource.dic.def.InjectField;
import com.beetle.framework.resource.dic.def.ServiceTransaction;
import com.beetle.framework.resource.dic.def.ServiceTransaction.Manner;

public class AccountServiceImpl implements AccountService {
	private Logger logger = AppLogger.getLogger(AccountServiceImpl.class);
	@InjectField
	private AccountDao accDao;

	private static class VR {
		private long payerAccId;
		private long payeeAccId;

		public VR(long payerAccId, long payeeAccId) {
			super();
			this.payerAccId = payerAccId;
			this.payeeAccId = payeeAccId;
		}

		public long getPayerAccId() {
			return payerAccId;
		}

		public long getPayeeAccId() {
			return payeeAccId;
		}

		@Override
		public String toString() {
			return "VR [payerAccId=" + payerAccId + ", payeeAccId=" + payeeAccId + "]";
		}

	}

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

	@ServiceTransaction(manner = Manner.REQUIRES_NEW)
	@Override
	public TallyResponse tally(TallyRequest req) throws AccountingServiceException {
		TallyResponse res = new TallyResponse();
		logger.info("tally-begin[{}]", req);
		// check raw
		VR vr = validate(req);
		long payerAccId = vr.getPayerAccId();
		long payeeAccId = vr.getPayeeAccId();
		logger.debug("vr:{}", vr);
		Account payeeAcc;
		Account payerAcc;
		if (payerAccId > payeeAccId) {
			payeeAcc = accDao.getAndLock(payeeAccId);
			payerAcc = accDao.getAndLock(payerAccId);
		} else {
			payerAcc = accDao.getAndLock(payerAccId);
			payeeAcc = accDao.getAndLock(payeeAccId);
		}
		// 更新付款方账户余额
		long payerForeBalance = payerAcc.getBalance();
		long payerNowBalance = calculateBalance(payerAcc, req.getAmount(), DirectFlag.DR);
		if(payerNowBalance<0){
			throw new AccountingServiceException(-20009, "付款账户[" + req.getPayerAccountNo() + "]余额不足");
		}
		logger.info("tally-end[{}]", res);
		return res;
	}

	private Long calculateBalance(Account account, Long tradeAmount, DirectFlag directFlag) {
		Long balanceAmount = 0L;
		if (directFlag.toString().equals(account.getSubjectDirect())) {
			balanceAmount = account.getBalance() + tradeAmount;
		} else {
			balanceAmount = account.getBalance() - tradeAmount;
		}
		return balanceAmount;
	}

	private VR validate(TallyRequest req) throws AccountingServiceException {
		Account payerAcc = null;
		Account payeeAcc = null;
		if (req.getAmount() == null || req.getAmount() <= 0) {
			throw new AccountingServiceException(-20001, "交易金融不能为空或小于等于零");
		}
		if (req.getOrderNo() == null || req.getOrderNo().trim().length() == 0) {
			throw new AccountingServiceException(-20003, "交易订单的支付编号不能为空");
		}
		try {
			payerAcc = accDao.get(req.getPayerAccountNo());
			if (payerAcc == null) {
				throw new AccountingServiceException(-20004, "付款账户[" + req.getPayerAccountNo() + "]不存在");
			}
			if (payerAcc.getAccountStatus() != AccountStatus.NORMAL.toInteger()) {
				throw new AccountingServiceException(-20005, "付款账户[" + req.getPayerAccountNo() + "]状态不正常，禁止交易");
			}
			if (payerAcc.getPasswordCheck() == PasswordCheck.NEED.toInteger()) {
				if (req.getPayerAccountPassword() == null || req.getPayerAccountPassword().trim().length() == 0) {
					throw new AccountingServiceException(-20002, "付款账户需要验证交易密码但密码为空");
				}
				if (!req.getPayerAccountPassword().equals(payerAcc.getPassword())) {
					throw new AccountingServiceException(-20008, "付款账户交易密码验证不通过");
				}
			}
			payeeAcc = accDao.get(req.getPayeeAccountNo());
			if (payeeAcc == null) {
				throw new AccountingServiceException(-20006, "收款账户[" + req.getPayeeAccountNo() + "]不存在");
			}
			if (payeeAcc.getAccountStatus() != AccountStatus.NORMAL.toInteger()) {
				throw new AccountingServiceException(-20007, "收款账户[" + req.getPayeeAccountNo() + "]状态不正常，禁止交易");
			}
			return new VR(payerAcc.getAccountId(), payeeAcc.getAccountId());
		} catch (DBOperatorException e) {
			throw new AccountingServiceException(-10001, "数据库操作异常", e);
		}
	}

}
