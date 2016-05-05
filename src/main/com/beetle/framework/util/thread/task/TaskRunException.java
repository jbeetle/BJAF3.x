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
package com.beetle.framework.util.thread.task;
import com.beetle.framework.AppRuntimeException;
public class TaskRunException extends AppRuntimeException {

	public TaskRunException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public TaskRunException(Throwable arg0) {
		super(arg0);
	}

	public TaskRunException(String arg0) {
		super(arg0);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
