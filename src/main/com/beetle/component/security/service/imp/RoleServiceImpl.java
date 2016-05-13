package com.beetle.component.security.service.imp;

import com.beetle.component.security.dto.SecRoles;
import com.beetle.component.security.dto.SecRolesPermissions;
import com.beetle.component.security.persistence.SecRolesDao;
import com.beetle.component.security.persistence.SecRolesPermissionsDao;
import com.beetle.component.security.persistence.SecUsersRolesDao;
import com.beetle.component.security.service.RoleService;
import com.beetle.component.security.service.SecurityServiceException;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.resource.dic.def.InjectField;
import com.beetle.framework.resource.dic.def.ServiceTransaction;
import com.beetle.framework.resource.dic.def.ServiceTransaction.Manner;

public class RoleServiceImpl implements RoleService {
	@InjectField
	private SecRolesDao roleDao;
	@InjectField
	private SecUsersRolesDao userRoleDao;
	@InjectField
	private SecRolesPermissionsDao rolePermDao;

	@Override
	public SecRoles createRole(SecRoles role) throws SecurityServiceException

	{
		try {
			return roleDao.insert(role);
		} catch (DBOperatorException de) {
			throw new SecurityServiceException(de);
		}
	}

	@Override
	@ServiceTransaction(manner = Manner.REQUIRED)
	public void deleteRole(Long roleId) throws SecurityServiceException

	{
		try {
			userRoleDao.deleteByRoleId(roleId);
			roleDao.delete(roleId);
		} catch (DBOperatorException de) {
			throw new SecurityServiceException(de);
		}
	}

	@Override
	@ServiceTransaction(manner = Manner.REQUIRED)
	public void correlationPermissions(Long roleId, Long... permissionIds) throws SecurityServiceException

	{
		try {
			for (Long pid : permissionIds) {
				SecRolesPermissions rp = new SecRolesPermissions();
				rp.setPermissionId(pid);
				rp.setRoleId(roleId);
				if (!rolePermDao.exists(rp)) {
					rolePermDao.insert(rp);
				}
			}
		} catch (DBOperatorException de) {
			throw new SecurityServiceException(de);
		}

	}

	@Override
	@ServiceTransaction
	public void uncorrelationPermissions(Long roleId, Long... permissionIds) throws SecurityServiceException

	{
		try {
			for (Long pid : permissionIds) {
				SecRolesPermissions rp = new SecRolesPermissions();
				rp.setPermissionId(pid);
				rp.setRoleId(roleId);
				rolePermDao.delete(rp);
			}
		} catch (DBOperatorException de) {
			throw new SecurityServiceException(de);
		}
	}

}
