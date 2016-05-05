package com.beetle.framework.business.service.common;

public class RpcConst {
	public final static int ERR_CODE_SERVER_SERVICE_INVOKE_EXCEPTION = -10001;
	public final static int ERR_CODE_SERVER_SERVICE_NOTFOUND_EXCEPTION = -10002;
	public final static int ERR_CODE_SERVER_SERVICE_NEW_INSTANCE_EXCEPTION = -10004;
	public final static int ERR_CODE_SERVER_CHANNEL_EXCEPTION = -10003;
	public final static int ERR_CODE_CLIENT_INVOKE_TIMEOUT_EXCEPTION = -10004;
	public final static int ERR_CODE_CONN_EXCEPTION = -2001;
	public final static int ERR_CODE_REMOTE_CALL_EXCEPTION = -2002;
	public final static int ERR_CODE_HOST_DIE_EXCEPTION = -2003;
	public final static long CLIENT_HOST_CHECK_TIME = 1000 * 30;
}
