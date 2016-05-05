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

import java.util.LinkedList;
import java.util.List;

public class ChainExecutor { 
	private List<ChainNode> chain = null;

	public ChainExecutor() {
		this.chain = new LinkedList<ChainNode>();
	}

	/**
	 * 添加节点
	 * 
	 * @param node
	 */
	public void addNode(ChainNode node) {
		node.setChain(this.chain);
		this.chain.add(node);
	}

	/**
	 * 启动执行列表中ChainNode对象工作；此方法可以指定从那个ChainNode对象开始执行。

	 * 
	 * @param node
	 *            输入参数 ChainNode 对象，从这个对象开始执行

	 * @param messenger
	 *            输入参数 IReq 对象
	 * @return HandleResult
	 */
	public HandleResult start(ChainNode node, IReq req) {
		if (chain.contains(node)) {
			return node.execute(req);
		} else {
			throw new AppRuntimeException(
					"the obj not in list,check it please!");
		}
	}

	/**
	 * 启动执行列表中ChainNode对象工作，这个方法是从列表中第一个对象开始执行

	 * 
	 * @param messenger
	 *            输入参数为IReq
	 * @return 返回 HandleResult
	 */
	public HandleResult start(IReq req) {
		ChainNode link = (ChainNode) chain.get(0);
		return link.execute(req);
	}

	/**
	 * 清除list列表中所有的ChainLinkNode对象
	 */
	public void clearAll() {
		chain.clear();
	}
}
