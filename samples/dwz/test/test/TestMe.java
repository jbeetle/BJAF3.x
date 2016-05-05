package test;

import com.beetle.framework.util.encrypt.AesEncrypt;

public class TestMe {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String a = AesEncrypt.encrypt("760224");
		System.out.println(a);
	}

}
