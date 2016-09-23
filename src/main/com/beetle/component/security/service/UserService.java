package com.beetle.component.security.service;

import java.util.Set;

import com.beetle.component.security.dto.SecUsers;

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
	
}
