package com.beetle.component.accounting.dto.enums;

/**
 * @author yuhaodong@gmail.com <br>
 *         MID_ACC-中间账户(特殊的科目账户)<br>
 *         SUB_ACC-科目账户<br>
 *         MEMBER_ACC-会员账户
 */
public enum AccountType {
	MID_ACC(1), SUB_ACC(2), MEMBER_ACC(3);
	public int toInteger() {
		return value;
	}

	private int value;

	private AccountType(int value) {
		this.value = value;
	}
}
