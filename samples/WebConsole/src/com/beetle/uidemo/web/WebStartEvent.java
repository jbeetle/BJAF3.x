package com.beetle.uidemo.web;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;

import com.beetle.uidemo.persistence.imp.PsXxUser;
import com.beetle.framework.AppProperties;
import com.beetle.framework.persistence.access.ConnectionFactory;

public class WebStartEvent implements com.beetle.framework.web.onoff.IStartUp {

	@Override
	public void startUp(ServletContext sc) {
		Reader r = new InputStreamReader(
				sc.getResourceAsStream("/WEB-INF/config/demodb.sql"));
		Connection conn = ConnectionFactory
				.getConncetion("SYSDATASOURCE_DEFAULT");
		try {
			org.h2.tools.RunScript.execute(conn, r);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionFactory.closeConnection(conn);
		}
	}

	// to test
	public static void main(String arg[]) {
		String db = AppProperties.getAppHome() + "demodb.sql";
		try {
			Reader r = new FileReader(db);
			Connection conn = ConnectionFactory
					.getConncetion("SYSDATASOURCE_DEFAULT");
			org.h2.tools.RunScript.execute(conn, r);
			// ConnectionFactory.closeConnection(conn);
			PsXxUser ps = new PsXxUser();
			System.out.println(ps.getAll());
			System.out.println(ps.getAll());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
