package com.beetle.component.security.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import com.beetle.component.security.dto.SecUsers;
import com.beetle.component.security.service.SecurityServiceException;
import com.beetle.component.security.service.UserService;
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.dic.DIContainer;

public class UserRealm extends AuthorizingRealm {
	private final UserService userService;
	private static final AppLogger logger = AppLogger.getInstance(UserRealm.class);

	public UserRealm() {
		super();
		this.userService = DIContainer.getInstance().retrieve(UserService.class);
	}

	// n. 授权，认可；批准，委任
	// 权限验证
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		String username = (String) principals.getPrimaryPrincipal();
		SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
		try {
			authorizationInfo.setRoles(userService.findRoles(username));
			authorizationInfo.setStringPermissions(userService.findPermissions(username));
			if (logger.isDebugEnabled())
				logger.debug("username{},authorizationInfo{}", username, authorizationInfo);
			return authorizationInfo;
		} catch (SecurityServiceException e) {
			logger.error(e);
			throw new AppRuntimeException("doGetAuthorizationInfo err", e);
		}
	}

	// n. 证明；鉴定；证实
	// 登陆验证
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String username = (String) token.getPrincipal();
		SecUsers user = null;
		try {
			user = userService.findByUsername(username);
		} catch (SecurityServiceException e) {
			throw new AuthenticationException(e);
		}
		if (user == null) {
			throw new UnknownAccountException();// 没找到帐号
		}
		if (user.getLocked() == 1) {
			throw new LockedAccountException(); // 帐号锁定
		}
		// 交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getUsername(), // 用户名
				user.getPassword(), // 密码
				ByteSource.Util.bytes(user.getCredentialsSalt()), // salt=username+salt
				getName() // realm name
		);
		return authenticationInfo;
	}

}
