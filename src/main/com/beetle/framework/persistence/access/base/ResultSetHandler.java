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
package com.beetle.framework.persistence.access.base;

/**
 * <p>Title: </p>
 * <p>Description: 结果集处理实现类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 余浩东

 * @version 1.0
 */

import com.beetle.framework.AppRuntimeException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final public class ResultSetHandler {
	private int rowCount;

	private int columnCount;

	private int[] columnTypes;

	private String[] columnNames;

	private List<Map<String, Object>> sqlResultSet; // 数据结果集

	private boolean isBuilded = false;

	public ResultSetHandler() {
		sqlResultSet = new ArrayList<Map<String, Object>>();
	}

	public ResultSetHandler(int dataSize) {
		sqlResultSet = new ArrayList<Map<String, Object>>(dataSize);
	}

	public void handle(ResultSet rs) throws SQLException {
		isBuilded = true;
		if (rowCount == 0) {
			ResultSetMetaData rsmd = rs.getMetaData();
			columnCount = rsmd.getColumnCount();
			columnTypes = new int[columnCount];
			columnNames = new String[columnCount];
			for (int i = 0; i < columnCount; i++) {
				columnTypes[i] = rsmd.getColumnType(i + 1);
				// columnNames[i] = rsmd.getColumnName(i + 1);
				columnNames[i] = rsmd.getColumnLabel(i + 1); // 2007-4-24,//修正部分数据库驱动无法处理as同名的bug
			}
		}
		rowCount++;
		processRow(rs);
	}

	private void processRow(ResultSet rs) throws SQLException {
		// HashMap rowMap = new HashMap(columnCount);
		Map<String, Object> rowMap = new LinkedHashMap<String, Object>(
				columnCount); // 保持顺序
		try {
			for (int i = 0; i < columnCount; i++) {
				// String key = columnNames[i].trim().toUpperCase();2006-11-5
				String key = columnNames[i].trim();
				Object value = rs.getObject(i + 1); // (columnNames[i]);?2006-9-29
				rowMap.put(key, value);
			}
			sqlResultSet.add(rowMap);
		} catch (SQLException sqe) {
			sqe.printStackTrace();
			throw sqe;
		}
	}

	public int getColumnCount() {
		return columnCount;
	}

	public String[] getColumnNames() {
		if (this.isBuilded) {
			return columnNames;
		} else {
			throw new AppRuntimeException("execute the access()method,first!");
		}
	}

	public int[] getColumnTypes() {
		if (this.isBuilded) {
			return columnTypes;
		} else {
			throw new AppRuntimeException("execute the access()method,first!");
		}
	}

	public int getRowCount() {
		return rowCount;
	}

	public final List<Map<String, Object>> getResultDataSet()
			throws DBAccessException {
		return sqlResultSet;
	}

	/*
	 * protected void finalize() throws Throwable { if (this.sqlResultSet !=
	 * null && !this.sqlResultSet.isEmpty()) { this.sqlResultSet.clear(); }
	 * OtherUtil.clearArray(this.columnNames); //this.columnTypes = null;
	 * super.finalize(); }
	 */
}
