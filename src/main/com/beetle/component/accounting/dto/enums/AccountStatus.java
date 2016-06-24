package com.beetle.component.accounting.dto.enums;

/**
 * @author yuhaodong@gmail.com<br>
 *账务状态；1-正常；2-冻结；3-销户
 */
public enum AccountStatus {
	NORMAL(1), FREEZED(2), CANCELED(3);
	public int toInteger() {
		return value;
	}

	private int value;

	private AccountStatus(int value) {
		this.value = value;
	}
}
