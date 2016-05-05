package beetle.generate.db;

import java.sql.Connection;

import beetle.generate.conf.Configuration;

import com.beetle.framework.persistence.access.ConnectionFactory;

public class DBConn {
	public static String dbType;
	// private String driver;
	// private String url;
	public static String user;
	public static String dbname;
	// private String password;
	public static String dsname;

	public DBConn() {
		Configuration cfg = Configuration.getInstance();
		this.dbType = cfg.getValue("db.dbType");
		// this.driver = cfg.getValue("db.driver");
		// this.url = cfg.getValue("db.url");
		this.user = cfg.getValue("db.user");
		this.dbname = cfg.getValue("db.dbname");
		this.dsname = cfg.getValue("db.dsname");
		// this.password = cfg.getValue("db.password");
	}

	public Connection getConnection() {
		/*
		 * Connection conn = null; try {
		 * Class.forName(this.driver).newInstance(); conn =
		 * DriverManager.getConnection(this.url, this.user, this.password);
		 * 
		 * } catch (java.lang.IllegalAccessException e) {
		 * System.out.println("�Ƿ�����" + e.toString()); } catch
		 * (java.lang.InstantiationException e) { System.out.println("��ʼ��ʧ��"
		 * + e.toString()); } catch (ClassNotFoundException e) {
		 * System.out.println("JDBCװ������ʧ��" + e.toString()); } catch
		 * (SQLException e) { System.out.println("������ݿ�ʧ��" + e.toString());
		 * } return conn;
		 */
		return ConnectionFactory.getConncetion(dsname);
	}

}
