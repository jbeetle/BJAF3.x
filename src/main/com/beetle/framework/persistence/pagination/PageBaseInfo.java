package com.beetle.framework.persistence.pagination;

public class PageBaseInfo {
	private int pageAmount;
	private int recordAmount;
	private int pageSize;

	public int getPageAmount() {
		return pageAmount;
	}

	public void setPageAmount(int pageAmount) {
		this.pageAmount = pageAmount;
	}

	public int getRecordAmount() {
		return recordAmount;
	}

	public void setRecordAmount(int recordAmount) {
		this.recordAmount = recordAmount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
