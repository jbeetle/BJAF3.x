package com.beetle.component.security.persistence;

import java.util.List;
import java.util.Set;

import com.beetle.component.security.dto.SecUsers;
import com.beetle.framework.persistence.access.operator.DBOperatorException;

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
}
