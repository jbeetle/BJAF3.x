package com.beetle.framework.util.encrypt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.util.file.FileUtil;

public class RSAEncrypt {
	private static final String KEY_ALGORITHM = "RSA";
	private static final String SIGNATURE_ALGORITHM = "MD5withRSA";
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
		try {// 对公钥解密
			byte[] keyBytes = Coder.decryptBASE64(key);

			// 取得公钥
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key publicKey = keyFactory.generatePublic(x509KeySpec);

			// 对数据加密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			return Coder.encryptBASE64(cipher.doFinal(data.getBytes("utf-8")));
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
		// 对密钥解密
		try {
			byte[] keyBytes = Coder.decryptBASE64(this.publicKey);

			// 取得公钥
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key publicKey = keyFactory.generatePublic(x509KeySpec);

			// 对数据解密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, publicKey);

			byte[] encryptedData = Coder.decryptBASE64(data);

			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > 128) {
					cache = cipher.doFinal(encryptedData, offSet, 128);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * 128;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();

			return new String(decryptedData, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("公钥解密异常" + e);
		}
		return null;
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

			// 对密钥解密
			byte[] keyBytes = Coder.decryptBASE64(this.privateKey);

			// 取得私钥
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

			// 对数据加密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);

			return Coder.encryptBASE64(cipher.doFinal(data.getBytes("utf-8")));
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("私钥加密异常" + e);
		}
		return null;
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
			// 对密钥解密
			byte[] keyBytes = Coder.decryptBASE64(privateKey);

			// 取得私钥
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key privateKeyx = keyFactory.generatePrivate(pkcs8KeySpec);

			// 对数据解密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKeyx);

			byte[] encryptedData = Coder.decryptBASE64(data);

			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > 128) {
					cache = cipher.doFinal(encryptedData, offSet, 128);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * 128;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();

			return new String(decryptedData, "utf-8");

		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("私钥解密异常" + e);
		}
		return null;
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
		// 解密由base64编码的私钥
		byte[] keyBytes = Coder.decryptBASE64(privateKey);

		// 构造PKCS8EncodedKeySpec对象
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取私钥匙对象
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 用私钥对信息生成数字签名
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data);

		return Coder.encryptBASE64(signature.sign());
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

		// 解密由base64编码的公钥
		byte[] keyBytes = Coder.decryptBASE64(publicKey);

		// 构造X509EncodedKeySpec对象
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取公钥匙对象
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(pubKey);
		signature.update(data);

		// 验证签名是否正常
		return signature.verify(Coder.decryptBASE64(sign));
	}

	private RSAEncrypt() {

	}

	private static final RSAEncrypt rsa = new RSAEncrypt();

	public static RSAEncrypt getInstance() {
		return rsa;
	}
}
