package com.beetle.component.accounting.dto.enums;

/**
 * @author yuhaodong@gmail.com
 *'是否需要支付密码验证；1-需要；0-不需求',
 */
public enum PasswordCheck {
	NOT_NEED(0), NEED(1);
	public int toInteger() {
		return value;
	}

	private int value;

	private PasswordCheck(int value) {
		this.value = value;
	}
}
