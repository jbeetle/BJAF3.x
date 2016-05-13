package com.beetle.component.security.service;

import com.beetle.component.security.dto.SecPermissions;

public interface PermissionService {
	SecPermissions createPermission(SecPermissions permission) throws SecurityServiceException;

	void deletePermission(Long permissionId) throws SecurityServiceException;
}
