package com.beetle.component.accounting.dto;

public class TallyRequest implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 业务交易订单号 */
	private String orderNo;
	/** 付款方账号 */
	private String payerAccountNo;

	/** 收款方账号 */
	private String payeeAccountNo;

	/** 交易金额（单位：分） */
	private Long amount;


	/** 付款方会员编号. */
	private String payerMemberNo;

	/**
	 * 付款方账户密码 
	 */
	private String payerAccountPassword;

	/** 是否校验密码. */
	private boolean checkPassword;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getPayerAccountNo() {
		return payerAccountNo;
	}

	public void setPayerAccountNo(String payerAccountNo) {
		this.payerAccountNo = payerAccountNo;
	}

	public String getPayeeAccountNo() {
		return payeeAccountNo;
	}

	public void setPayeeAccountNo(String payeeAccountNo) {
		this.payeeAccountNo = payeeAccountNo;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getPayerMemberNo() {
		return payerMemberNo;
	}

	public void setPayerMemberNo(String payerMemberNo) {
		this.payerMemberNo = payerMemberNo;
	}

	public String getPayerAccountPassword() {
		return payerAccountPassword;
	}

	public void setPayerAccountPassword(String payerAccountPassword) {
		this.payerAccountPassword = payerAccountPassword;
	}

	public boolean isCheckPassword() {
		return checkPassword;
	}

	public void setCheckPassword(boolean checkPassword) {
		this.checkPassword = checkPassword;
	}

	@Override
	public String toString() {
		return "TallyRequest [orderNo=" + orderNo + ", payerAccountNo=" + payerAccountNo + ", payeeAccountNo="
				+ payeeAccountNo + ", amount=" + amount + ", payerMemberNo=" + payerMemberNo + ", payerAccountPassword="
				+ payerAccountPassword + ", checkPassword=" + checkPassword + "]";
	}
	
}
