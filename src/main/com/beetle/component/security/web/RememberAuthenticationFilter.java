package com.beetle.component.security.web;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import com.beetle.component.security.dto.SecUsers;
import com.beetle.component.security.service.SecurityServiceException;
import com.beetle.component.security.service.UserService;
import com.beetle.framework.AppProperties;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.dic.DIContainer;

/**
 *
 * @author yuhaodong@gmail.com
 *
 */
public class RememberAuthenticationFilter extends FormAuthenticationFilter {
	private final UserService userService;
	private static final AppLogger logger = AppLogger.getInstance(RememberAuthenticationFilter.class);

	public RememberAuthenticationFilter() {
		super();
		this.userService = DIContainer.getInstance().retrieve(UserService.class);
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		Subject subject = this.getSubject(request, response);
		if (!subject.isAuthenticated() && subject.isRemembered()) {// 没登录，通过记住我进来的
			Session session = subject.getSession(false);
			logger.debug("session:{}", session);
			if (session == null) {
				if (AppProperties.getAsBoolean("security_login_success_create_session", true)) {
					String username = subject.getPrincipal().toString();
					SecUsers user;
					try {
						user = userService.findByUsername(username);
						logger.debug("user:{}", user);
						if (user == null) {
							throw new AuthenticationException("user not found!");
						}
						user.setPassword("");// 为了安全去掉密码
						user.setSalt("");
						subject.getSession(true).setAttribute("APP_LOGINED_USER", user);
						logger.debug("user:{},session renew OK", user.getUserId());
					} catch (SecurityServiceException e) {
						logger.error("err", e);
					}
				}
			}
		}
		// return super.isAccessAllowed(request, response, mappedValue);
		return subject.isAuthenticated() || subject.isRemembered();
	}

}
