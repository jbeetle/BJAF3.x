package com.beetle.component.security.persistence;

import java.util.List;
import java.util.Set;

import com.beetle.component.security.dto.SecUsers;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.resource.define.PageList;

public interface SecUsersDao {
	SecUsers get(Long userid) throws DBOperatorException;

	List<SecUsers> getAll() throws DBOperatorException;

	int insert(SecUsers secUsers) throws DBOperatorException;

	int update(SecUsers secUsers) throws DBOperatorException;

	int delete(Long userid) throws DBOperatorException;

	SecUsers getByName(String username) throws DBOperatorException;

	Set<String> findRoles(String username) throws DBOperatorException;

	Set<String> findPermissions(String username) throws DBOperatorException;

	int updateTryTime(long userid, int times) throws DBOperatorException;

	int updateLock(long userid, int lock) throws DBOperatorException;

	
	/**
	 * 分页组合查询（查询条件可以全有，也可以为空，条件直接是and的关系）
	 * @param userid 用户id，不查则为null
	 * @param username 用户名，支持模糊查询，不查为null
	 * @param lock 用户是为锁的状态，不查输入为null
	 * @param pageNumber 分页页号，（第1页，第2页，N...）
	 * @param pageSize 每页最多显示的数据（如：每页显示20条）
	 * @return
	 * @throws DBOperatorException
	 */
	PageList<SecUsers> compositeQuery(long userid, String username, int lock, int pageNumber, int pageSize)
			throws DBOperatorException;
}
