package com.beetle.component.security.persistence.imp;

import com.beetle.component.security.dto.SecRolesPermissions;
import com.beetle.component.security.persistence.SecRolesPermissionsDao;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.QueryOperator;
import com.beetle.framework.persistence.access.operator.UpdateOperator;

public class SecRolesPermissionsImpl implements SecRolesPermissionsDao {

	// fieldsNames:[permissionId,roleId"]
	// private TableOperator<SecRolesPermissions> operator;

	public SecRolesPermissionsImpl() {
		// operator = new
		// TableOperator<SecRolesPermissions>("SYSDATASOURCE_DEFAULT",
		// "sec_roles_permissions",
		// SecRolesPermissions.class);
		//// 针对包含自增字段的表，使用下面的构造函数
		// operator = new TableOperator("SYSDATASOURCE_DEFAULT",
		//// "sec_roles_permissions",
		//// SecRolesPermissions.class,"IDENTITY_FIELD_NAME");
	}

	public int insert(SecRolesPermissions secrolespermissions) throws DBOperatorException {
		UpdateOperator u = new UpdateOperator();
		u.setDataSourceName(Helper.DATASOURCE);
		u.setSql("insert into sec_roles_permissions (permissionId,roleId) values (?,?)");
		u.addParameter(secrolespermissions.getPermissionId());
		u.addParameter(secrolespermissions.getRoleId());
		u.access();
		return u.getEffectCounts();
	}

	@Override
	public boolean exists(SecRolesPermissions secrolespermissions) throws DBOperatorException {
		QueryOperator q = new QueryOperator();
		q.setDataSourceName(Helper.DATASOURCE);
		q.setSql("select count(1) cc from sec_roles_permissions where permissionId=? and roleId=? ");
		q.addParameter(secrolespermissions.getPermissionId());
		q.addParameter(secrolespermissions.getRoleId());
		q.access();
		Long x = (Long) q.getResultValueOfARow(0, "cc");
		if (x > 0) {
			return true;
		}
		return false;
	}

	@Override
	public int delete(SecRolesPermissions secRolesPermissions) throws DBOperatorException {
		UpdateOperator u = new UpdateOperator();
		u.setDataSourceName(Helper.DATASOURCE);
		u.setSql("delete from sec_roles_permissions where permissionId=? and roleId=?");
		u.addParameter(secRolesPermissions.getPermissionId());
		u.addParameter(secRolesPermissions.getRoleId());
		u.access();
		return u.getEffectCounts();
	}

	@Override
	public int deleteByPermissionId(long pid) throws DBOperatorException {
		UpdateOperator u = new UpdateOperator();
		u.setDataSourceName(Helper.DATASOURCE);
		u.setSql("delete from sec_roles_permissions where permissionId=?");
		u.addParameter(pid);
		u.access();
		return u.getEffectCounts();
	}

}