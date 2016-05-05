package com.beetle.uidemo.business.imp;

import com.beetle.framework.resource.define.PageList;
import com.beetle.framework.resource.dic.def.InjectField;
import com.beetle.framework.resource.dic.def.ServiceTransaction;
import com.beetle.uidemo.business.IUserManagerService;
import com.beetle.uidemo.business.ServiceException;
import com.beetle.uidemo.persistence.dao.IXxUserDao;
import com.beetle.uidemo.valueobject.XxUser;

public class BsUserManagerService implements IUserManagerService {

	public BsUserManagerService() {
		super();
	}

	@InjectField
	private IXxUserDao userdao;

	@Override
	public PageList<XxUser> showAllUserByPage(int pageNumber, int pageSize,
			String orderField, String orderArith) throws ServiceException {
		return userdao.getAllUserByPage(pageNumber, pageSize, orderField,
				orderArith);
	}

	@ServiceTransaction
	@Override
	public void createUser(XxUser user) throws ServiceException {
		if (user == null) {
			throw new ServiceException("user can't be null");
		}
		user.setUserid(userdao.generateUserId());
		userdao.insert(user);
	}

	@Override
	public int deleteUser(long userId) throws ServiceException {
		return userdao.delete(userId);
	}

	@Override
	public XxUser findUser(long userId) throws ServiceException {
		return userdao.get(userId);
	}

	@Override
	public int updateUser(XxUser user) throws ServiceException {
		return userdao.update(user);
	}

	@Override
	public PageList<XxUser> searchByName(String username, int pageNumber,
			int pageSize, String orderField, String orderArith)
			throws ServiceException {
		return userdao.findByName(username, pageNumber, pageSize, orderField,
				orderArith);

	}

}
