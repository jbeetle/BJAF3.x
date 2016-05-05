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
package com.beetle.framework.util.pattern.observer;

import java.util.Observable;

/**
 * <p>
 * Title: FrameWork
 * </p>
 * <p>
 * Description: 受监控对象抽类
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: 甲壳虫软件
 * </p>
 * 
 * @author 余浩东
 * 
 * @version 1.0
 */

public abstract class ObservedObj extends Observable {

	public ObservedObj() {
	}

	/**
	 * 监控一个属性对象，一般在setter方法属性变化的时候设置
	 * 
	 * 
	 * @param observedObj
	 *            Object－－属性对象
	 */
	public void observeOneObject(Object observedObj) {
		this.setChanged();
		this.notifyObservers(observedObj);
	}

	/**
	 * 增加一个观察者
	 * 
	 * 
	 * @param oimp
	 *            ObserverImp－－观察者
	 */
	public void addOneObserver(ObserverEvent oimp) {
		this.addObserver(oimp);
	}

	/**
	 * 删除一个受控对象
	 * 
	 * 
	 * @param oimp
	 *            ObserverImp－－观察者
	 */
	public void removeOneObserver(ObserverEvent oimp) {
		this.deleteObserver(oimp);
	}

	/**
	 * 取消所有的监控，在取消之前触发受控消息
	 */
	public void cancelAllObservers() {
		this.notifyObservers();
		this.clearChanged();
		this.deleteObservers();
	}

}
