package com.beetle.component.accounting.service.imp;

import java.sql.Timestamp;
import java.util.UUID;

import org.slf4j.Logger;

import com.beetle.component.accounting.dto.Account;
import com.beetle.component.accounting.dto.TallyRequest;
import com.beetle.component.accounting.dto.TallyResponse;
import com.beetle.component.accounting.dto.Water;
import com.beetle.component.accounting.dto.enums.AccountStatus;
import com.beetle.component.accounting.dto.enums.DirectFlag;
import com.beetle.component.accounting.dto.enums.PasswordCheck;
import com.beetle.component.accounting.persistence.AccountDao;
import com.beetle.component.accounting.persistence.WaterDao;
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
	@InjectField
	private WaterDao waterDao;

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

	private static class UR {
		private long payerForeBalance;
		private long payerAftBalance;
		private long payeeForeBalance;
		private long payeeAftBalance;

		public UR(long payerForeBalance, long payerAftBalance, long payeeForeBalance, long payeeAftBalance) {
			super();
			this.payerForeBalance = payerForeBalance;
			this.payerAftBalance = payerAftBalance;
			this.payeeForeBalance = payeeForeBalance;
			this.payeeAftBalance = payeeAftBalance;
		}

		public long getPayerForeBalance() {
			return payerForeBalance;
		}

		public long getPayerAftBalance() {
			return payerAftBalance;
		}

		public long getPayeeForeBalance() {
			return payeeForeBalance;
		}

		public long getPayeeAftBalance() {
			return payeeAftBalance;
		}

		@Override
		public String toString() {
			return "UR [payerForeBalance=" + payerForeBalance + ", payerAftBalance=" + payerAftBalance
					+ ", payeeForeBalance=" + payeeForeBalance + ", payeeAftBalance=" + payeeAftBalance + "]";
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
		logger.info("validate done");
		// update account balance
		UR ur = updateAccountBalance(req, payerAccId, payeeAccId, payeeAcc, payerAcc);
		logger.info("updateAccountBalance done");
		// log water
		// 借方记账流水
		Water drWater = new Water();
		drWater.setAccountId(payerAccId);
		drWater.setAccountNo(req.getDrAccountNo());
		drWater.setAftBalance(ur.getPayerAftBalance());
		drWater.setAmount(req.getAmount());
		drWater.setDirectFlag(DirectFlag.DR.toString());
		drWater.setForeBalance(ur.getPayerForeBalance());
		drWater.setOrderNo(req.getOrderNo());
		drWater.setSubjectNo(payerAcc.getSubjectNo());
		long drWaterId = waterDao.insert(drWater);
		logger.debug("drWaterId:{}", drWaterId);
		// 贷方记账流水
		Water crWater = new Water();
		crWater.setAccountId(payeeAccId);
		crWater.setAccountNo(req.getCrAccountNo());
		crWater.setAftBalance(ur.getPayeeAftBalance());
		crWater.setAmount(req.getAmount());
		crWater.setDirectFlag(DirectFlag.CR.toString());
		crWater.setForeBalance(ur.getPayeeForeBalance());
		crWater.setOrderNo(req.getOrderNo());
		crWater.setSubjectNo(payeeAcc.getSubjectNo());
		long crWaterId = waterDao.insert(crWater);
		logger.debug("crWaterId:{}", crWaterId);
		logger.info("insertWater done");
		// 构建返回对象
		TallyResponse res = new TallyResponse();
		res.setCrAccountId(payeeAccId);
		res.setCrAccountWaterId(crWaterId);
		res.setDrAccountId(payerAccId);
		res.setDrAccountWaterId(drWaterId);
		logger.info("tally-end[{}]", res);
		return res;
	}

	private UR updateAccountBalance(TallyRequest req, long payerAccId, long payeeAccId, Account payeeAcc,
			Account payerAcc) throws AccountingServiceException {
		// 更新付款方账户余额
		long payerForeBalance = payerAcc.getBalance();
		long payerAftBalance = calculateBalance(payerAcc, req.getAmount(), DirectFlag.DR);
		if (payerAftBalance < 0) {
			throw new AccountingServiceException(-20009, "DR账户[" + req.getDrAccountNo() + "]余额不足");
		}
		int rowx = accDao.updateBalance(payerAftBalance, payerAccId);
		if (rowx != 1) {
			throw new AccountingServiceException(-20010, "DR账户[" + req.getDrAccountNo() + "]无法更新余额，可能账户不存在，请联系管理员");
		}
		//// 更新收款方余额
		long payeeForeBalance = payeeAcc.getBalance();
		long payeeAftBalance = calculateBalance(payeeAcc, req.getAmount(), DirectFlag.CR);
		if (payeeAftBalance < 0) {
			throw new AccountingServiceException(-20011,
					"CR账户[" + req.getCrAccountNo() + "]计算后余额为负数，终止交易，请检测业务记账规则是否有误");
		}
		int rowy = accDao.updateBalance(payeeAftBalance, payeeAccId);
		if (rowy != 1) {
			throw new AccountingServiceException(-20012, "CR账户[" + req.getCrAccountNo() + "]无法更新余额，可能账户不存在，请联系管理员");
		}
		UR ur = new UR(payerForeBalance, payerAftBalance, payeeForeBalance, payeeAftBalance);
		return ur;
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
			payerAcc = accDao.get(req.getDrAccountNo());
			if (payerAcc == null) {
				throw new AccountingServiceException(-20004, "DR账户[" + req.getDrAccountNo() + "]不存在");
			}
			if (payerAcc.getAccountStatus() != AccountStatus.NORMAL.toInteger()) {
				throw new AccountingServiceException(-20005, "DR账户[" + req.getDrAccountNo() + "]状态不正常，禁止交易");
			}
			if (req.isDrPasswordCheck()) {
				if (payerAcc.getPasswordCheck() == PasswordCheck.NEED.toInteger()) {
					if (req.getDrAccountPassword() == null || req.getDrAccountPassword().trim().length() == 0) {
						throw new AccountingServiceException(-20002, "DR账户需要验证交易密码但密码为空");
					}
					if (!req.getDrAccountPassword().equals(payerAcc.getPassword())) {
						throw new AccountingServiceException(-20008, "DR账户交易密码验证不通过");
					}
				}
			}
			payeeAcc = accDao.get(req.getCrAccountNo());
			if (payeeAcc == null) {
				throw new AccountingServiceException(-20006, "CR账户[" + req.getCrAccountNo() + "]不存在");
			}
			if (payeeAcc.getAccountStatus() != AccountStatus.NORMAL.toInteger()) {
				throw new AccountingServiceException(-20007, "CR账户[" + req.getCrAccountNo() + "]状态不正常，禁止交易");
			}
			return new VR(payerAcc.getAccountId(), payeeAcc.getAccountId());
		} catch (DBOperatorException e) {
			throw new AccountingServiceException(-10001, "数据库操作异常", e);
		}
	}

}
