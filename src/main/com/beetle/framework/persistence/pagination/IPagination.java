/*
 * BJAF - Beetle J2EE Application Framework
 * 甲壳虫J2EE企业应用开发框架
 * 版权所有2003-2015 余浩东 (www.beetlesoft.net)
 * 
 * 这是一个免费开源的软件，您必须在
 *<http://www.apache.org/licenses/LICENSE-2.0>
 *协议下合法使用、修改或重新发布。
 *
 * 感谢您使用、推广本框架，若有建议或问题，欢迎您和我联系。
 * 邮件： <yuhaodong@gmail.com/>.
 */
package com.beetle.framework.persistence.pagination;

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

public interface IPagination {
	/**
	 * 执行分页查询，返回查询数据
	 * 
	 * 
	 * @param pInfo
	 *            PageParameter
	 * @return PageResult
	 * @throws PaginationException
	 */
	PageResult page(PageParameter pInfo) throws PaginationException;

	/**
	 * 计算查询结果集以便获取基础信息
	 * 
	 * @param pInfo
	 * @return
	 * @throws PaginationException
	 */
	PageBaseInfo calc(PageParameter pInfo) throws PaginationException;
}
