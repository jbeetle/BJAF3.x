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
 * <p>Title: Beetle Persistence Framework</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: BeetleSoft</p>
 *
 * @author HenryYu (yuhaodong@gmail.com)
 * @version 1.0
 */
import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.persistence.access.DBConfig;
import com.beetle.framework.util.cache.ICache;
import com.beetle.framework.util.cache.StrongCache;

final public class PaginationOperator {
	private PaginationOperator() {
	}

	private static ICache cache = new StrongCache();

	/**
	 * 执行分页查询，并返回当前页结果
	 * 
	 * @param pageParam
	 *            --页参数
	 * @return
	 */
	public static PageResult access(PageParameter pageParam) {
		String dataSourceName = pageParam.getDataSourceName();
		IPagination ip = (IPagination) cache.get(dataSourceName);
		if (ip == null) {
			String classname = DBConfig.getExtensionValue(dataSourceName,
					"pagination-imp");
			if (classname == null || classname.trim().length() == 0) {
				throw new PaginationException(
						"can't found 'Extensions->"
								+ dataSourceName
								+ "->pagination-imp'setting in the DBConfig.xml,please check it! ");
			}
			ip = loadInstance(dataSourceName, classname);
		}
		PageResult pr = ip.page(pageParam);
		pr.setPageSize(pageParam.getPageSize());
		return pr;
	}

	private static IPagination loadInstance(String ds, String classname)
			throws AppRuntimeException {
		IPagination ip;
		try {
			Object obj = Class.forName(classname).newInstance();
			cache.put(ds, (IPagination) obj);
			ip = (IPagination) obj;
		} catch (Exception ex) {
			throw new AppRuntimeException(ex);
		}
		return ip;
	}

}
