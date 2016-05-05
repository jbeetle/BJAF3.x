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
package com.beetle.framework.util;

import com.beetle.framework.AppRuntimeException;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OtherUtil {

	private static Random rand = new Random(System.currentTimeMillis());
	private static String localHostname = "";

	/**
	 * 删除文件名中的路径，只返回文件名
	 * 
	 * @param filename
	 * @return
	 */
	public final static String removePath(String filename) {
		int i = filename.lastIndexOf('/');
		if (i == -1) {
			return filename;
		} else {
			return filename.substring(i + 1);
		}
	}

	/**
	 * 文件最后一次被修改的时间
	 * 
	 * @param filename
	 * @return 0-表示文件不存在或无权限读取，大于0为修改时间
	 */
	public static long getFileLastModified(String filename) {
		long l = 0;
		File f = new File(filename);
		if (f.exists()) {
			try {
				l = f.lastModified();
			} catch (SecurityException se) {
				l = 0;
				se.printStackTrace();
			}
		}
		return l;
	}

	/**
	 * 获取本机名称
	 * 
	 * @return
	 */
	public static String getLocalHostName() {
		if (localHostname.equals("")) {
			try {
				localHostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				localHostname = "UnknownHost";
			}
		}
		return localHostname;
	}

	/**
	 * 获取本地所有的ip地址，组成字符串返回（以char=2分隔）
	 * 
	 * @return
	 */
	public static String getLocalHostIps() {
		StringBuffer sb = new StringBuffer();
		final char flag = 2;
		try {
			Enumeration<?> netInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) netInterfaces
						.nextElement();
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					InetAddress inetAddress = ips.nextElement();
					String ip = inetAddress.getHostAddress();
					if (!inetAddress.isLoopbackAddress()
							&& ip.indexOf(":") == -1) {
						sb.append(ip).append(flag);
					}
				}
			}
		} catch (Exception e) {
			return "";
		}
		return sb.toString();
	}

	public static Random getRandom() {
		return rand;
		// return new Random(System.currentTimeMillis());
	}

	/**
	 * Generates pseudo-random long from specific range. Generated number is
	 * great or equals to min parameter value and less then max parameter value.
	 * 
	 * @param min
	 *            lower (inclusive) boundary
	 * @param max
	 *            higher (exclusive) boundary
	 * @return pseudo-random value
	 */

	public static long randomLong(long min, long max) {
		return min + (long) (Math.random() * (max - min));
	}

	/**
	 * 清空数组
	 * 
	 * @param arg
	 */
	public static void clearArray(Object arg[]) {
		if (arg == null) {
			return;
		}
		for (int i = 0; i < arg.length; i++) {
			arg[i] = null;
		}
		// arg = null;
	}

	/**
	 * Generates pseudo-random integer from specific range. Generated number is
	 * great or equals to min parameter value and less then max parameter value.
	 * 
	 * @param min
	 *            lower (inclusive) boundary
	 * @param max
	 *            higher (exclusive) boundary
	 * @return pseudo-random value
	 */
	public static int randomInt(int min, int max) {
		return min + (int) (Math.random() * (max - min));
	}

	/**
	 * 中断线程一段时间，可代替sleep，采取object.wait实现
	 * 
	 * @param lockObj
	 *            加锁对象
	 * @param sometime
	 *            ，单位毫秒
	 * @throws InterruptedException
	 */
	public static final void blockSomeTime(final Object lockObj, long sometime)
			throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		synchronized (lockObj) {
			long waitTime = sometime;
			long start = System.currentTimeMillis();
			try {
				for (;;) {
					lockObj.wait(waitTime);
					waitTime = sometime - (System.currentTimeMillis() - start);
					if (waitTime <= 0) {
						break;
					}
				}
			} catch (InterruptedException ex) {
				lockObj.notify();
				throw ex;
			}
		}
	}

	public static String strBase64Encode(String str) {
		Base64 b64 = new Base64();
		byte[] b = b64.encode(str.getBytes());
		return new String(b);
	}

	public static String strBase64Decode(String base64EncodeStr) {
		Base64 b64 = new Base64();
		byte[] b = b64.decode(base64EncodeStr.getBytes());
		return new String(b);
	}

	public static final String md5AndBase64Encode(String str) {
		byte[] b = md5(str);
		if (b == null) {
			throw new AppRuntimeException("md5 encode err!");
		}
		Base64 b64 = new Base64();
		b = b64.encode(b);
		return new String(b);
	}

	public static final byte[] md5(String str) {

		java.security.MessageDigest digest;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
			return digest.digest(str.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 检查ip地址的合法性
	 * 
	 * @param ip
	 * @return
	 */
	public final static boolean checkip(String ip) {
		Pattern patt = Pattern
				.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
		Matcher mat = patt.matcher(ip);
		return mat.matches();
	}

	/**
	 * 获取一个dns或计算机名称所对应的ip地址数组
	 * 
	 * @param dnsip
	 * @return
	 */
	public final static String[] getDnsIPs(String dnsip) {
		String ips[];
		try {
			InetAddress ias[] = InetAddress.getAllByName(dnsip);
			ips = new String[ias.length];
			for (int i = 0; i < ias.length; i++) {
				ips[i] = ias[i].getHostAddress();
				ias[i] = null;
			}
		} catch (UnknownHostException e) {
			ips = null;
		}
		return ips;
	}

	private static final int NANO_TO_MILL = 1000000;

	/**
	 * 获取当前的物理毫秒时间
	 * 
	 * @return
	 */
	public final static long getCurrentTime() {
		return System.nanoTime() / NANO_TO_MILL;
	}
}
