package com.beetle.component.accounting.dto;

public class TallyRequest implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 业务交易订单号 */
	private String orderNo;
	/** DR(如：付款方)账号 */
	private String drAccountNo;

	/** CR(如：收款方)账号 */
	private String crAccountNo;

	/** 交易金额（单位：分） */
	private Long amount;

	/**
	 * DR(如：付款方)账户密码
	 */
	private String drAccountPassword;
	/**
	 * DR方密码校验
	 */
	private boolean drPasswordCheck;

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getDrAccountNo() {
		return drAccountNo;
	}

	public void setDrAccountNo(String drAccountNo) {
		this.drAccountNo = drAccountNo;
	}

	public String getCrAccountNo() {
		return crAccountNo;
	}

	public void setCrAccountNo(String crAccountNo) {
		this.crAccountNo = crAccountNo;
	}

	public String getDrAccountPassword() {
		return drAccountPassword;
	}

	public void setDrAccountPassword(String drAccountPassword) {
		this.drAccountPassword = drAccountPassword;
	}

	public boolean isDrPasswordCheck() {
		return drPasswordCheck;
	}

	public void setDrPasswordCheck(boolean drPasswordCheck) {
		this.drPasswordCheck = drPasswordCheck;
	}

	@Override
	public String toString() {
		return "TallyRequest [orderNo=" + orderNo + ", drAccountNo=" + drAccountNo + ", crAccountNo=" + crAccountNo
				+ ", amount=" + amount + ", drPasswordCheck=" + drPasswordCheck + "]";
	}


}
