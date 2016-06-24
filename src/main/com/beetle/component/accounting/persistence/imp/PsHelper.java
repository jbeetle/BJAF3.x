package com.beetle.component.accounting.persistence.imp;

import com.beetle.framework.AppProperties;
import com.beetle.framework.persistence.access.operator.QueryOperator;
import com.beetle.framework.persistence.access.operator.UpdateOperator;

public class PsHelper {
	public static String DATASOURCE = "";
	static {
		DATASOURCE = AppProperties.get("accounting_datasource", "SYSDATASOURCE_DEFAULT");
	}

	public static QueryOperator createQueryOperator(final String sql) {
		QueryOperator q = new QueryOperator();
		q.setDataSourceName(DATASOURCE);
		q.setSql(sql);
		return q;
	}

	public static UpdateOperator createUpdateOperator(final String sql) {
		UpdateOperator u = new UpdateOperator();
		u.setDataSourceName(DATASOURCE);
		u.setSql(sql);
		return u;
	}
}
