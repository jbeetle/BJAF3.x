package com.beetle.framework.business.common.tst;

import com.beetle.framework.business.command.CommandHelper;

public class ServiceTransactionRollback {
	private ServiceTransactionRollback() {

	}

	public static void rollbackByHand() {
		CommandHelper.setRollbackFlag();
	}
}
