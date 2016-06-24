package com.beetle.component.accounting.dto.enums;

/**
 * @author yuhaodong@gmail.com <br>
 *         1-ASSETS资产<br>
 *         2-LIABILITY负债<br>
 *         3-EQUITY所有者权益类<br>
 *         4-Cost成本类<br>
 *         5-损益类 Profit and loss<br>
 *         6-OUTFORM表外
 */
public enum SubjectType {
	ASSETS(1), LIABILITY(2), EQUITY(3), COST(4), PROFIT_AND_LOSS(5), OUTFORM(6);
	public int toInteger() {
		return value;
	}

	private int value;

	private SubjectType(int value) {
		this.value = value;
	}
}
