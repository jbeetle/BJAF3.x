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
package com.beetle.framework.business.common.ejb;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import java.rmi.RemoteException;
import java.security.Principal;
public class SessionEJBImp implements SessionBean {
	private static final long serialVersionUID = -19760224l;
	
	private SessionContext sessionContext;

	public SessionEJBImp() {
	}

	public void ejbCreate() throws CreateException {
		// System.out.println("new:-->"+System.currentTimeMillis());
		// this.principal = this.getSessionContext().getCallerPrincipal();
	}

	public Principal getPrincipal() {
		Principal p = this.getSessionContext().getCallerPrincipal();
		//System.out.println("------->>>z");
		//System.out.println(p);
		// System.out.println(p instanceof java.util.HashMap);
		// System.out.println("------->>>");
		boolean f = p instanceof java.io.Serializable;
		if (!f) {
			return null;//��ô�棿��
		}
		return p;
	}

	public void setSessionContext(SessionContext sessionContext)
			throws EJBException, RemoteException {
		this.sessionContext = sessionContext;
	}

	/**
	 * ��������ع�

	 */
	public void setRollbackOnly() {
		try {
			getSessionContext().setRollbackOnly();
		} catch (final IllegalStateException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * ����SessionContext
	 * 
	 * @return SessionContext
	 */
	public SessionContext getSessionContext() {
		return sessionContext;
	}

	public void ejbRemove() throws EJBException, RemoteException {
	}

	public void ejbActivate() throws EJBException, RemoteException {
	}

	public void ejbPassivate() throws EJBException, RemoteException {
	}

}
