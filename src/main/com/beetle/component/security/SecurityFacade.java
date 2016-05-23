package com.beetle.component.security;

import java.io.File;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;

import com.beetle.component.security.dto.SecUsers;
import com.beetle.component.security.service.PermissionService;
import com.beetle.component.security.service.RoleService;
import com.beetle.component.security.service.UserService;
import com.beetle.framework.AppProperties;
import com.beetle.framework.log.AppLogger;
import com.beetle.framework.resource.dic.DIContainer;

/**
 * @author henryyu 操作面板 <br>
 *         初始化步骤： <br>
 *         要在application.properties文件中配置
 *         resource_DI_CONTAINER_FILES=securityBinder.xml <br>
 *         同时，要先配置好数据库脚本security.sql，当前脚本是mysql的，其他数据库可以根据脚本改造。框架代码是支持多个数据库的
 *         <br>
 *         配置DBConfig.xml文件中配置数据源 最后，application.properties文件中 <br>
 *         security_datasource属性中指定属于名称 <br>
 *         一般操作过程： <br>
 *         1、身份验证（login） <br>
 *         2、授权（hasRole/isPermitted或checkRole/checkPermission） <br>
 *         3、将相应的数据存储到会话（Session） <br>
 *         4、切换身份（RunAs）/多线程身份传播 <br>
 *         5、退出 <br>
 */
public class SecurityFacade {
	private static final AppLogger logger = AppLogger.getInstance(SecurityFacade.class);
	private static org.apache.shiro.mgt.SecurityManager sm;
	private static final PermissionService permissionService;
	private static final RoleService roleService;
	private static final UserService userService;

	public enum LoginStatus {
		Success(0), FailWithUnknownUsername(1), FailWithErrorPassowrd(2), FailWithLocked(3), FailWithLimitRetryCount(
				4), FailWithunknownReason(5);
		public int getValue() {
			return value;
		}

		private int value;

		private LoginStatus(int value) {
			this.value = value;
		}
	};

	static {

		// Ini config = new Ini();
		// config.setSectionProperty("main", "credentialsMatcher",
		// "com.beetle.component.security.credentials.RetryLimitHashedCredentialsMatcher");
		// config.setSectionProperty("main", "userRealm",
		// "com.beetle.component.security.realm.UserRealm");
		// config.setSectionProperty("main", "userRealm.credentialsMatcher",
		// "$credentialsMatcher");
		// config.setSectionProperty("main", "securityManager.realms",
		// "$userRealm");
		// // Factory<org.apache.shiro.mgt.SecurityManager> factory = new
		// // IniSecurityManagerFactory(configFile);
		// Factory<org.apache.shiro.mgt.SecurityManager> factory = new
		// IniSecurityManagerFactory(config);
		// sm = factory.getInstance();
		// SecurityUtils.setSecurityManager(sm);
		permissionService = DIContainer.getInstance().retrieve(PermissionService.class);
		roleService = DIContainer.getInstance().retrieve(RoleService.class);
		userService = DIContainer.getInstance().retrieve(UserService.class);
		initializeByHand();
		logger.info("SecurityFacade Initialized!");
	}

	private static void initializeByHand() {
		try {
			sm = SecurityUtils.getSecurityManager();
		} catch (UnavailableSecurityManagerException e) {
			logger.warn("no SecurityManager instance found! initialize by shiro.ini");
			String file = AppProperties.getAppHome() + "shiro.ini";
			File f = new File(file);
			if (f.exists()) {
				file = "file:" + file;
				logger.info("load from[{}]", file);
			} else {
				file = "classpath:" + file; 
				logger.info("load from[{}]", file);
			}
			Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory(file);
			sm = factory.getInstance();
			SecurityUtils.setSecurityManager(sm);
		}
	}

	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	public static LoginStatus login(String userName, String password) {
		Subject subject = getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
		try {
			subject.login(token);
			if (subject.isAuthenticated()) {
				return LoginStatus.Success;
			}
		} catch (UnknownAccountException e) {
			return LoginStatus.FailWithUnknownUsername;
		} catch (IncorrectCredentialsException e) {
			return LoginStatus.FailWithErrorPassowrd;
		} catch (LockedAccountException e) {
			return LoginStatus.FailWithLocked;
		} catch (ExcessiveAttemptsException e) {
			return LoginStatus.FailWithLimitRetryCount;
		} catch (Exception e) {
			logger.error("logger err", e);
			return LoginStatus.FailWithunknownReason;
		}
		return LoginStatus.FailWithunknownReason;
	}

	public static PermissionService getPermissionservice() {
		return permissionService;
	}

	/**
	 * 从当前登录会话中获取用户对象
	 * 
	 * @return
	 */
	public static SecUsers getUserFromSession() {
		Subject subject = getSubject();
		Session s = subject.getSession();
		if (s != null) {
			SecUsers user = (SecUsers) s.getAttribute("APP_LOGINED_USER");
			return user;
		}
		return null;
	}

	public static RoleService getRoleservice() {
		return roleService;
	}

	public static UserService getUserservice() {
		return userService;
	}

	public static void logout() {
		getSubject().logout();
	}
}
