package com.beetle.component.security.persistence.imp;

import com.beetle.component.security.dto.SecUsersRoles;
import com.beetle.component.security.persistence.SecUsersRolesDao;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.QueryOperator;
import com.beetle.framework.persistence.access.operator.UpdateOperator;

public class SecUsersRolesImpl implements SecUsersRolesDao {

	// fieldsNames:[roleId,userId"]
	// private TableOperator<SecUsersRoles> operator;

	public SecUsersRolesImpl() {

		// operator = new TableOperator<SecUsersRoles>("SYSDATASOURCE_DEFAULT",
		// "sec_users_roles", SecUsersRoles.class);
		//// 针对包含自增字段的表，使用下面的构造函数
		// operator = new TableOperator("SYSDATASOURCE_DEFAULT",
		//// "sec_users_roles", SecUsersRoles.class,"IDENTITY_FIELD_NAME");
	}

	public int insert(SecUsersRoles secusersroles) throws DBOperatorException {
		UpdateOperator update = new UpdateOperator();
		update.setDataSourceName(Helper.DATASOURCE);
		update.setSql("insert into sec_users_roles (userId,roleId) values (?,?)");
		update.addParameter(secusersroles.getUserId());
		update.addParameter(secusersroles.getRoleId());
		update.access();
		return update.getEffectCounts();
	}

	@Override
	public boolean exists(SecUsersRoles secUsersRoles) throws DBOperatorException {
		QueryOperator q = new QueryOperator();
		q.setDataSourceName(Helper.DATASOURCE);
		q.setSql("select count(1) cc from sec_users_roles where userId=? and roleId=? ");
		q.addParameter(secUsersRoles.getUserId());
		q.addParameter(secUsersRoles.getRoleId());
		q.access();
		Long x = (Long) q.getResultValueOfARow(0, "cc");
		if (x > 0) {
			return true;
		}
		return false;
	}

	@Override
	public void delete(SecUsersRoles secUsersRoles) throws DBOperatorException {
		UpdateOperator update = new UpdateOperator();
		update.setDataSourceName(Helper.DATASOURCE);
		update.setSql("delete from sec_users_roles where userId=? and roleId=?");
		update.addParameter(secUsersRoles.getUserId());
		update.addParameter(secUsersRoles.getRoleId());
		update.access();		
	}

	@Override
	public int deleteByRoleId(Long roleId) throws DBOperatorException {
		UpdateOperator update = new UpdateOperator();
		update.setDataSourceName(Helper.DATASOURCE);
		update.setSql("delete from sec_users_roles where roleId=?");
		update.addParameter(roleId);
		update.access();		
		return update.getEffectCounts();
	}

}