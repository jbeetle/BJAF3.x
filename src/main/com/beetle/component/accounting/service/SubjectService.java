package com.beetle.component.accounting.service;

import com.beetle.component.accounting.dto.Subject;

public interface SubjectService {
	void createSubject(Subject subject) throws AccountingServiceException;
}
