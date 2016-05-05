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
 * <p>Description: 更新操作者类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: 甲壳虫科技</p>
 * @author 余浩东

 * @version 1.0
 */

import com.beetle.framework.persistence.access.ConnectionException;
import com.beetle.framework.persistence.access.ConnectionFactory;
import com.beetle.framework.persistence.access.base.AccessMannerFactory;
import com.beetle.framework.persistence.access.base.DBAccess;
import com.beetle.framework.persistence.access.base.DBAccessException;

import java.util.ArrayList;
import java.util.List;

public final class UpdateOperator extends BaseOperator {
	private ArrayList<List<SqlParameter>> batchValues = null;
	private int effectCounts;
	private int[] batchEffectCounts;

	public UpdateOperator() {
		batchValues = null;
	}

	protected void accessImp() throws DBOperatorException {
		try {
			if (batchValues == null) {
				notBatch();
			} else { // 执行批量
				if (!this.isUseOnlyConnectionFlag()) {
					this.batchEffectCounts = DBAccess.updateBatch(
							AccessMannerFactory.getAccessManner(this.getSql(),
									this.batchValues), ConnectionFactory
									.getConncetion(this.getDataSourceName()));
				} else {
					if (!this.isPresentConnectionUsable()) {
						throw new ConnectionException(
								"the current connection is closed!");
					}
					this.batchEffectCounts = DBAccess
							.updateBatchForOneConnection(
									AccessMannerFactory.getAccessManner(
											this.getSql(), this.batchValues),
									this.getPresentConnection());
				}
			}
		} catch (ConnectionException ce) {
			throw new DBOperatorException("db connection err", ce);
		} catch (DBAccessException dbe) {
			throw new DBOperatorException("UpdateOperator err", dbe);
		} catch (Throwable e) {
			throw new DBOperatorException("UpdateOperator raise unknown err", e);
		} finally {
			if (this.batchValues != null) {
				this.batchValues.clear();
			}
		}
	}

	private void notBatch() {
		if (this.getParameters().isEmpty()) {
			if (!this.isUseOnlyConnectionFlag()) {
				effectCounts = DBAccess.update(AccessMannerFactory
						.getAccessManner(this.getSql()), ConnectionFactory
						.getConncetion(this.getDataSourceName()));
			} else {
				if (!this.isPresentConnectionUsable()) {
					// this.setPresentConnection(ConnectionFactory
					// .getConncetion(this.getDataSourceName()));
					throw new ConnectionException(
							"the current connection is closed!");
				}
				effectCounts = DBAccess.updateForOneConnection(
						AccessMannerFactory.getAccessManner(this.getSql()),
						this.getPresentConnection());
			}
		} else {
			if (!this.isUseOnlyConnectionFlag()) {
				effectCounts = DBAccess.update(AccessMannerFactory
						.getAccessManner(this.getSql(), this.getParameters()),
						ConnectionFactory.getConncetion(this
								.getDataSourceName()));
			} else {
				if (!this.isPresentConnectionUsable()) {
					// this.setPresentConnection(ConnectionFactory
					// .getConncetion(this.getDataSourceName()));
					throw new ConnectionException(
							"the current connection is closed!");
				}
				effectCounts = DBAccess.updateForOneConnection(
						AccessMannerFactory.getAccessManner(this.getSql(),
								this.getParameters()),
						this.getPresentConnection());
			}
		}
	}

	/**
	 * 添加一行sql语句的参数（批处理时候使用）
	 * 
	 * @param sqlParameterSet
	 *            整条sql语句参数对象
	 */
	public void addBatchParameter(SqlParameterSet sqlParameterSet) {
		if (this.batchValues == null) {
			this.batchValues = new ArrayList<List<SqlParameter>>();
		}
		this.batchValues.add(sqlParameterSet.getSqlParameterSet());
	}

	/**
	 * 获取执行后影响的记录条数,不支持批量更新语句 对于 INSERT、UPDATE 或 DELETE 语句，返回行数; 对于什么都不返回的 SQL
	 * 语句，返回 0
	 * 
	 * @return int
	 */
	public int getEffectCounts() {
		if (!this.isAccessed()) {
			throw new com.beetle.framework.AppRuntimeException(
					"please run method of access() first!");
		}
		return effectCounts;
	}

	/**
	 * an array of update counts containing one element for each command in the
	 * batch. The elements of the array are ordered according to the order in
	 * which commands were added to the batch.
	 * 
	 * @return int[]
	 */
	public int[] getBatchEffectCounts() {
		return batchEffectCounts;
	}

}
