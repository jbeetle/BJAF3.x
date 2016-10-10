package com.beetle.component.security.service;

import java.util.Set;

import com.beetle.component.security.dto.SecUsers;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.resource.define.PageList;

public interface UserService {
	/**
	 * 创建用户
	 * 
	 * @param user
	 */

	public SecUsers createUser(SecUsers user) throws SecurityServiceException;

	/**
	 * 修改密码
	 * 
	 * @param userId
	 * @param newPassword
	 */
	public void changePassword(Long userId, String newPassword) throws SecurityServiceException;

	/**
	 * 修改密码
	 * 
	 * @param userId
	 * @param oldPassowrd
	 *            老密码
	 * @param newPassword
	 *            新密码
	 * @throws SecurityServiceException
	 *             老密码不正确抛出异常，错误吗-1002
	 */
	void changePassword(Long userId, String oldPassowrd, String newPassword) throws SecurityServiceException;

	/**
	 * 添加用户-角色关系
	 * 
	 * @param userId
	 * @param roleIds
	 */
	public void correlationRoles(Long userId, Long... roleIds) throws SecurityServiceException;

	/**
	 * 移除用户-角色关系
	 * 
	 * @param userId
	 * @param roleIds
	 */
	public void uncorrelationRoles(Long userId, Long... roleIds) throws SecurityServiceException;

	/**
	 * 根据用户名查找用户
	 * 
	 * @param username
	 * @return
	 */
	public SecUsers findByUsername(String username) throws SecurityServiceException;

	/**
	 * 根据用户名查找其角色
	 * 
	 * @param username
	 * @return
	 */
	public Set<String> findRoles(String username) throws SecurityServiceException;

	/**
	 * 根据用户名查找其权限
	 * 
	 * @param username
	 * @return
	 */
	public Set<String> findPermissions(String username) throws SecurityServiceException;

	int updateTryTime(long userid, int time) throws SecurityServiceException;

	int lockUser(long userid) throws SecurityServiceException;

	int unlockUser(long userid) throws SecurityServiceException;

	/**
	 * 分页组合查询（查询条件可以全有，也可以为空，条件直接是and的关系）
	 * 
	 * @param userid
	 *            用户id，不查则为null
	 * @param username
	 *            用户名，支持模糊查询，不查为null
	 * @param lock
	 *            用户是为锁的状态，不查输入为null
	 * @param pageNumber
	 *            分页页号，（第1页，第2页，N...）
	 * @param pageSize
	 *            每页最多显示的数据（如：每页显示20条）
	 * @return
	 * @throws DBOperatorException
	 */
	PageList<SecUsers> compositeQuery(Long userid, String username, Integer lock, int pageNumber, int pageSize)
			throws SecurityServiceException;
}
