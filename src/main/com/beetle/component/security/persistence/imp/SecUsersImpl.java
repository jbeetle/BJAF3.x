package com.beetle.component.security.persistence.imp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.beetle.component.security.dto.SecUsers;
import com.beetle.component.security.persistence.SecUsersDao;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.QueryOperator;
import com.beetle.framework.persistence.access.operator.TableOperator;

public class SecUsersImpl implements SecUsersDao {

	// fieldsNames:[password,salt,locked,userId,username"]
	private TableOperator<SecUsers> operator;

	public SecUsersImpl() {
		// operator = new TableOperator<SecUsers>("SYSDATASOURCE_DEFAULT",
		// "sec_users", SecUsers.class);
		//// 针对包含自增字段的表，使用下面的构造函数
		operator = new TableOperator<SecUsers>(Helper.DATASOURCE, "sec_users", SecUsers.class, "userId");
	}

	public SecUsers get(Long id) throws DBOperatorException {
		return operator.selectByPrimaryKey(id);
	}

	public List<SecUsers> getAll() throws DBOperatorException {
		return operator.selectByWhereCondition("", null);
	}

	public int insert(SecUsers secusers) throws DBOperatorException {
		return operator.insert(secusers);
	}

	public int update(SecUsers secusers) throws DBOperatorException {
		return operator.update(secusers);
	}

	public int delete(Long id) throws DBOperatorException {
		return operator.deleteByPrimaryKey(id);
	}

	@Override
	public SecUsers getByName(String username) throws DBOperatorException {
		List<SecUsers> x = operator.selectByWhereCondition("where username=?", new Object[] { username });
		if (x.isEmpty()) {
			return null;
		}
		return x.get(0);
	}

	@Override
	public Set<String> findRoles(String username) throws DBOperatorException {
		String sql = "select role from sec_users u, sec_roles r,sec_users_roles ur where u.username=? and u.userId=ur.userId and r.roleId=ur.roleId";
		QueryOperator q = new QueryOperator();
		q.setDataSourceName(Helper.DATASOURCE);
		q.setSql(sql);
		q.addParameter(username);
		q.access();
		int rows = q.resultRowCount();
		Set<String> ss = new HashSet<String>();
		for (int i = 0; i < rows; i++) {
			String v = (String) q.getResultValueOfARow(i, "role");
			ss.add(v);
		}
		return ss;
	}

	@Override
	public Set<String> findPermissions(String username) throws DBOperatorException {
		String sql = "select permission from sec_users u, sec_roles r, sec_permissions p, sec_users_roles ur, sec_roles_permissions rp where u.username=? and u.userId=ur.userId and r.roleId=ur.roleId and r.roleId=rp.roleId and p.permissionId=rp.permissionId";
		QueryOperator q = new QueryOperator();
		q.setDataSourceName(Helper.DATASOURCE);
		q.setSql(sql);
		q.addParameter(username);
		q.access();
		int rows = q.resultRowCount();
		Set<String> ss = new HashSet<String>();
		for (int i = 0; i < rows; i++) {
			String v = (String) q.getResultValueOfARow(i, "permission");
			ss.add(v);
		}
		return ss;
	}

	@Override
	public int updateTryTime(long userid, int times) throws DBOperatorException {
		Map<String, Object> fd = new HashMap<String, Object>();
		fd.put("userId", userid);
		fd.put("trycount", times);
		return operator.update(fd);
	}

	@Override
	public int updateLock(long userid, int lock) throws DBOperatorException {
		Map<String, Object> fd = new HashMap<String, Object>();
		fd.put("userId", userid);
		fd.put("locked", lock);
		return operator.update(fd);
	}

}