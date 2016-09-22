package com.beetle.component.security.web;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

import com.beetle.framework.log.AppLogger;

public class ServletPathPermissionsFilter extends PermissionsAuthorizationFilter {
	private static final AppLogger logger = AppLogger.getInstance(ServletPathPermissionsFilter.class);

	@Override
	public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws IOException {
		logger.debug("mappedValue:{}", mappedValue);
		boolean f = super.isAccessAllowed(request, response, mappedValue);
		if (!f) {// 兼容原来的权限字符比对模式，匹配不上了，再采取比对路径的方式
			HttpServletRequest req = (HttpServletRequest) request;
			String path = req.getServletPath();
			logger.debug("map path[{}] mode:", path);
			String[] perms = new String[1];
			perms[0] = path;
			f = super.isAccessAllowed(request, response, perms);
			logger.debug("map path result:{}", f);
		}
		return f;
	}

}
