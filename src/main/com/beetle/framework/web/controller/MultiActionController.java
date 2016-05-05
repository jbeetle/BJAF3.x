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
package com.beetle.framework.web.controller;

import com.beetle.framework.web.controller.ControllerHelper.MethodEx;
import com.beetle.framework.web.view.View;

/**
 * <pre>
 * 动作控制器，即可以用一个控制器处理多个页面提交的动作。
 * 每一个动作对应一个自定义的方法。方法定义满足以下原则：
 * 1--在符合java规范条件下，随便定义。 
 * 2--方法的输入参数只能是WebInput，返回类型必须是View
 * 3--方法必须抛出ControllerException异常
 * 4--方法必须使用public修饰
 * eg:View xxxAction(WebInput webInput)	throws ControllerException
 * 注意：在页面提交表单中，必须用'$action'关键字指定具体的方法名称。
 * eg:&lt;INPUT TYPE=&quot;hidden&quot; NAME=&quot;$action&quot; value=&quot;xxxAction&quot;&gt;
 * 若'$action'不设置，则会执行defaultAction方法
 * </pre>
 */
public abstract class MultiActionController extends ControllerImp {

	final public View perform(WebInput webInput) throws ControllerException {
		String actionName = webInput.getParameter("$action");
		if (actionName == null || actionName.length() == 0) {
			// throw new ControllerException("must set '$action' value!");
			return defaultAction(webInput);
		}
		MethodEx method = ControllerHelper.getActionMethod(
				webInput.getControllerName(), actionName, this, WebInput.class);
		try {
			View view = (View) method.getMethod().invoke(this, webInput);
			return view;
		} catch (Exception e) {
			throw new ControllerException(e);
		}
	}

	/**
	 * 默认执行动作（若$action没有设置，则会执行此方法）
	 * 
	 * @param webInput
	 * @return
	 * @throws ControllerException
	 */
	public abstract View defaultAction(WebInput webInput)
			throws ControllerException;
}
