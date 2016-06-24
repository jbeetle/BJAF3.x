package com.beetle.component.accounting.service.imp;

import com.beetle.component.accounting.dto.Subject;
import com.beetle.component.accounting.persistence.SubjectDao;
import com.beetle.component.accounting.service.AccountingServiceException;
import com.beetle.component.accounting.service.SubjectService;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.resource.dic.def.InjectField;

public class SubjectServiceImpl implements SubjectService {
	@InjectField
	private SubjectDao subDao;

	@Override
	public void createSubject(Subject subject) throws AccountingServiceException {
		try {
			subDao.insert(subject);
		} catch (DBOperatorException e) {
			throw new AccountingServiceException(e);
		}
	}

}
