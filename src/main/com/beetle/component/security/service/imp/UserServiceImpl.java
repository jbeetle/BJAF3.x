package com.beetle.component.security.service.imp;

import java.util.Set;

import com.beetle.component.security.dto.SecUsers;
import com.beetle.component.security.dto.SecUsersRoles;
import com.beetle.component.security.persistence.SecUsersDao;
import com.beetle.component.security.persistence.SecUsersRolesDao;
import com.beetle.component.security.service.SecurityServiceException;
import com.beetle.component.security.service.UserService;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.resource.dic.def.InjectField;
import com.beetle.framework.resource.dic.def.ServiceTransaction;

public class UserServiceImpl implements UserService {
	@InjectField
	private SecUsersDao userDao;
	@InjectField
	private SecUsersRolesDao userRoleDao;
	private final Helper helper;

	public UserServiceImpl() {
		super();
		this.helper = new Helper();
	}

	@Override
	public SecUsers createUser(SecUsers user) throws SecurityServiceException {
		try {
			helper.encryptPassword(user);
			int i = userDao.insert(user);
			if (i <= 0) {
				throw new SecurityServiceException(-2001, "create user err");
			}
			return userDao.getByName(user.getUsername());
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	public void changePassword(Long userId, String newPassword) throws SecurityServiceException {
		try {
			SecUsers user = userDao.get(userId);
			user.setPassword(newPassword);
			helper.encryptPassword(user);
			userDao.update(user);
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	@ServiceTransaction
	public void correlationRoles(Long userId, Long... roleIds) throws SecurityServiceException {
		try {
			for (Long roleId : roleIds) {
				SecUsersRoles ur = new SecUsersRoles();
				ur.setRoleId(roleId);
				ur.setUserId(userId);
				if (!userRoleDao.exists(ur)) {
					userRoleDao.insert(ur);
				}
			}
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	@ServiceTransaction
	public void uncorrelationRoles(Long userId, Long... roleIds) throws SecurityServiceException {
		try {
			for (Long roleId : roleIds) {
				SecUsersRoles ur = new SecUsersRoles();
				ur.setRoleId(roleId);
				ur.setUserId(userId);
				userRoleDao.delete(ur);
			}
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	public SecUsers findByUsername(String username) throws SecurityServiceException {
		return userDao.getByName(username);
	}

	@Override
	public Set<String> findRoles(String username) throws SecurityServiceException {
		try {
			return userDao.findRoles(username);
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	public Set<String> findPermissions(String username) throws SecurityServiceException {
		try {
			return userDao.findPermissions(username);
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	public int updateTryTime(long userid, int time) throws SecurityServiceException {
		try {
			return userDao.updateTryTime(userid, time);
		} catch (DBOperatorException e) {
			throw new SecurityServiceException(e);
		}
	}

}
