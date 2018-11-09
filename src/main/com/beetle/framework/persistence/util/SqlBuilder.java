package com.beetle.framework.persistence.util;

/**
 * SQL辅助生成器，提高Java编写sq语句的可读性，鼓励使用java编写sql而不是直接在写sql
 * 
 * <pre>
 * String sql = new SqlBuilder() {
 * 	{
 * 		SELECT(" * ");
 * 		FROM("company_labels t");
 * 		WHERE("t.loan_id=?");
 * 		WHERE("t.loan_test=?");
 * 		ORDER_BY("t.label_id DESC limit 1");
 * 	}
 * }.toString();
 * System.out.println(sql);
 * </pre>
 * 
 *
 */
public class SqlBuilder extends AbstractSQL<SqlBuilder> {
	public SqlBuilder getSelf() {
		return this;
	}
}