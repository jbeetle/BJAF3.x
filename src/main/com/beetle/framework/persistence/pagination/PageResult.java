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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.RsDataSet;
import com.beetle.framework.resource.define.PageList;

public class PageResult {
	/**
	 * 当前页号
	 */
	private int curPageNumber;

	private int nextPageNumber;

	private int prePageNumber;

	private int pageAmount;

	private int curPos;

	/**
	 * 当前页面大小
	 */
	private int curPageSize;

	private int recordAmount;
	private int pageSize;

	public PageResult() {
		this.sqlResultSet = new ArrayList<Map<String, Object>>();
	}

	/**
	 * 获取当前页面的数据集
	 * 
	 * @return List
	 */
	public List<Map<String, Object>> getSqlResultSet() {
		return sqlResultSet;
	}

	/**
	 * 获取当前页号
	 * 
	 * @return int
	 */
	public int getCurPageNumber() {
		return curPageNumber;
	}

	/**
	 * 获取当前页面大小
	 * 
	 * @return int
	 */
	public int getCurPageSize() {
		return curPageSize;
	}

	/**
	 * 获取总记录数
	 * 
	 * @return int
	 */
	public int getRecordAmount() {
		return recordAmount;
	}

	/**
	 * 获取下一页的页号
	 * 
	 * @return int
	 */
	public int getNextPageNumber() {
		return nextPageNumber;
	}

	/**
	 * 获取总页数
	 * 
	 * 
	 * @return int
	 */
	public int getPageAmount() {
		return pageAmount;
	}

	/**
	 * 获取前页号
	 * 
	 * 
	 * @return int
	 */
	public int getPrePageNumber() {
		return prePageNumber;
	}

	/**
	 * 获取当前记录位置
	 * 
	 * @return int
	 */
	public int getCurPos() {
		return curPos;
	}

	/**
	 * 首页页号，为常数1
	 * 
	 * @return int
	 */
	public int getFirstPageNumber() {
		return 1;
	}

	/**
	 * 页面大小（页面想要显示的记录条数）
	 * 
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 最后页号
	 * 
	 * 
	 * @return int
	 */
	public int getLastPageNumber() {
		return pageAmount;
	}

	public void setSqlResultSet(List<Map<String, Object>> sqlResultSet) {
		if (sqlResultSet != null) {
			this.sqlResultSet.addAll(sqlResultSet);
		}
		// this.sqlResultSe = sqlResultSe.
	}

	public void setCurPageNumber(int curPageNumber) {
		this.curPageNumber = curPageNumber;
	}

	public void setCurPageSize(int curPageSize) {
		this.curPageSize = curPageSize;
	}

	public void setRecordAmount(int recordAmount) {
		this.recordAmount = recordAmount;
	}

	public void setNextPageNumber(int nextPageNumber) {
		this.nextPageNumber = nextPageNumber;
	}

	public void setPageAmount(int pageAmount) {
		this.pageAmount = pageAmount;
	}

	public void setPrePageNumber(int prePageNumber) {
		this.prePageNumber = prePageNumber;
	}

	public void setCurPos(int curPos) {
		this.curPos = curPos;
	}

	public <T> PageList<T> getPageList(Class<T> dtoClass) {
		RsDataSet rs = new RsDataSet(getSqlResultSet());
		PageList<T> pl = new PageList<T>(rs.rowCount);
		try {
			for (int i = 0; i < rs.rowCount; i++) {
				T t = dtoClass.newInstance();
				rs.autoFillRow(t);
				pl.add(t);
				rs.next();
			}
			pl.setCurPageNumber(getCurPageNumber());
			pl.setCurPageSize(getCurPageSize());
			pl.setRecordAmount(getRecordAmount());
			pl.setNextPageNumber(getNextPageNumber());
			pl.setPageAmount(getPageAmount());
			pl.setPrePageNumber(getPrePageNumber());
			pl.setCurPos(getCurPos());
			pl.setPageSize(getPageSize());
			return pl;
		} catch (Exception e) {
			throw new DBOperatorException(e);
		} finally {
			rs.clearAll();
		}
	}

	/**
	 * 结果集列表
	 */
	private List<Map<String, Object>> sqlResultSet;

	/**
	 * 清除内存查询结果
	 */
	public void clearAll() {
		if (!this.sqlResultSet.isEmpty()) {
			this.sqlResultSet.clear();
			this.sqlResultSet = null;
		}

	}
}
