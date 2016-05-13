package com.beetle.component.security.persistence;

import java.util.List;

import com.beetle.component.security.dto.SecPermissions;
import com.beetle.framework.persistence.access.operator.DBOperatorException;

public interface SecPermissionsDao {
	SecPermissions get(Long permissionid) throws DBOperatorException;

	List<SecPermissions> getAll() throws DBOperatorException;

	SecPermissions insert(SecPermissions secPermissions) throws DBOperatorException;

	int update(SecPermissions secPermissions) throws DBOperatorException;

	int delete(Long permissionid) throws DBOperatorException;

}
