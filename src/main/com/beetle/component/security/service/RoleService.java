package com.beetle.component.security.service;

import com.beetle.component.security.dto.SecRoles;

public interface RoleService {
	SecRoles createRole(SecRoles role) throws SecurityServiceException;

	void deleteRole(Long roleId) throws SecurityServiceException;

	/**
	 * 添加角色-权限之间关系
	 * 
	 * @param roleId
	 * @param permissionIds
	 */
	void correlationPermissions(Long roleId, Long... permissionIds) throws SecurityServiceException;

	/**
	 * 移除角色-权限之间关系
	 * 
	 * @param roleId
	 * @param permissionIds
	 */
	void uncorrelationPermissions(Long roleId, Long... permissionIds) throws SecurityServiceException;
}
