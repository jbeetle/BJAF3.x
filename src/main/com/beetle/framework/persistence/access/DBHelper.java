package com.beetle.framework.persistence.access;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DBHelper {
	/**
	 * ��ȡ��ݿ�ϵͳ���û�
	 * 
	 * @param conn
	 *            Connection
	 * @return String[]
	 * @throws SQLException
	 */
	public static String[] getDBUsers(Connection conn) throws SQLException {
		return getDBUsers(conn.getMetaData());
	}

	/**
	 * ��ȡ��ݿ�ϵͳ���û�
	 * 
	 * @param metaData
	 *            DatabaseMetaData
	 * @return String[]
	 * @throws SQLException
	 */
	public static String[] getDBUsers(DatabaseMetaData metaData)
			throws SQLException {
		ResultSet rs = metaData.getSchemas();
		List<String> users = new ArrayList<String>();
		try {
			while (rs.next()) {
				users.add(rs.getString("TABLE_SCHEM"));
			}
		} finally {
			rs.close();
		}
		String result[] = new String[users.size()];
		users.toArray(result);
		return result;
	}

	/**
	 * ��ȡ��ݿ�ϵͳ��������ݿ����
	 * 
	 * @param conn
	 *            ��ݿ�l��
	 * @return String[]
	 * @throws SQLException
	 */
	public static String[] getDatabases(Connection conn) throws SQLException {
		return getDatabases(conn.getMetaData());
	}

	/**
	 * ��ȡ��ݿ�ϵͳ����ƣ��磺sybase/oracle/sqlserver...��
	 * 
	 * @param conn
	 *            Connection
	 * @return String
	 * @throws SQLException
	 */
	public static String getDBMSName(Connection conn) throws SQLException {
		DatabaseMetaData dmd = conn.getMetaData();
		return dmd.getDatabaseProductName();
	}

	/**
	 * 
	 * 
	 * @param metaData
	 *            DatabaseMetaData
	 * @return String[]
	 * @throws SQLException
	 */
	public static String[] getDatabases(DatabaseMetaData metaData)
			throws SQLException {
		List<String> databases = new ArrayList<String>();
		ResultSet rs = metaData.getCatalogs();
		try {
			while (rs.next()) {
				databases.add(rs.getString("TABLE_CAT"));
			}
		} finally {
			rs.close();
		}
		String result[] = new String[databases.size()];
		databases.toArray(result);
		return result;
	}

	/**
	 * 
	 * 
	 * @param conn
	 *            Connection
	 * @param user
	 *            String
	 * @param dataBase
	 *            String
	 * @return String[]
	 * @throws SQLException
	 */
	public static String[] getDBTables(Connection conn, String user,
			String dataBase) throws SQLException {
		return getDBTables(conn.getMetaData(), dataBase, user);
	}

	/**
	 * 
	 * 
	 * @param metaData
	 *            DatabaseMetaData
	 * @param dataBase
	 *            String
	 * @param user
	 *            String
	 * @return String[]
	 * @throws SQLException
	 */
	public static String[] getDBTables(DatabaseMetaData metaData,
			String dataBase, String user) throws SQLException {
		ResultSet rs = metaData.getTables(dataBase, null, user,
				new String[] { "TABLE" });
		List<String> tables = new ArrayList<String>();
		try {
			while (rs.next()) {
				tables.add(rs.getString("TABLE_NAME"));
			}
		} finally {
			rs.close();
		}
		String result[] = new String[tables.size()];
		tables.toArray(result);
		return result;
	}

	public static Set<String> getTableFields(String tableName,
			Connection connection) throws SQLException {
		Set<String> columns = new HashSet<String>();
		ResultSet result = null;
		String table = null;
		String schema = null;
		try {
			result = connection.getMetaData().getColumns(null, null, tableName,
					null);
			while (result.next()) {
				if (result.getString(4) != null) {
					columns.add(result.getString(4));
				}
			}
			if (columns.isEmpty()) {
				result.close();
				int pIndex = tableName.lastIndexOf('.');
				if (pIndex > 0 && pIndex < (tableName.length() - 1)) {
					table = tableName.substring(pIndex + 1);
					schema = tableName.substring(0, pIndex);
					result = connection.getMetaData().getColumns(null, schema,
							table, null);
				}
				if (pIndex < 0) {
					tableName = tableName.toUpperCase();
					result = connection.getMetaData().getColumns(null, null,
							tableName, null);
				}
				if (!result.isClosed()) {
					while (result.next()) {
						if (result.getString(4) != null) {
							columns.add(result.getString(4));
						}
					}
				}
			}
		} finally {
			ConnectionFactory.closeAll(null, null, result);
		}
		return columns;
	}

	public static String getTablePrimaryKeyFieldName(String tableName,
			Connection connection) throws SQLException {
		String k = null;
		ResultSet result = null;
		String table = null;
		String schema = null;
		try {
			result = connection.getMetaData().getPrimaryKeys(null, null,
					tableName);
			if (result.next()) {
				k = result.getString(4);
			}
			if (k == null) {
				result.close();
				int pIndex = tableName.lastIndexOf('.');
				if (pIndex > 0 && pIndex < (tableName.length() - 1)) {
					table = tableName.substring(pIndex + 1);
					schema = tableName.substring(0, pIndex);
					result = connection.getMetaData().getPrimaryKeys(null,
							schema, table);
				}
				if (pIndex < 0) {
					tableName = tableName.toUpperCase();
					result = connection.getMetaData().getPrimaryKeys(null,
							null, tableName);
				}
				if (result.next()) {
					k = result.getString(4);
				}
			}
		} finally {
			ConnectionFactory.closeAll(null, null, result);
		}
		return k;
	}

	private static String[] protocols = new String[] { "jdbc", "postgresql" };

	public static Properties parsePostgreSqlURL(String url) throws SQLException {
		int state = -1;
		Properties urlProps = new Properties();

		String l_urlServer = url;
		String l_urlArgs = "";

		int l_qPos = url.indexOf(63);
		if (l_qPos != -1) {
			l_urlServer = url.substring(0, l_qPos);
			l_urlArgs = url.substring(l_qPos + 1);
		}

		int ipv6start = l_urlServer.indexOf("[");
		int ipv6end = l_urlServer.indexOf("]");
		String ipv6address = null;
		if ((ipv6start != -1) && (ipv6end > ipv6start)) {
			ipv6address = l_urlServer.substring(ipv6start + 1, ipv6end);
			l_urlServer = l_urlServer.substring(0, ipv6start) + "ipv6host"
					+ l_urlServer.substring(ipv6end + 1);
		}

		StringTokenizer st = new StringTokenizer(l_urlServer, ":/", true);

		for (int count = 0; st.hasMoreTokens(); count++) {
			String token = st.nextToken();

			if (count <= 3) {
				if ((count % 2 == 1) && (token.equals(":")))
					continue;
				if (count % 2 == 0) {
					boolean found = count == 0;
					for (int tmp = 0; tmp < protocols.length; tmp++) {
						if (!token.equals(protocols[tmp])) {
							continue;
						}
						if ((count != 2) || (tmp <= 0))
							continue;
						urlProps.setProperty("Protocol", token);
						found = true;
					}

					if (!found)
						return null;
				} else {
					return null;
				}
			} else {
				if (count <= 3)
					continue;
				if ((count == 4) && (token.equals("/"))) {
					state = 0;
				} else if (count == 4) {
					urlProps.setProperty("PGDBNAME", token);
					state = -2;
				} else if ((count == 5) && (state == 0) && (token.equals("/"))) {
					state = 1;
				} else {
					if ((count == 5) && (state == 0))
						return null;
					if ((count == 6) && (state == 1)) {
						urlProps.setProperty("PGHOST", token);
					} else if ((count == 7) && (token.equals(":"))) {
						state = 2;
					} else if ((count == 8) && (state == 2)) {
						try {
							Integer portNumber = Integer.decode(token);
							urlProps.setProperty("PGPORT",
									portNumber.toString());
						} catch (Exception e) {
							return null;
						}
					} else if (((count == 7) || (count == 9))
							&& ((state == 1) || (state == 2))
							&& (token.equals("/"))) {
						state = -1;
					} else {
						if (state != -1)
							continue;
						urlProps.setProperty("PGDBNAME", token);
						state = -2;
					}
				}
			}
		}

		if (ipv6address != null) {
			urlProps.setProperty("PGHOST", ipv6address);
		}

		StringTokenizer qst = new StringTokenizer(l_urlArgs, "&");
		for (int count = 0; qst.hasMoreTokens(); count++) {
			String token = qst.nextToken();
			int l_pos = token.indexOf(61);
			if (l_pos == -1) {
				urlProps.setProperty(token, "");
			} else {
				urlProps.setProperty(token.substring(0, l_pos),
						token.substring(l_pos + 1));
			}
		}
		return urlProps;
	}
}
