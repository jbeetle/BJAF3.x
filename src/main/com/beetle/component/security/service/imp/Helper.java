package com.beetle.component.security.service.imp;

import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import com.beetle.component.security.dto.SecUsers;
import com.beetle.framework.AppProperties;
import com.beetle.framework.AppRuntimeException;

public class Helper {
	private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

	public static final String algorithmName = "md5";
	public static final int hashIterations = 3;
	private final static String Base64Format = "Base64Format";
	private final static String HexFormat = "HexFormat";
	private final String format;

	public Helper() {
		super();
		this.format = AppProperties.get("security_password_hash_format", Base64Format);
	}

	public void encryptPassword(SecUsers user) {
		final String newPassword;
		if (format.equalsIgnoreCase(HexFormat)) {
			user.setSalt(randomNumberGenerator.nextBytes().toHex());
			newPassword = new SimpleHash(algorithmName, user.getPassword(),
					ByteSource.Util.bytes(user.getCredentialsSalt()), hashIterations).toHex();
		} else if (format.equalsIgnoreCase(Base64Format)) {
			user.setSalt(randomNumberGenerator.nextBytes().toBase64());
			newPassword = new SimpleHash(algorithmName, user.getPassword(),
					ByteSource.Util.bytes(user.getCredentialsSalt()), hashIterations).toBase64();
		} else {
			throw new AppRuntimeException("not support this format[" + format + "]");
		}
		user.setPassword(newPassword);
	}

	public void encryptPasswordForOld(SecUsers user) {
		final String newPassword;
		if (format.equalsIgnoreCase(HexFormat)) {
			// user.setSalt(randomNumberGenerator.nextBytes().toHex());
			newPassword = new SimpleHash(algorithmName, user.getPassword(),
					ByteSource.Util.bytes(user.getCredentialsSalt()), hashIterations).toHex();
		} else if (format.equalsIgnoreCase(Base64Format)) {
			// user.setSalt(randomNumberGenerator.nextBytes().toBase64());
			newPassword = new SimpleHash(algorithmName, user.getPassword(),
					ByteSource.Util.bytes(user.getCredentialsSalt()), hashIterations).toBase64();
		} else {
			throw new AppRuntimeException("not support this format[" + format + "]");
		}
		user.setPassword(newPassword);
	}
}
