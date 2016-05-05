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

import com.beetle.framework.AppRuntimeException;

import java.util.Observable;
import java.util.Observer;

/**
 * <p>
 * Title: FrameWork
 * </p>
 * <p>
 * Description: 观察者抽类
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

public abstract class ObserverEvent implements Observer {
	public ObserverEvent() {
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof ObservedObj) {
			dealEvent((ObservedObj) o, arg);
		} else {
			throw new AppRuntimeException("o must be ObservedObj Obj!");
		}
	}

	/**
	 * 当监控对象发生变化时候，触发处理事件
	 * 
	 * @param observedObj
	 *            observedObj－－受监控的对象
	 * 
	 * @param observedObjField
	 *            Object－－受监控的对象的属性（字段）对象
	 */
	public abstract void dealEvent(ObservedObj observedObj,
			Object observedObjField);
}
