package com.beetle.component.security.persistence;

import com.beetle.component.security.dto.SecUsersRoles;
import com.beetle.framework.persistence.access.operator.DBOperatorException;

public interface SecUsersRolesDao {

	int insert(SecUsersRoles secUsersRoles) throws DBOperatorException;

	boolean exists(SecUsersRoles secUsersRoles) throws DBOperatorException;

	void delete(SecUsersRoles secUsersRoles) throws DBOperatorException;

	int deleteByRoleId(Long roleId) throws DBOperatorException;
}
