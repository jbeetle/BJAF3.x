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
package com.beetle.framework.util.encrypt;

/**
 * <p>字符加密类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: beetle</p>
 *
 * @author henryyu
 * @version 1.0
 */

import com.beetle.framework.AppProperties;
import com.beetle.framework.util.OtherUtil;
import com.beetle.framework.util.ResourceLoader;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;

public final class AesEncrypt {
	private static Cipher cipher = null;
	private static SecretKeySpec skeySpec = null;
	private final static String sysconfigFileName = AppProperties.getAppHome()
			+ "BeetleACE.key";
	private final static char f = 2;
	private final static Object lock = new Object();

	private synchronized static void loadKey() {
		if (skeySpec == null) {
			File f = new File(sysconfigFileName);
			if (f.exists()) {
				fromFile();
			} else {
				fromPag();
			}
		}
	}

	private static void fromPag() {
		InputStream is = null;
		ObjectInputStream ois = null;
		try {
			cipher = Cipher.getInstance("AES");
			is = ResourceLoader
					.getResAsStream("com/beetle/framework/util/encrypt/AesEncryptKey.properties");
			ois = new ObjectInputStream(is);
			skeySpec = (SecretKeySpec) ois.readObject();
			// System.out.println(skeySpec);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void fromFile() {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			cipher = Cipher.getInstance("AES");
			fis = new FileInputStream(sysconfigFileName);
			ois = new ObjectInputStream(fis);
			skeySpec = (SecretKeySpec) ois.readObject();
			// System.out.println(skeySpec);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Turns array of bytes into string
	 */
	private static String asHex(byte buf[]) {
		StringBuilder strbuf = new StringBuilder(buf.length * 2);
		for (byte aBuf : buf) {
			if (((int) aBuf & 0xff) < 0x10) {
				strbuf.append("0");
			}

			strbuf.append(Long.toString((int) aBuf & 0xff, 16));
		}
		return strbuf.toString();
	}

	/**
	 * Turns string into array of bytes
	 */
	private static byte[] asByte(String buf) {
		if (buf.length() % 2 == 1) {
			buf = "0" + buf;
		}
		int length = buf.length() / 2;
		byte[] bytebuf = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			String b = buf.substring(pos, pos + 2);
			bytebuf[i] = (byte) Integer.parseInt(b, 16);
		}
		return bytebuf;
	}

	/**
	 * 加密字符串
	 * 
	 * @param pwd
	 *            --明文
	 * @return--密文（最少生成64位大小字符串，明文21位以内都是生成64，大于21，则相应增大）
	 */
	public static String encrypt(String pwd) {
		if (skeySpec == null) {
			loadKey();
		}
		pwd = OtherUtil.strBase64Encode(pwd);
		synchronized (lock) {
			try {
				int lg = pwd.length();
				if (lg < 16) {
					int c = 16 - lg;
					for (int i = 0; i < c; i++) {
						pwd = pwd + f;
					}
				}
				cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
				byte[] encrypted = cipher.doFinal(pwd.getBytes());
				return asHex(encrypted);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * 检验密码是否相等，密文密码不会解开，保证密码在内存中不会被监听泄露
	 * 
	 * @param pwd
	 *            --输入的密码，明文
	 * @param encodedPwd
	 *            --要比较的密码（以加密的密文）
	 * @return
	 */
	public static boolean verify(String pwd, String encodedPwd) {
		String pw = encrypt(pwd);
		return pw.equals(encodedPwd);
	}

	/**
	 * 解密字符串
	 * 
	 * @param pwd
	 *            --密文
	 * @return--明文
	 */
	public static String decrypt(String pwd) {
		if (skeySpec == null) {
			loadKey();
		}
		synchronized (lock) {
			try {
				byte[] encrypted = asByte(pwd);
				cipher.init(Cipher.DECRYPT_MODE, skeySpec);
				byte[] original = cipher.doFinal(encrypted);
				String r = new String(original);
				int lg = r.length();
				if (lg == 16) {
					int i = r.indexOf(f);
					if (i > 0) {
						r = r.substring(0, i);
					}
				}
				r = OtherUtil.strBase64Decode(r);
				return r;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static void genKeyFile() throws Exception {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(128);
		java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(
				new java.io.FileOutputStream("BeetleACE.key"));
		out.writeObject(keyGen.generateKey());
		out.close();

	}

}
