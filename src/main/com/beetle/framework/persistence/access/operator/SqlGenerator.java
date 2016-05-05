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

import com.beetle.framework.util.cache.ICache;
import com.beetle.framework.util.cache.StrongCache;

import java.util.Iterator;
import java.util.Set;

class SqlGenerator {
	private static ICache SQL_CACHER = new StrongCache();

	public static String generateFieldsFormatStr(Set<?> fields) {
		String s = fields.toString();
		return s.substring(1, s.length() - 1);
	}

	public static String generateFieldsFormatStr(Set<?> fields, String tableName) {
		// String s = fields.toString();
		// return s.substring(1, s.length() - 1);
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = fields.iterator();
		while (it.hasNext()) {
			String f = (String) it.next();
			sb.append(tableName);
			sb.append('.');
			sb.append(f);
			sb.append(',');
		}
		String s = sb.toString();
		s = s.substring(0, s.length() - 1);
		return s;
	}

	public static String generateFieldsValueFormatStr(Set<?> fields) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fields.size(); i++) {
			sb.append("?");
			if (i < fields.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	public static String generateFieldsValueUpdateFormatStr(Set<?> fields,
			String autoKeyName) {
		StringBuilder sb = new StringBuilder();
		Object l[] = fields.toArray();
		for (int i = 0; i < l.length; i++) {
			if (l[i].equals(autoKeyName)) {
				continue;
			}
			sb.append(l[i].toString()).append("=?");
			if (i < l.length - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	public static String generateFieldsValueUpdateFormatStr(Set<?> fields) {
		StringBuilder sb = new StringBuilder();
		Object l[] = fields.toArray();
		for (int i = 0; i < l.length; i++) {
			sb.append(l[i].toString()).append("=?");
			if (i < l.length - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	private static String SELECT_ALL_SQL_TEMP = "select #FIELDS from #TABLENAME ";

	public static String generateSelectAllSql(Set<?> fields, String tableName) {
		String key = tableName + "_select_all";
		String sql = (String) SQL_CACHER.get(key);
		if (sql == null) {
			sql = SELECT_ALL_SQL_TEMP;
			sql = sql.replaceAll("#FIELDS",
					generateFieldsFormatStr(fields, tableName));
			sql = sql.replaceAll("#TABLENAME", tableName);
		}
		return sql;
	}

	private static String UPDATE_BY_PK_SQL_TEMP = "update #TABLENAME set #FIELDS where #PK = ?";

	public static String generateUpdateByPKSql(Set<?> fields, String tableName,
			String pkFiledName, boolean autoKeyFlag, String autoKeyFiledName) {
		String key = tableName + "_update";
		String sql = (String) SQL_CACHER.get(key);
		if (sql == null) {
			sql = UPDATE_BY_PK_SQL_TEMP;
			if (!autoKeyFlag) {
				sql = sql.replaceAll("#TABLENAME", tableName);
				sql = sql.replaceAll("#FIELDS",
						generateFieldsValueUpdateFormatStr(fields));
				sql = sql.replaceAll("#PK", pkFiledName);
			} else {
				sql = sql.replaceAll("#TABLENAME", tableName);
				sql = sql.replaceAll(
						"#FIELDS",
						generateFieldsValueUpdateFormatStr(fields,
								autoKeyFiledName));
				sql = sql.replaceAll("#PK", pkFiledName);
			}
		}
		return sql;
	}

	private static String DELETE_BY_PK_SQL_TEMP = "delete from #TABLENAME where #PK = ?";

	public static String generateDeleteByPKSql(String tableName,
			String pkFiledName) {
		String key = tableName + "_delete";
		String sql = (String) SQL_CACHER.get(key);
		if (sql == null) {
			sql = DELETE_BY_PK_SQL_TEMP;
			sql = sql.replaceAll("#TABLENAME", tableName);
			sql = sql.replaceAll("#PK", pkFiledName);
		}
		return sql;
	}

	private static String SELECT_BY_PK_SQL_TEMP = "select #FIELDS from #TABLENAME where #PK = ?";

	public static String generateSelectByPKSql(Set<?> fields, String tableName,
			String pkFiledName) {
		String key = tableName + "_select_pk";
		String sql = (String) SQL_CACHER.get(key);
		if (sql == null) {
			sql = SELECT_BY_PK_SQL_TEMP;
			sql = sql.replaceAll("#FIELDS", generateFieldsFormatStr(fields));
			sql = sql.replaceAll("#TABLENAME", tableName);
			sql = sql.replaceAll("#PK", pkFiledName);
		}
		return sql;
	}

	private static String replaceX(String sql, String key) {
		int i = sql.indexOf(key);
		int j = sql.lastIndexOf(key);
		boolean lf = false;
		int k = j - 1;
		if (k >= 0) {
			char f = sql.charAt(k);
			if (f == ' ') {
				lf = true;
			}
		}
		if (i == 0) {
			sql = sql.replaceFirst(key + ", ", "");
		} else if (lf) {
			sql = sql.replaceAll(", " + key, "");
		} else if (i > 0) {
			sql = sql.replaceAll(" " + key + ", ", "");
		}
		return sql;
	}

	private static String INSERT_SQL_TEMP = "insert into #TABLENAME (#FIELDS) values (#VALUES)";

	public static String generateInsertSql(Set<?> fields, String tableName,
			boolean autoKeyFlag, String autoKeyFiledName) {
		String key = tableName + "_insert";
		String sql = (String) SQL_CACHER.get(key);
		if (sql == null) {
			sql = INSERT_SQL_TEMP;
			if (autoKeyFlag) {
				String fs = generateFieldsFormatStr(fields);
				int i = fs.indexOf(autoKeyFiledName);
				if (i < 0) {
					throw new com.beetle.framework.AppRuntimeException("表不存在此["
							+ autoKeyFiledName + "]字段，请检查字段名称是否正确，区分大小写");
				} else {
					// fs = fs.replaceAll(autoKeyFiledName + ", ", "");
					fs = replaceX(fs, autoKeyFiledName);
					String fv = generateFieldsValueFormatStr(fields);
					fv = fv.substring(2);
					sql = sql.replaceAll("#TABLENAME", tableName);
					sql = sql.replaceAll("#FIELDS", fs);
					sql = sql.replaceAll("#VALUES", fv);
				}
			} else {
				sql = sql.replaceAll("#TABLENAME", tableName);
				sql = sql
						.replaceAll("#FIELDS", generateFieldsFormatStr(fields));
				sql = sql.replaceAll("#VALUES",
						generateFieldsValueFormatStr(fields));
			}
			SQL_CACHER.put(key, sql);
		}
		return sql;
	}
}
