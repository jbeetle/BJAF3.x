/*
 * BJAF - Beetle J2EE Application Framework
 * �׿ǳ�J2EE��ҵӦ�ÿ������
 * ��Ȩ����2003-2009 ��ƶ� (www.beetlesoft.net)
 * 
 * ����һ����ѿ�Դ�������������ڡ��׿ǳ�J2EEӦ�ÿ�������ȨЭ�顷
 *
 *   ��GNU Lesser General Public License v3.0��
 *<http://www.gnu.org/licenses/lgpl-3.0.txt/>�ºϷ�ʹ�á��޸Ļ����·�����
 *
 * ��л��ʹ�á��ƹ㱾��ܣ����н�������⣬��ӭ�������ϵ��
 * �ʼ��� <yuhaodong@gmail.com/>.
 */
package com.beetle.framework.persistence.pagination.imp;

import com.beetle.framework.persistence.pagination.*;

/**
 * <p>
 * Title: Beetle Persistence Framework
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * <p>
 * Company: BeetleSoft
 * </p>
 * 
 * @author HenryYu (yuhaodong@gmail.com)
 * @version 1.0
 */
public class DB2PaginationImp implements IPagination {
	public DB2PaginationImp() {
	}

	/**
	 * getPagination
	 * 
	 * @param pInfo
	 *            PageParameter
	 * @return PageResult
	 * @throws PaginationException
	 * @todo Implement this
	 *       com.beetle.framework.persistence.pagination.IPagination method
	 */
	public PageResult page(PageParameter pInfo) throws PaginationException {
		throw new PaginationException("not implemented,yet!");
	}

	public PageBaseInfo calc(PageParameter pInfo) throws PaginationException {
		// TODO Auto-generated method stub
		return null;
	}
}
