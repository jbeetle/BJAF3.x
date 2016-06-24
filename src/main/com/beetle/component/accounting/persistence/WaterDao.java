package com.beetle.component.accounting.persistence;

import java.util.List;

import com.beetle.component.accounting.dto.Water;
import com.beetle.framework.persistence.access.operator.DBOperatorException;

public interface WaterDao {
	Water get(Long waterid) throws DBOperatorException;

	List<Water> getAll() throws DBOperatorException;

	int insert(Water water) throws DBOperatorException;

	int update(Water water) throws DBOperatorException;

	int delete(Long waterid) throws DBOperatorException;

}
