package com.beetle.framework.resource.jta;

import com.beetle.framework.resource.define.Constant;
import com.beetle.framework.resource.watch.WatchHelper;
import com.beetle.framework.resource.watch.WatchInfo;

public class MockTransaction implements ITransaction {
	private MockTransaction() {
	}

	private static MockTransaction instance = new MockTransaction();

	public static MockTransaction getInstance() {
		return instance;
	}

	@Override
	public void begin() throws JTAException {
	}

	@Override
	public void commit() throws JTAException {
		WatchInfo wi = WatchHelper.currentWatch();
		if (wi == null)
			return;
		ITransaction t = (ITransaction) wi
				.getResourceByName(Constant.BUSINESS_CMD_TRANS);
		if (t != null && !(t instanceof MockTransaction)) {
			t.commit();
		}
	}

	@Override
	public void rollback() throws JTAException {
		WatchInfo wi = WatchHelper.currentWatch();
		if (wi == null)
			return;
		ITransaction t = (ITransaction) wi
				.getResourceByName(Constant.BUSINESS_CMD_TRANS);
		if (t != null && !(t instanceof MockTransaction)) {
			t.rollback();
		}
	}

	@Override
	public void setRollbackOnly() throws JTAException {
	}

	@Override
	public int getStatus() throws JTAException {
		return 0;
	}

	@Override
	public void setTransactionTimeout(int timeout) throws JTAException {
	}

}
