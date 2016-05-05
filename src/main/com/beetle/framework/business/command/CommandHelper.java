/*
 * BJAF - Beetle J2EE Application Framework
 * 甲壳虫J2EE企业应用开发框架
 * 版权所有2003-2015 余浩东 (www.beetlesoft.net)
 * 
 * 这是一个免费开源的软件，您必须在
 *<http://www.apache.org/licenses/LICENSE-2.0>
 *协议下合法使用、修改或重新发布。
 *
 * 感谢您使用、推广本框架，若有建议或问题，欢迎您和我联系。
 * 邮件： <yuhaodong@gmail.com/>.
 */
package com.beetle.framework.business.command;

/**
 * <p>Title: Beetleҵ���߼����</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: �׿ǳ����</p>
 *
 * @author ��ƶ���yuhaodong@gmail.com��
 * @version 1.0
 */
import com.beetle.framework.resource.define.Constant;
import com.beetle.framework.resource.jta.ITransaction;
import com.beetle.framework.resource.watch.WatchHelper;
import com.beetle.framework.resource.watch.WatchInfo;

public class CommandHelper {
	private final static int ROLLBACK_CODE = 20090521;

	public static void bind(ITransaction trans) {
		if (!WatchHelper.isNeedWatch()) {
			return;
		}
		WatchInfo wi = new WatchInfo();
		wi.addResource(Constant.BUSINESS_CMD_TRANS, trans,
				Constant.COMMON_WATCHINFO_OBJECT_TYPE_TRANS);
		WatchHelper.bind(wi);
	}

	public static void bind() {
		if (!WatchHelper.isNeedWatch()) {
			return;
		}
		WatchInfo wi = new WatchInfo();
		WatchHelper.bind(wi);
	}

	public static void setRollbackFlag() {
		WatchInfo wi = WatchHelper.currentWatch();
		if (wi != null) {
			wi.setStatus(ROLLBACK_CODE);
		}
	}

	public static boolean isNeedToRollback() {
		WatchInfo wi = WatchHelper.currentWatch();
		if (wi != null) {
			int f = wi.getStatus();
			if (f == ROLLBACK_CODE) {
				return true;
			}
		}
		return false;
	}

	// synchronized
	public static void unbind() {
		if (!WatchHelper.isNeedWatch()) {
			return;
		}
		WatchHelper.unbind();
	}
}
