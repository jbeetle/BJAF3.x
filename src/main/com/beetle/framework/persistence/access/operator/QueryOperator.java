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
package com.beetle.framework.persistence.access.operator;

/**
 * <p>Title: </p>
 * <p>Description: 查询操作者类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: 甲壳虫科技</p>
 * @author 余浩东

 * @version 1.0
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.beetle.framework.AppRuntimeException;
import com.beetle.framework.persistence.access.ConnectionException;
import com.beetle.framework.persistence.access.ConnectionFactory;
import com.beetle.framework.persistence.access.base.AccessMannerFactory;
import com.beetle.framework.persistence.access.base.DBAccess;
import com.beetle.framework.persistence.access.base.DBAccessException;
import com.beetle.framework.persistence.access.base.ResultSetHandler;
import com.beetle.framework.resource.define.CompositeDTO;

public class QueryOperator extends BaseOperator {
	/**
	 * 默认构造函数，此函数默认会根据系统配置来进行脱敏处理，是否进行要看数据源配置而定<br>
	 * <item name="desensitize-imp" value=
	 * "com.beetle.framework.resource.desensitize.DefaultDesensitizeImpl" /><br>
	 * 如果这个配置不配，则不做脱敏处理
	 */
	public QueryOperator() {
		maxRow = 0;
		this.notDesensitize = false;
	}

	/**
	 * 
	 * @param notDesensitize
	 *            为true,无论是否配置desensitize，查询结果不会做脱敏处理。
	 */
	public QueryOperator(boolean notDesensitize) {
		super();
		this.notDesensitize = notDesensitize;
	}

	private int maxRow = 0;
	private final boolean notDesensitize;// 不做脱敏处理
	private List<Map<String, Object>> SQLResultSet = null;

	private ResultSetHandler handlerImp = null;

	protected void accessImp() throws DBOperatorException {
		doAccess();
	}

	private void doAccess() throws DBOperatorException {
		try {
			handlerImp = new ResultSetHandler(notDesensitize, this.getDataSourceName());
			if (!this.getParameters().isEmpty()) {
				if (!this.isUseOnlyConnectionFlag()) {
					DBAccess.query(AccessMannerFactory.getAccessManner(this.getSql(), this.getParameters()), handlerImp,
							ConnectionFactory.getConncetion(this.getDataSourceName()), this.maxRow);
				} else {
					if (!this.isPresentConnectionUsable()) {
						// this.setPresentConnection(ConnectionFactory
						// .getConncetion(this.getDataSourceName()));
						throw new DBOperatorException(-1007, "the current connection is closed!");
					}
					DBAccess.queryForOneConnection(
							AccessMannerFactory.getAccessManner(this.getSql(), this.getParameters()), handlerImp,
							this.getPresentConnection(), this.maxRow);
				}
			} else {
				if (!this.isUseOnlyConnectionFlag()) {
					DBAccess.query(AccessMannerFactory.getAccessManner(this.getSql()), handlerImp,
							ConnectionFactory.getConncetion(this.getDataSourceName()), this.maxRow);
				} else {
					if (!this.isPresentConnectionUsable()) {
						// this.setPresentConnection(ConnectionFactory
						// .getConncetion(this.getDataSourceName()));
						throw new DBOperatorException(-1008, "the current connection is closed!");
					}
					DBAccess.queryForOneConnection(AccessMannerFactory.getAccessManner(this.getSql()), handlerImp,
							this.getPresentConnection(), this.maxRow);
				}
			}
			this.SQLResultSet = handlerImp.getResultDataSet();
			if (logger.isDebugEnabled()) {
				logger.debug("rowcount:" + handlerImp.getRowCount());
			}
		} catch (ConnectionException ce) {
			throw new DBOperatorException(-1009, "db connection err", ce);
		} catch (DBAccessException dbe) {
			throw new DBOperatorException(-1010, "QueryOperator err", dbe);
		} catch (Throwable e) {
			throw new DBOperatorException(-1011, "QueryOperator raise unknown err", e);
		}
	}

	/**
	 * 查询返回的结果集是否可用(是否有数据) true--结果可用，拥有记录 false--结果不可用，没有记录集
	 * 
	 * 
	 * @return boolean
	 */
	public boolean resultSetAvailable() {
		ResultSetHandler imp = getRsHandler();
		if (imp.getRowCount() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 获取结果集的行数
	 */
	public int resultRowCount() {
		ResultSetHandler imp = getRsHandler();
		return imp.getRowCount();
	}

	/*
	 * 获取结果的列数
	 */
	public int resultColumnCount() {
		ResultSetHandler imp = getRsHandler();
		return imp.getColumnCount();
	}

	ResultSetHandler getRsHandler() {
		if (handlerImp == null) {
			throw new AppRuntimeException("method[access()]has not executed yet,run it first!");
		}
		return handlerImp;
	}

	/**
	 * 根据输入的数据对象定义类，自动装配，返回其结果数据列表
	 * 
	 * @param dtoClass
	 *            --只针对自定义的值对象，不支持单个Java基础类型对象，例如：String.class等
	 * @return
	 */
	public <T> List<T> getResultList(Class<T> dtoClass) {
		if (!this.resultSetAvailable()) {
			return new ArrayList<T>();
		}
		RsDataSet rs = new RsDataSet(this.getSqlResultSet());
		List<T> lt = new ArrayList<T>(rs.rowCount);
		try {
			for (int i = 0; i < rs.rowCount; i++) {
				T t = dtoClass.newInstance();
				rs.autoFillRow(t);
				lt.add(t);
				rs.next();
			}
			return lt;
		} catch (Exception e) {
			throw new DBOperatorException("autoFillRow err ", e);
		} finally {
			rs.clearAll();
		}
	}

	/**
	 * 获取结果值对象（根据输入定义类自动状态，如果结果存在多条记录，只返回第一条，没有数据返回null）
	 * 
	 * @param dtoClass
	 * @return 如果结果存在多条记录，只返回第一条，没有数据返回null
	 */
	public <T> T getResultAsObject(Class<T> dtoClass) {
		List<T> lt = getResultList(dtoClass);
		if (lt.isEmpty()) {
			return null;
		}
		T t = lt.get(0);
		lt.clear();
		return t;
	}

	/**
	 * 针对单个数据dto对象无法包含所有的结果数据，而又不想重新定向其对应的dto对象的情况。
	 * 可以将结果集中所对应的多个数据对象定义作为参数输入，方法会自动匹配装入，返回其各个数据列表
	 * [注意：如果结果集中某些字段在各个输入值对象类中都有定义，都会被设置值。]
	 * 
	 * @param dtoClass
	 * @return CompositeDTO对象，没有数据时为null，有数据时，包含各个值对象的结果列表（对于单挑记录，其数据列表记录为1条）
	 */
	public CompositeDTO getResult(Class<?>... dtoClass) {
		if (!this.resultSetAvailable()) {
			return null;
		}
		RsDataSet rs = new RsDataSet(this.getSqlResultSet());
		try {
			@SuppressWarnings("unchecked")
			List<Object> oos[] = new ArrayList[dtoClass.length];
			for (int y = 0; y < dtoClass.length; y++) {
				rs.first();
				List<Object> lt = new ArrayList<Object>(rs.rowCount);
				for (int i = 0; i < rs.rowCount; i++) {
					Object o = dtoClass[y].newInstance();
					rs.autoFillRow(o);
					lt.add(o);
					rs.next();
				}
				oos[y] = lt;
			}
			return new CompositeDTO(oos);
		} catch (Exception e) {
			throw new DBOperatorException("autoFillRow err ", e);
		} finally {
			rs.clearAll();
		}
	}

	/**
	 * 获取查询结果集，每个记录对应一个key-value的map数据对象，key为查询语句的字段名称，value为数据
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getResultList() {
		return getSqlResultSet();
	}

	/**
	 * 获取结果集中某一行某个字段的值
	 * 
	 * @param rowIndex
	 *            行号从0开始
	 * @param fieldname
	 *            查询字段名称
	 * @return
	 */
	public Object getResultValueOfARow(int rowIndex, String fieldname) {
		Map<String, Object> arow = getResultList().get(rowIndex);
		return arow.get(fieldname);
	}

	/**
	 * 返回sql语句返回的结果列表
	 * 
	 * 
	 * @return List
	 * @throws DBOperatorException
	 */
	public List<Map<String, Object>> getSqlResultSet() {
		if (!this.isAccessed()) {
			throw new AppRuntimeException("method[access()]has not executed yet,run it first!");
		}
		return SQLResultSet;
	}

	/**
	 * 设置结果集返回的最大条数。默认有多少返回多少。
	 * 
	 * 
	 * @param maxRow
	 *            --最大返回条数
	 */
	public void setMaxRow(int maxRow) {
		this.maxRow = maxRow;
	}
}
