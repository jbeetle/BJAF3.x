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
package com.beetle.framework.resource.watch;

/**
 * <p>Title: Beetle业务逻辑框架</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 甲壳虫软件</p>
 *
 * @author 余浩东（yuhaodong@gmail.com）

 * @version 1.0
 */

import com.beetle.framework.AppProperties;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.watch.WatchInfo.VO;
import com.beetle.framework.util.cache.ConcurrentCache;
import com.beetle.framework.util.cache.ICache;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

public class WatchHelper {
	private final static ICache cache = new ConcurrentCache(1334);// StrongCache
	private static boolean startFlag = false;
	private static boolean needFlag = false;
	// private static int checktime = 2000;
	private static AppLogger logger = AppLogger.getInstance(WatchHelper.class);
	static {
		// String s = ResourceReader.getResStr("resource_BUSINESS_WATCH");
		String s = AppProperties.get("resource_BUSINESS_WATCH", "1");
		if (s.trim().equals("1")) {
			needFlag = true;
			// checktime = Integer.parseInt(ResourceReader.getResStr(
			// "resource_BUSINESS_WATCH_CHECK_TIME"));
		}
	}

	public static boolean isNeedWatch() {
		return needFlag;
	}

	public static synchronized void startWatch() {
		if (!isNeedWatch()) {
			return;
		}
		if (!startFlag) {
			/*
			 * Context ctx = AppContext.getFrameworkContext(); try {
			 * ctx.bind(WatchInfo.WATCH_INFO_KEY, WatchInfo.DO_WATCH); } catch
			 * (NamingException ex) { } Timer timer = new Timer(true);
			 * timer.schedule(new WatchTask(), 5000, checktime); startFlag =
			 * true; logger.info("startWatch ok!");
			 */
		}
	}

	public static void bind(WatchInfo wi) {
		cache.put(Thread.currentThread(), wi);
		if (logger.isDebugEnabled()) {
			logger.debug("bind WatchInfo:{}", Thread.currentThread());
		}
	}

	/**
	 * 随机获取一个有效的监控信息对象
	 * 
	 * @return WatchInfo
	 */
	public static WatchInfo currentWatch() {
		WatchInfo wi = (WatchInfo) cache.get(Thread.currentThread());
		return wi;
	}

	public static void unbind() {
		WatchInfo wi = (WatchInfo) cache.get(Thread.currentThread());
		if (wi != null) {
			Map<String, VO> m = wi.getResoucrces();
			Iterator<VO> it = m.values().iterator();
			while (it.hasNext()) {
				VO vo = it.next();
				if (vo.getObjType() == 1) {
					Connection conn = (Connection) vo.getResObj();
					try {
						if (!conn.isClosed()) {
							conn.close();
							if (logger.isDebugEnabled()) {
								logger.debug("close connection:{} OK", conn);
							}
						}
					} catch (SQLException ex) {
						logger.error(ex.getMessage(), ex);
					} finally {
						conn = null;
					}
				}
			}
			// ...others
			wi.clearResoucrces();
			wi = null;
			cache.remove(Thread.currentThread());
			if (logger.isDebugEnabled()) {
				logger.debug("clear WatchInfo:{}", Thread.currentThread());
			}
		}
	}
}
