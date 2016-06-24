package com.beetle.component.accounting.dto;

public class TallyResponse implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long payerAccountId;
	private Long payerAccountWaterId;
	private Long payeeAccountId;
	private Long payeeAccountWaterId;
	public TallyResponse() {
		super();
	}
	public Long getPayerAccountId() {
		return payerAccountId;
	}
	public void setPayerAccountId(Long payerAccountId) {
		this.payerAccountId = payerAccountId;
	}
	public Long getPayerAccountWaterId() {
		return payerAccountWaterId;
	}
	public void setPayerAccountWaterId(Long payerAccountWaterId) {
		this.payerAccountWaterId = payerAccountWaterId;
	}
	public Long getPayeeAccountId() {
		return payeeAccountId;
	}
	public void setPayeeAccountId(Long payeeAccountId) {
		this.payeeAccountId = payeeAccountId;
	}
	public Long getPayeeAccountWaterId() {
		return payeeAccountWaterId;
	}
	public void setPayeeAccountWaterId(Long payeeAccountWaterId) {
		this.payeeAccountWaterId = payeeAccountWaterId;
	}
	@Override
	public String toString() {
		return "TallyResponse [payerAccountId=" + payerAccountId + ", payerAccountWaterId=" + payerAccountWaterId
				+ ", payeeAccountId=" + payeeAccountId + ", payeeAccountWaterId=" + payeeAccountWaterId + "]";
	}
	
}
