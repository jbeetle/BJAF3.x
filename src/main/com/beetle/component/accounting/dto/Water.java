package com.beetle.component.accounting.dto;

public class Water implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Long accountid;
	private Long amount;
	private String orderno;
	private String subjectno;
	private String directflag;
	private java.sql.Timestamp createtime;
	private String accountno;
	private Long forebalance;
	private Long aftbalance;
	private Long waterid;

	public Water() {
	}

	public Long getAccountId() {
		return this.accountid;
	}

	public Long getAmount() {
		return this.amount;
	}

	public String getOrderNo() {
		return this.orderno;
	}

	public String getSubjectNo() {
		return this.subjectno;
	}

	public String getDirectFlag() {
		return this.directflag;
	}

	public java.sql.Timestamp getCreateTime() {
		return this.createtime;
	}

	public String getAccountNo() {
		return this.accountno;
	}

	public Long getForeBalance() {
		return this.forebalance;
	}

	public Long getAftBalance() {
		return this.aftbalance;
	}

	public Long getWaterId() {
		return this.waterid;
	}

	public void setAccountId(Long accountid) {
		this.accountid = accountid;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public void setOrderNo(String orderno) {
		this.orderno = orderno;
	}

	public void setSubjectNo(String subjectno) {
		this.subjectno = subjectno;
	}

	public void setDirectFlag(String directflag) {
		this.directflag = directflag;
	}

	public void setCreateTime(java.sql.Timestamp createtime) {
		this.createtime = createtime;
	}

	public void setAccountNo(String accountno) {
		this.accountno = accountno;
	}

	public void setForeBalance(Long forebalance) {
		this.forebalance = forebalance;
	}

	public void setAftBalance(Long aftbalance) {
		this.aftbalance = aftbalance;
	}

	public void setWaterId(Long waterid) {
		this.waterid = waterid;
	}

	@Override
	public String toString() {
		return "Water [accountid=" + accountid + ", amount=" + amount + ", orderno=" + orderno + ", subjectno="
				+ subjectno + ", directflag=" + directflag + ", createtime=" + createtime + ", accountno=" + accountno
				+ ", forebalance=" + forebalance + ", aftbalance=" + aftbalance + ", waterid=" + waterid + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountid == null) ? 0 : accountid.hashCode());
		result = prime * result + ((accountno == null) ? 0 : accountno.hashCode());
		result = prime * result + ((aftbalance == null) ? 0 : aftbalance.hashCode());
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((createtime == null) ? 0 : createtime.hashCode());
		result = prime * result + ((directflag == null) ? 0 : directflag.hashCode());
		result = prime * result + ((forebalance == null) ? 0 : forebalance.hashCode());
		result = prime * result + ((orderno == null) ? 0 : orderno.hashCode());
		result = prime * result + ((subjectno == null) ? 0 : subjectno.hashCode());
		result = prime * result + ((waterid == null) ? 0 : waterid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Water other = (Water) obj;
		if (accountid == null) {
			if (other.accountid != null)
				return false;
		} else if (!accountid.equals(other.accountid))
			return false;
		if (accountno == null) {
			if (other.accountno != null)
				return false;
		} else if (!accountno.equals(other.accountno))
			return false;
		if (aftbalance == null) {
			if (other.aftbalance != null)
				return false;
		} else if (!aftbalance.equals(other.aftbalance))
			return false;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (createtime == null) {
			if (other.createtime != null)
				return false;
		} else if (!createtime.equals(other.createtime))
			return false;
		if (directflag == null) {
			if (other.directflag != null)
				return false;
		} else if (!directflag.equals(other.directflag))
			return false;
		if (forebalance == null) {
			if (other.forebalance != null)
				return false;
		} else if (!forebalance.equals(other.forebalance))
			return false;
		if (orderno == null) {
			if (other.orderno != null)
				return false;
		} else if (!orderno.equals(other.orderno))
			return false;
		if (subjectno == null) {
			if (other.subjectno != null)
				return false;
		} else if (!subjectno.equals(other.subjectno))
			return false;
		if (waterid == null) {
			if (other.waterid != null)
				return false;
		} else if (!waterid.equals(other.waterid))
			return false;
		return true;
	}

}