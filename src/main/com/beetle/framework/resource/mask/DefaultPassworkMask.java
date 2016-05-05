package com.beetle.framework.resource.mask;

import com.beetle.framework.util.encrypt.AesEncrypt;

public class DefaultPassworkMask implements IPasswordMask {

	@Override
	public String decode(String mask) {
		return AesEncrypt.decrypt(mask);
	}

	@Override
	public String encode(String src) {
		return AesEncrypt.encrypt(src);
	}

}
