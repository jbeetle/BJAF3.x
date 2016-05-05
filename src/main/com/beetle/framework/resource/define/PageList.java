package com.beetle.framework.resource.define;

import java.util.ArrayList;

public class PageList<T> extends ArrayList<T> {

	public PageList() {
		super();
	}

	public PageList(int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	public String toString() {
		return "PageList [recordAmount=" + recordAmount + ", curPageSize="
				+ curPageSize + ", curPageNumber=" + curPageNumber
				+ ", nextPageNumber=" + nextPageNumber + ", prePageNumber="
				+ prePageNumber + ", pageAmount=" + pageAmount + ", curPos="
				+ curPos + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + curPageNumber;
		result = prime * result + curPageSize;
		result = prime * result + curPos;
		result = prime * result + nextPageNumber;
		result = prime * result + pageAmount;
		result = prime * result + prePageNumber;
		result = prime * result + recordAmount;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		PageList other = (PageList) obj;
		if (curPageNumber != other.curPageNumber)
			return false;
		if (curPageSize != other.curPageSize)
			return false;
		if (curPos != other.curPos)
			return false;
		if (nextPageNumber != other.nextPageNumber)
			return false;
		if (pageAmount != other.pageAmount)
			return false;
		if (prePageNumber != other.prePageNumber)
			return false;
		if (recordAmount != other.recordAmount)
			return false;
		return true;
	}

	private static final long serialVersionUID = 8215605110760184261L;

	private int recordAmount;

	private int curPageSize;

	private int curPageNumber;

	private int nextPageNumber;

	private int prePageNumber;

	private int pageAmount;

	private int curPos;
	private int pageSize;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurPageNumber() {
		return curPageNumber;
	}

	public void setCurPageNumber(int curPageNumber) {
		this.curPageNumber = curPageNumber;
	}

	public int getCurPos() {
		return curPos;
	}

	public void setCurPos(int curPos) {
		this.curPos = curPos;
	}

	public int getNextPageNumber() {
		return nextPageNumber;
	}

	public void setNextPageNumber(int nextPageNumber) {
		this.nextPageNumber = nextPageNumber;
	}

	public int getPageAmount() {
		return pageAmount;
	}

	public void setPageAmount(int pageAmount) {
		this.pageAmount = pageAmount;
	}

	public int getPrePageNumber() {
		return prePageNumber;
	}

	public void setPrePageNumber(int prePageNumber) {
		this.prePageNumber = prePageNumber;
	}

	public int getCurPageSize() {
		return curPageSize;
	}

	public void setCurPageSize(int curPageSize) {
		this.curPageSize = curPageSize;
	}

	public int getRecordAmount() {
		return recordAmount;
	}

	public void setRecordAmount(int recordAmount) {
		this.recordAmount = recordAmount;
	}
}
