package beetle.generate.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import beetle.generate.conf.Configuration;
import beetle.generate.conf.Type;
import beetle.generate.util.Common;

import com.beetle.framework.persistence.access.DBHelper;

public class DBOperate {
	public DBOperate() {
	}

	public ArrayList getTables_bak() throws SQLException {
		DBConn dbConn = new DBConn();
		Connection conn = dbConn.getConnection();
		String tableNames[] = DBHelper.getDBTables(conn, DBConn.user,
				DBConn.dbname);
		ArrayList tables = new ArrayList();
		for (int i = 0; i < tableNames.length; i++) {
			tables.add(tableNames[i]);
			tableNames[i] = null;
		}
		return tables;
	}

	public ArrayList getTables() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList tables = new ArrayList();
		try {
			DBConn dbConn = new DBConn();
			conn = dbConn.getConnection();
			// stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
			// ResultSet.CONCUR_UPDATABLE);
			stmt = conn.createStatement();
			Configuration cfg = Configuration.getInstance();
			String dbType = cfg.getValue("db.dbType").toLowerCase();
			String sqlStr = null;
			if (dbType.equals("mysql")) {
				sqlStr = "show tables";
			} else if (dbType.equals("oracle")) {
				 sqlStr = "select * from tab";
				//sqlStr = "select * from tab where tname='EXP_USER'";
			} else if (dbType.equals("sybase")) {
				sqlStr = "select * from sysobjects where type='U'";
			} else if (dbType.equals("postgres")) {
				sqlStr = "SELECT tablename FROM pg_tables WHERE tablename NOT  LIKE 'pg%'  AND  tablename NOT  LIKE 'sql_%'  ORDER BY tablename";
			}

			rs = stmt.executeQuery(sqlStr);
			while (rs.next()) {
				// tables.add(rs.getString(1).toLowerCase());
				tables.add(rs.getString(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return tables;
	}

	public HashMap getTbFields(String tabName) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		java.sql.ResultSetMetaData rsmd = null;
		HashMap tbFields = new HashMap();
		Configuration cfg = Configuration.getInstance();
		Type type = Type.getInstance();
		tbFields.put("tabName", tabName);
		try {
			DBConn dbConn = new DBConn();
			conn = dbConn.getConnection();

			DatabaseMetaData dbMeta = conn.getMetaData();
			ResultSet pkRSet = dbMeta.getPrimaryKeys(DBConn.dbname, null,
					tabName);
			// System.out.println(tabName);
			if (pkRSet == null) {
				System.err.println("Error: pkRSet is null");
			}
			// System.err.println("TABLE_CAT  : " + pkRSet.getObject(1));
			if (pkRSet.next()) {
				// System.out.println("key:"+pkRSet.getString(4).toLowerCase());
				System.err.println("****** Comment ******");
				System.err.println("TABLE_CAT  : " + pkRSet.getObject(1));
				System.err.println("TABLE_SCHEM: " + pkRSet.getObject(2));
				System.err.println("TABLE_NAME : " + pkRSet.getObject(3));
				System.err.println("COLUMN_NAME: " + pkRSet.getObject(4));
				System.err.println("KEY_SEQ    : " + pkRSet.getObject(5));
				System.err.println("PK_NAME    : " + pkRSet.getObject(6));
				System.err.println("****** ******* ******");

				tbFields.put("primaryKey", pkRSet.getString(4));
			}
			// System.out.print(tabName+","+tbFields.get("primaryKey")+"| ");
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery("select * from " + tabName);
			rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if (tbFields.get("primaryKey") == null) {
					// System.out.print(tbFields.get("primaryKey")+"|");
					if ((rsmd.getColumnName(i)).indexOf("id") >= 0) {
						tbFields.put("primaryKey", rsmd.getColumnName(i));

					} else {
						if (i == rsmd.getColumnCount()) {
							tbFields.put("primaryKey", "no_primarykey_define");
						}
						// System.out.print(rsmd.getColumnName(i).toLowerCase()+", ");
					}
					// System.out.print(tbFields.get("primaryKey")+"|");
				}
				System.out.println(rsmd.getColumnName(i));
				System.out.println(rsmd.getColumnTypeName(i));
				System.out.println(rsmd.getColumnClassName(i));
				System.out.println(rsmd.getPrecision(i));
				System.out.println("Scale:+" + rsmd.getScale(i));
				System.out.println(rsmd.getColumnTypeName(i));
				System.out.println(type.getValue(rsmd.getColumnTypeName(i)));
				System.out.println("-------");
				if (cfg.getValue("java.dataType").equals("0")) {
					tbFields.put(rsmd.getColumnName(i),
							Common.CompressType(rsmd.getColumnClassName(i)));
				} else if (cfg.getValue("java.dataType").equals("1")) {
					// System.out.println(rsmd.getColumnName(i).toLowerCase());
					// System.out.println(rsmd.getColumnTypeName(i));
					// System.out.println(rsmd.getPrecision(i));
					// System.out.println("Scale:+"+rsmd.getScale(i));
					// System.out.println(type.getValue(rsmd.getColumnTypeName(i)));
					
					
					String coltype = rsmd.getColumnTypeName(i);
					if (coltype.equals("NUMBER")) {//for oracle
						int l1=rsmd.getPrecision(i);
						int l2=rsmd.getScale(i);
						if(l1>10&&l2==0){
							tbFields.put(rsmd.getColumnName(i),"Long");
						}else if(l1<=10&&l2==0){
							tbFields.put(rsmd.getColumnName(i),"Integer");
						}else if(l1>10&&l2>0){
							tbFields.put(rsmd.getColumnName(i),"Double");
						}else{
							tbFields.put(rsmd.getColumnName(i),
									type.getValue(rsmd.getColumnTypeName(i)));
						}
					}else{
						tbFields.put(rsmd.getColumnName(i),
								type.getValue(rsmd.getColumnTypeName(i)));
					}
					/*
					if (rsmd.getPrecision(i) >= 18) {
						tbFields.put(rsmd.getColumnName(i), "Long");
					} else {
						if (rsmd.getScale(i) > 0) {
							tbFields.put(rsmd.getColumnName(i), "Double");
						} else {
							tbFields.put(rsmd.getColumnName(i),
									type.getValue(rsmd.getColumnTypeName(i)));
						}
					}
					*/
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		// System.out.println("-----");
		// System.out.println(tbFields.get("primaryKey"));
		return tbFields;

	}

	public ArrayList getAllTbFields() {
		ArrayList allTbFields = new ArrayList();
		ArrayList tables = null;
		tables = getTables();
		for (int i = 0; i < tables.size(); i++) {
			allTbFields.add(getTbFields(tables.get(i).toString()));
		}
		return allTbFields;
	}

	public static void main(String[] args) throws SQLException {
		DBOperate dbOperate = new DBOperate();
		dbOperate.getTbFields("EXP_USER");
		// ArrayList allTbFields = dbOperate.getAllTbFields();
		/*
		 * ArrayList tbFields = new ArrayList(); for(int
		 * i=0;i<allTbFields.size();i++){ tbFields =
		 * (ArrayList)allTbFields.get(i); for(int j=0;j<tbFields.size();j++){
		 * System.out.print(tbFields.get(j)+", "); } System.out.println(""); }
		 */
		// Common.printContent(allTbFields);
	}

}
