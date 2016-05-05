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
package com.beetle.framework.util.pattern.cor;

import com.beetle.framework.AppRuntimeException;

import java.util.List;

/**
 * <p>
 * Title: Chain of Responsibility
 * </p>
 * 
 * <p>
 * Description: 管理节点处理对象的链接列表节点，运行并返回结果

 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * 
 * <p>
 * Company: 甲壳虫软件

 * </p>
 * 
 * @author 余浩东

 * @version 1.0
 */
public class ChainNode {
	private List<?> chain; 

	private NodeHandler handler;

	/**
	 * ChainLinkNode
	 * 
	 * @param s
	 *            NodeHandle 策略（链接工作）对象
	 */
	public ChainNode(NodeHandler handle) {
		this.handler = handle;
		// chain.add(this);
	}

	private ChainNode next() {
		// Where this link is in the chain:
		int location = chain.indexOf(this);
		if (!end()) {
			return (ChainNode) chain.get(location + 1);
		}
		// Indicates a programming error (thus
		// doesn't need to be a checked exception):
		throw new AppRuntimeException("end of chain");
	}

	private boolean end() {
		int location = chain.indexOf(this);
		return location + 1 >= chain.size();
	}

	HandleResult execute(IReq req) { // 迭代
		HandleResult r = handler.handle(req);
		if (r.isSuccessful() || end()) {
			return r;
		}
		return next().execute(req);
	}

	void setChain(List<?> chain) {
		this.chain = chain;
	}

}
