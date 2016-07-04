package com.beetle.framework.util.encrypt;

import org.apache.commons.codec.binary.Base64;

import com.beetle.framework.util.OtherUtil;

public class Coder {

	public static final byte[] decryptBASE64(String key) throws Exception {
		Base64 b64 = new Base64();
		byte[] b = b64.decode(key.getBytes());
		return b;
	}

	public static final String md5(String key) {
		byte[] bb = OtherUtil.md5(key);
		if (bb == null)
			return null;
		return bytesToHexString2(bb);
	}

	public static final String encryptBASE64(byte[] key) throws Exception {
		Base64 b64 = new Base64();
		byte[] b = b64.encode(key);
		return new String(b);
	}

	/**
	 * byte数组转换成十六进制字符串
	 * 
	 * @param byte[]
	 * @return HexString
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	static final String bytesToHexString2(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp);
		}
		return sb.toString();
	}

	/**
	 * 把16进制字符串转换成字节数组
	 * 
	 * @param hexString
	 * @return byte[]
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static int toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
}
