package com.beetle.component.accounting.dto;

public class TallyResponse implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long drAccountId;
	private Long drAccountWaterId;
	private Long crAccountId;
	private Long crAccountWaterId;

	public TallyResponse() {
		super();
	}

	public Long getDrAccountId() {
		return drAccountId;
	}

	public void setDrAccountId(Long drAccountId) {
		this.drAccountId = drAccountId;
	}

	public Long getDrAccountWaterId() {
		return drAccountWaterId;
	}

	public void setDrAccountWaterId(Long drAccountWaterId) {
		this.drAccountWaterId = drAccountWaterId;
	}

	public Long getCrAccountId() {
		return crAccountId;
	}

	public void setCrAccountId(Long crAccountId) {
		this.crAccountId = crAccountId;
	}

	public Long getCrAccountWaterId() {
		return crAccountWaterId;
	}

	public void setCrAccountWaterId(Long crAccountWaterId) {
		this.crAccountWaterId = crAccountWaterId;
	}

	@Override
	public String toString() {
		return "TallyResponse [drAccountId=" + drAccountId + ", drAccountWaterId=" + drAccountWaterId + ", crAccountId="
				+ crAccountId + ", crAccountWaterId=" + crAccountWaterId + "]";
	}

}
