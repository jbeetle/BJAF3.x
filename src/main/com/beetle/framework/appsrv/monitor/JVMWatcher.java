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
package com.beetle.framework.appsrv.monitor;

import com.beetle.framework.AppProperties;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.util.thread.ThreadImp;

/**
 * 内存监控器，防止服务器由于内存过低而瘫痪
 * 
 * 
 * @author HenryYu
 * 
 */
public class JVMWatcher extends ThreadImp {

	public JVMWatcher(int interval) {
		super("AppMemoryWatcher", interval);
		init(interval);
	}

	private float gcRate;
	private AppLogger logger;
	private int counter;
	private int gcTime;
	private boolean gcFlag;

	private void init(int interval) {
		// this.gcRate =
		// Float.parseFloat(ResourceReader.getParameter("mem-rate"));
		this.gcRate = AppProperties.getAsFloat("routinespool_MEM_RATE", 0.8f);
		this.logger = AppLogger.getInstance(JVMWatcher.class);
		this.counter = 0;
		int gcInterval = 1000 * 60 * 10;
		this.gcTime = gcInterval / interval;
		this.gcFlag = false;
	}

	protected void routine() {
		float memused = memoryUsedRate();
		if (memused >= gcRate) {
			if (!gcFlag) {
				gcFlag = true;
				counter = 0;
				Runtime.getRuntime().gc();
				if (logger.isInfoEnabled()) {
					logger.info("system jvm called gc()");
				}
			}
		}
		// logger.debug("gcRate=" + gcRate + ",memoryUsedRate=" + memused);
		counter++;
		if (counter >= gcTime) {
			counter = 0;
			gcFlag = false;
		}
	}

	private static float memoryUsedRate() {
		long free = Runtime.getRuntime().freeMemory();
		long total = Runtime.getRuntime().totalMemory();
		return (float) (total - free) / (float) total;
	}

	protected void end() {// 只有调用stopNow方法才触发

		logger.info("[AppMemoryWatcher]" + "stopped.");
	}

}
