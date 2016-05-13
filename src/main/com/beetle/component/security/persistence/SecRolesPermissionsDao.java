package com.beetle.component.security.persistence;

import com.beetle.component.security.dto.SecRolesPermissions;
import com.beetle.framework.persistence.access.operator.DBOperatorException;

public interface SecRolesPermissionsDao {

	int insert(SecRolesPermissions secRolesPermissions) throws DBOperatorException;

	boolean exists(SecRolesPermissions secrolespermissions) throws DBOperatorException;
	
	int delete(SecRolesPermissions secRolesPermissions) throws DBOperatorException;
	
	int deleteByPermissionId(long pid) throws DBOperatorException;
}
