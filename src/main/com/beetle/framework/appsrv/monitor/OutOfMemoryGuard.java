package com.beetle.framework.appsrv.monitor;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 内存泄漏守卫<br>
 * 监控进程内部内存泄漏的异常情况，发现问题后，进程自杀<br>
 * 进程重新启动依赖于外部监控程序，如:watchdog<br>
 * 参数说明：<br>
 * allocateNum为每次检查尝试分配的内存空间大小，单位字节，可选参数，默认1024*512个字节<br>
 * checkRate为每次检查的频率，单位毫秒，可选参数，默认1000*25毫秒检查一次<br>
 * timeRange为出OM异常后连续检查的时间（范围），单位是毫秒，可选参数，默认1000*25*10毫秒，一般设置为checkRate整数倍<br>
 * debugOn打开调试信息参数，默认为false，为true是在控制台打印相关调试信息<br>
 * 
 */
public class OutOfMemoryGuard {
	private volatile static OutOfMemoryGuard instance;

	public static OutOfMemoryGuard getInstance() {
		if (instance == null) {
			instance = new OutOfMemoryGuard();
		}
		return instance;
	}

	public void setCheckRate(int checkRate) {
		this.checkRate = checkRate;
	}

	public void setAllocateNum(int allocateNum) {
		this.allocateNum = allocateNum;
	}

	public void setTimeRange(int timeRange) {
		this.timeRange = timeRange;
	}

	private static String dateFormat(java.util.Date date, String formatStr) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				formatStr);
		return sdf.format(date);
	}

	private OutOfMemoryGuard() {
		try {
			fw = new FileWriter("outOfMemoryGuardt.log", true);
			String pid_ = ManagementFactory.getRuntimeMXBean().getName();
			String pid = pid_.split("@")[0];
			boolean win_flag = isWindowsOS();
			if (win_flag) {
				this.killPidCmd = "tskill " + pid;
			} else {
				this.killPidCmd = "kill -9 " + pid;
			}
			System.out.println("pid:" + pid);
			selfKillLongInfo = "The process["
					+ pid
					+ "] which started at "
					+ dateFormat(
							new java.util.Date(System.currentTimeMillis()),
							"yyyy-MM-dd HH:mm:ss")
					+ " was killed by OutOfMemoryGuard.\n";
		} catch (IOException e) {
			throw new RuntimeException("OutOfMemoryGuard log file create err",
					e);
		}
	}

	public void setDebugOn(boolean debugOn) {
		this.debugOn = debugOn;
	}

	private static boolean isWindowsOS() {
		String osname = System.getProperty("os.name").toLowerCase().trim();
		if (osname.startsWith("win")) {
			return true;
		} else {
			return false;
		}
	}

	private void killSelf() {
		Process pro = null;
		try {
			logAtDie();
			System.out.println(killPidCmd);
			pro = Runtime.getRuntime().exec(killPidCmd);
			Thread.sleep(3000);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (pro != null) {
				pro.destroy();
			}
			System.exit(-1);// O(∩_∩)O~
		}
	}

	private void debugInfo(String info) {
		if (debugOn) {
			System.out.println(info);
		}
	}

	private void debugInfo(int info) {
		if (debugOn) {
			System.out.println(info);
		}
	}

	public void startMonitor() {
		size = timeRange / checkRate;
		omMark = new ArrayList<Long>(size);
		Thread guard = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						sleep();
						byte[] bb = new byte[allocateNum];
						debugInfo(" memory allocated[" + bb.length + "]OK");
						bb = null;
					} catch (java.lang.OutOfMemoryError err) {
						debugInfo(err.getMessage());
						debugInfo(omMark.size());
						if (!err.getMessage().equals(HEAP_OUT)) {// 其它两种om异常即死
							killSelf();
						}
						if (omMark.size() < size) {
							omMark.add(System.currentTimeMillis());
							int lastPos = omMark.size() - 1;
							if (lastPos >= 1) {
								long newfirst = omMark.get(lastPos - 1);
								long newlast = omMark.get(lastPos);
								long x = newlast - newfirst;
								if (x >= timeRange) {
									omMark.clear();
								}
							}
						} else {
							omMark.clear();
							killSelf();
						}
					} catch (Throwable t) {
						t.printStackTrace();
						debugInfo(t.getMessage());
					}
				}
			}
		});
		guard.setName("outOfMemoryGuard_monitor");
		guard.setDaemon(true);
		guard.start();
		System.out.println("outOfMemoryGuard started!");
	}

	// 预先创建好相关对象，以免OM时没有空间创建
	private final FileWriter fw;
	private final String selfKillLongInfo;
	private int checkRate = 1000 * 25;
	private volatile int allocateNum = 1024 * 512;// 默认每次分配512K测试
	private int timeRange = 1000 * 25 * 10;// 大约4分钟,相当于默认连续检查10次
	private ArrayList<Long> omMark;
	private int size;
	private static final String HEAP_OUT = "Java heap space";
	private final String killPidCmd;
	private boolean debugOn = false;

	private void sleep() {
		try {
			Thread.sleep(checkRate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void premain(String agentArgs, Instrumentation instrumentation) {
		OutOfMemoryGuard guard = OutOfMemoryGuard.getInstance();
		if (agentArgs != null) {
			StringTokenizer st = new StringTokenizer(agentArgs, ",");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				String key = token.substring(0, token.indexOf("="));
				String value = token.substring(token.indexOf("=") + 1,
						token.length());
				System.out.println("agent Arg: Key [" + key + "] Value ["
						+ value + "]");
				if (key.equalsIgnoreCase("allocateNum")) {
					guard.setAllocateNum(Integer.parseInt(value.trim()));
				} else if (key.equalsIgnoreCase("checkRate")) {
					guard.setCheckRate(Integer.parseInt(value.trim()));
				} else if (key.equalsIgnoreCase("timeRange")) {
					guard.setTimeRange(Integer.parseInt(value.trim()));
				} else if (key.equalsIgnoreCase("debugOn")) {
					guard.setDebugOn(Boolean.valueOf(value.trim()));
				}
			}
		}
		guard.startMonitor();
	}

	private void logAtDie() {
		try {
			debugInfo(selfKillLongInfo);
			fw.append(selfKillLongInfo);
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

	}

}
