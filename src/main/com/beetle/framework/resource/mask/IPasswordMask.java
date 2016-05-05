package com.beetle.framework.resource.mask;

public interface IPasswordMask {
	String decode(String src);

	String encode(String mask);
}
