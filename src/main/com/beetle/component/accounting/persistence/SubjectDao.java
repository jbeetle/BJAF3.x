package com.beetle.component.accounting.persistence;

import java.util.List;

import com.beetle.component.accounting.dto.Subject;
import com.beetle.framework.persistence.access.operator.DBOperatorException;

public interface SubjectDao {
	Subject get(String subjectno) throws DBOperatorException;

	List<Subject> getAll() throws DBOperatorException;

	int insert(Subject subject) throws DBOperatorException;

	int update(Subject subject) throws DBOperatorException;

	int delete(String subjectno) throws DBOperatorException;

}
