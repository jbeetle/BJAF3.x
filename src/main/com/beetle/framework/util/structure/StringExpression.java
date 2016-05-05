package com.beetle.framework.util.structure;

/**
 * 简单字符表达式 ${variable}<br>
 * eg: I am ${name},and sex is ${sex}. <br>
 * name=Henry,sex=M <br>
 * result:I am Henry,and sex is M.
 */
public class StringExpression {
	public StringExpression(String expression) {
		this.result = expression;
	}

	private final static String pre = "\\$\\{";
	private final static String suf = "\\}";
	private String result;

	/**
	 * 获取结果
	 * 
	 * @return
	 */
	public String getResult() {
		return result;
	}

	/**
	 * 设置变量值
	 * 
	 * @param key
	 *            --变量名称
	 * @param value
	 *            --变量值
	 */
	public void set(String key, String value) {
		String namePattern = pre + key + suf;
		result = result.replaceAll(namePattern, value);
	}

}
