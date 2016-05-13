package com.beetle.component.security.persistence.imp;

import java.util.List;

import com.beetle.component.security.dto.SecRoles;
import com.beetle.component.security.persistence.SecRolesDao;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.TableOperator;

public class SecRolesImpl implements SecRolesDao {

	// fieldsNames:[role,roleId,available,description"]
	private TableOperator<SecRoles> operator;

	public SecRolesImpl() {
		// operator = new TableOperator<SecRoles>(Helper.DATASOURCE,
		// "sec_roles", SecRoles.class);
		//// 针对包含自增字段的表，使用下面的构造函数
		operator = new TableOperator<SecRoles>(Helper.DATASOURCE, "sec_roles", SecRoles.class, "roleId");
	}

	public SecRoles get(Long id) throws DBOperatorException {
		return operator.selectByPrimaryKey(id);
	}

	public List<SecRoles> getAll() throws DBOperatorException {
		return operator.selectByWhereCondition("", null);
	}

	public SecRoles insert(SecRoles secroles) throws DBOperatorException {
		Long pk = operator.insertAndRetrievePK(secroles);
		secroles.setRoleId(pk);
		return secroles;
	}

	public int update(SecRoles secroles) throws DBOperatorException {
		return operator.update(secroles);
	}

	public int delete(Long id) throws DBOperatorException {
		return operator.deleteByPrimaryKey(id);
	}

}