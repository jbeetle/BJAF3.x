package com.beetle.framework.util.encrypt;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.util.file.FileUtil;

public class RSAEncrypt {
	private static final String KEY_ALGORITHM = "RSA";
	//private static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	// private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
	// 公钥
	private String publicKey = "";
	// 私钥
	private String privateKey = "";

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public void setPublicKeyFromFile(String publicKeyFileName) throws IOException {
		String x = FileUtil.readString(publicKeyFileName);
		publicKey = x;
	}

	public void setPrivateKeyFromFile(String privateKeyFileName) throws IOException {
		String x = FileUtil.readString(privateKeyFileName);
		privateKey = x;
	}

	/**
	 * 生成一对公私Key，以文件输出
	 * 
	 * @return
	 * @throws Exception
	 */
	public static void generateKeys(int bits) {
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
			keyPairGen.initialize(bits);
			KeyPair keyPair = keyPairGen.generateKeyPair();
			// 公钥
			RSAPublicKey pubk = (RSAPublicKey) keyPair.getPublic();
			String pkStr = Coder.encryptBASE64(pubk.getEncoded());
			FileUtil.writeString("rsa" + bits + "public.key", pkStr);
			// 私钥
			RSAPrivateKey prik = (RSAPrivateKey) keyPair.getPrivate();
			String prvStr = Coder.encryptBASE64(prik.getEncoded());
			FileUtil.writeString("rsa" + bits + "private.key", prvStr);
			System.out.println("generateKeys OK");
		} catch (Exception e) {
			throw new AppRuntimeException(e);
		}
	}

	public static String encryptByPublicKey(String key, String data) {
		try {
			byte[] bs = RSAUtils.encryptByPublicKey(data.getBytes("utf-8"), key);
			return Coder.encryptBASE64(bs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 公钥加密
	 * 
	 * @param data
	 * @param key
	 * @return 如果加密失败返回null
	 */
	public String encryptByPublicKey(String data) {
		return encryptByPublicKey(this.publicKey, data);
	}

	/**
	 * 公钥解密
	 * 
	 * @param data
	 * @param key
	 * @return 如果解码失败返回null
	 */
	public String decryptByPublicKey(String data) {
		try {
			byte[] encryptedData = Coder.decryptBASE64(data);
			byte[] decryptedData = RSAUtils.decryptByPublicKey(encryptedData, this.publicKey);
			return new String(decryptedData, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 私钥加密
	 * 
	 * @param data
	 * @param key
	 * @return 如果加密失败返回null
	 *
	 */
	public String encryptByPrivateKey(String data) {
		try {
			byte[] encrypData = RSAUtils.encryptByPrivateKey(data.getBytes("utf-8"), this.privateKey);
			return Coder.encryptBASE64(encrypData);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 私钥解密
	 * 
	 * @param data
	 * @return 如果解码失败返回null
	 */
	public String decryptByPrivateKey(String data) {
		return decryptByPrivateKey(data, this.privateKey);
	}

	/**
	 * 私钥解密
	 * 
	 * @param data
	 * @param privateKey
	 * @return 如果解码失败返回null
	 */
	public static String decryptByPrivateKey(String data, String privateKey) {
		try {
			byte[] encryptedData = Coder.decryptBASE64(data);
			byte[] decryptedData = RSAUtils.decryptByPrivateKey(encryptedData, privateKey);
			return new String(decryptedData, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 用私钥对信息生成数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param privateKey
	 *            私钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		return RSAUtils.sign(data, privateKey);
	}

	/**
	 * 校验数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param publicKey
	 *            公钥
	 * @param sign
	 *            数字签名
	 * 
	 * @return 校验成功返回true 失败返回false
	 * @throws Exception
	 * 
	 */
	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
		return RSAUtils.verify(data, publicKey, sign);
	}

	private RSAEncrypt() {

	}

	private static final RSAEncrypt rsa = new RSAEncrypt();

	public static RSAEncrypt getInstance() {
		return rsa;
	}
}
