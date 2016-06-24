package com.beetle.component.accounting.dto;

public class Account implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String subjectno;
	private String accountname;
	private Integer accounttype;
	private java.sql.Timestamp updatetime;
	private Integer accountstatus;
	private Long accountid;
	private String memberno;
	private String password;
	private Long balance;
	private java.sql.Timestamp createtime;
	private String accountno;
	private Integer passwordcheck;
	private String subjectdirect;

	public Account() {
	}

	public String getSubjectNo() {
		return this.subjectno;
	}

	public String getAccountName() {
		return this.accountname;
	}

	public Integer getAccountType() {
		return this.accounttype;
	}

	public java.sql.Timestamp getUpdateTime() {
		return this.updatetime;
	}

	public Integer getAccountStatus() {
		return this.accountstatus;
	}

	public Long getAccountId() {
		return this.accountid;
	}

	public String getMemberNo() {
		return this.memberno;
	}

	public String getPassword() {
		return this.password;
	}

	public Long getBalance() {
		return this.balance;
	}

	public java.sql.Timestamp getCreateTime() {
		return this.createtime;
	}

	public String getAccountNo() {
		return this.accountno;
	}

	public Integer getPasswordCheck() {
		return this.passwordcheck;
	}

	public String getSubjectDirect() {
		return this.subjectdirect;
	}

	public void setSubjectNo(String subjectno) {
		this.subjectno = subjectno;
	}

	public void setAccountName(String accountname) {
		this.accountname = accountname;
	}

	public void setAccountType(Integer accounttype) {
		this.accounttype = accounttype;
	}

	public void setUpdateTime(java.sql.Timestamp updatetime) {
		this.updatetime = updatetime;
	}

	public void setAccountStatus(Integer accountstatus) {
		this.accountstatus = accountstatus;
	}

	public void setAccountId(Long accountid) {
		this.accountid = accountid;
	}

	public void setMemberNo(String memberno) {
		this.memberno = memberno;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public void setCreateTime(java.sql.Timestamp createtime) {
		this.createtime = createtime;
	}

	public void setAccountNo(String accountno) {
		this.accountno = accountno;
	}

	public void setPasswordCheck(Integer passwordcheck) {
		this.passwordcheck = passwordcheck;
	}

	public void setSubjectDirect(String subjectdirect) {
		this.subjectdirect = subjectdirect;
	}

	@Override
	public String toString() {
		return "Account [subjectno=" + subjectno + ", accountname=" + accountname + ", accounttype=" + accounttype
				+ ", updatetime=" + updatetime + ", accountstatus=" + accountstatus + ", accountid=" + accountid
				+ ", memberno=" + memberno + ", password=" + password + ", balance=" + balance + ", createtime="
				+ createtime + ", accountno=" + accountno + ", passwordcheck=" + passwordcheck + ", subjectdirect="
				+ subjectdirect + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountid == null) ? 0 : accountid.hashCode());
		result = prime * result + ((accountname == null) ? 0 : accountname.hashCode());
		result = prime * result + ((accountno == null) ? 0 : accountno.hashCode());
		result = prime * result + ((accountstatus == null) ? 0 : accountstatus.hashCode());
		result = prime * result + ((accounttype == null) ? 0 : accounttype.hashCode());
		result = prime * result + ((balance == null) ? 0 : balance.hashCode());
		result = prime * result + ((createtime == null) ? 0 : createtime.hashCode());
		result = prime * result + ((memberno == null) ? 0 : memberno.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((passwordcheck == null) ? 0 : passwordcheck.hashCode());
		result = prime * result + ((subjectdirect == null) ? 0 : subjectdirect.hashCode());
		result = prime * result + ((subjectno == null) ? 0 : subjectno.hashCode());
		result = prime * result + ((updatetime == null) ? 0 : updatetime.hashCode());
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
		Account other = (Account) obj;
		if (accountid == null) {
			if (other.accountid != null)
				return false;
		} else if (!accountid.equals(other.accountid))
			return false;
		if (accountname == null) {
			if (other.accountname != null)
				return false;
		} else if (!accountname.equals(other.accountname))
			return false;
		if (accountno == null) {
			if (other.accountno != null)
				return false;
		} else if (!accountno.equals(other.accountno))
			return false;
		if (accountstatus == null) {
			if (other.accountstatus != null)
				return false;
		} else if (!accountstatus.equals(other.accountstatus))
			return false;
		if (accounttype == null) {
			if (other.accounttype != null)
				return false;
		} else if (!accounttype.equals(other.accounttype))
			return false;
		if (balance == null) {
			if (other.balance != null)
				return false;
		} else if (!balance.equals(other.balance))
			return false;
		if (createtime == null) {
			if (other.createtime != null)
				return false;
		} else if (!createtime.equals(other.createtime))
			return false;
		if (memberno == null) {
			if (other.memberno != null)
				return false;
		} else if (!memberno.equals(other.memberno))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (passwordcheck == null) {
			if (other.passwordcheck != null)
				return false;
		} else if (!passwordcheck.equals(other.passwordcheck))
			return false;
		if (subjectdirect == null) {
			if (other.subjectdirect != null)
				return false;
		} else if (!subjectdirect.equals(other.subjectdirect))
			return false;
		if (subjectno == null) {
			if (other.subjectno != null)
				return false;
		} else if (!subjectno.equals(other.subjectno))
			return false;
		if (updatetime == null) {
			if (other.updatetime != null)
				return false;
		} else if (!updatetime.equals(other.updatetime))
			return false;
		return true;
	}

}