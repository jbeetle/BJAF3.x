package com.beetle.component.security.service.imp;

import com.beetle.component.security.dto.SecPermissions;
import com.beetle.component.security.persistence.SecPermissionsDao;
import com.beetle.component.security.persistence.SecRolesPermissionsDao;
import com.beetle.component.security.service.PermissionService;
import com.beetle.component.security.service.SecurityServiceException;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.resource.dic.def.InjectField;
import com.beetle.framework.resource.dic.def.ServiceTransaction;

public class PermissionServiceImpl implements PermissionService {
	@InjectField
	private SecPermissionsDao pmsDao;
	@InjectField
	private SecRolesPermissionsDao rpDao;

	@Override
	public SecPermissions createPermission(SecPermissions permission) throws SecurityServiceException {
		try {
			return pmsDao.insert(permission);
		} catch (DBOperatorException de) {
			throw new SecurityServiceException(de);
		}
	}

	@Override
	@ServiceTransaction
	public void deletePermission(Long permissionId) throws SecurityServiceException {
		try {
			rpDao.deleteByPermissionId(permissionId);
			pmsDao.delete(permissionId);
		} catch (DBOperatorException de) {
			throw new SecurityServiceException(de);
		}
	}

}
