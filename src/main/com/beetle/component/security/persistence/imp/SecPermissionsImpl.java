package com.beetle.component.security.persistence.imp;

import java.util.List;

import com.beetle.component.security.dto.SecPermissions;
import com.beetle.component.security.persistence.SecPermissionsDao;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.TableOperator;

public class SecPermissionsImpl implements SecPermissionsDao {

	// fieldsNames:[permissionId,available,description,permission"]
	private TableOperator<SecPermissions> operator;

	public SecPermissionsImpl() {
		// operator = new TableOperator<SecPermissions>(Helper.DATASOURCE,
		// "sec_permissions", SecPermissions.class);
		//// 针对包含自增字段的表，使用下面的构造函数
		operator = new TableOperator<SecPermissions>(Helper.DATASOURCE, "sec_permissions", SecPermissions.class,
				"permissionId");
	}

	public SecPermissions get(Long id) throws DBOperatorException {
		return operator.selectByPrimaryKey(id);
	}

	public List<SecPermissions> getAll() throws DBOperatorException {
		return operator.selectByWhereCondition("", null);
	}

	public SecPermissions insert(SecPermissions secpermissions) throws DBOperatorException {
		Long x = operator.insertAndRetrievePK(secpermissions);
		secpermissions.setPermissionId(x);
		return secpermissions;
	}

	public int update(SecPermissions secpermissions) throws DBOperatorException {
		return operator.update(secpermissions);
	}

	public int delete(Long id) throws DBOperatorException {
		return operator.deleteByPrimaryKey(id);
	}

}