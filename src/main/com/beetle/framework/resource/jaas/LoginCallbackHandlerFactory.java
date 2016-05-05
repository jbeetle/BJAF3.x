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
package com.beetle.framework.resource.jaas;

import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.resource.container.ContainerConfig;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import java.security.PrivilegedAction;

public class LoginCallbackHandlerFactory {
	public static CallbackHandler create(String tagname, String user,
			String password, String url) {
		CallbackHandler handler;
		String pdc = ContainerConfig.getContainValue(tagname,
				"CONTAINER_PRODUCT");
		if (pdc.equalsIgnoreCase("weblogic")) {
			handler = new WebLogicLoginCallbackHandler(user, password, url);
		} else if (pdc.equalsIgnoreCase("jboss")) {
			handler = new JBossLoginCallbackHandler(user, password, url);
		} else if (pdc.equalsIgnoreCase("websphere")) {
			handler = new WebSphereLoginCallbackHandler(user, password, url);
		} else {
			throw new AppRuntimeException("sorry,not supported " + pdc
					+ " yet!");
		}
		return handler;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object subjectDoAs(String tagname, Subject sbj,
			PrivilegedAction paction) {
		Object obj = null;
		String pdc = ContainerConfig.getContainValue(tagname,
				"CONTAINER_PRODUCT");
		if (pdc.equalsIgnoreCase("weblogic")) {
			// obj = weblogic.security.Security.runAs(sbj, paction);
		} else if (pdc.equalsIgnoreCase("jboss")) {
			obj = Subject.doAs(sbj, paction);
			System.out.println("jboss not yet!");
		} else if (pdc.equalsIgnoreCase("websphere")) {
			obj = Subject.doAs(sbj, paction);
			System.out.println("websphere not yet!");
		} else {
			obj = Subject.doAs(sbj, paction);
		}
		return obj;
	}
}
