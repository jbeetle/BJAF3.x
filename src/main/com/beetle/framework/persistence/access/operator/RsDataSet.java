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
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 余浩东

 * @version 1.0
 */

import com.beetle.framework.util.ObjectUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class RsDataSet {
	public int rowCount;

	public int colCount;

	private List<Map<String, Object>> sqlResultSet; // 数据结果集

	private int pos = 0;

	private boolean clearFlag = false;

	private String columnNames[] = null;

	/**
	 * 自动填充查询一行记录，无需再使用getFieldValue方法逐一获取
	 * 
	 * @param valueObject
	 *            以记录对于的值对象（对象属性名词要和查询字段名称保持一致，大小写敏感）
	 */
	public void autoFillRow(Object valueObject) {
		String[] cnames = this.getColumnNames();
		if (cnames != null) {
			for (int i = 0; i < cnames.length; i++) {
				String key = cnames[i];
				Object value = this.getFieldValue(key);
				if (value == null) {
					continue;
				}
				try {
					ObjectUtil.setValue(key, valueObject, value);
				} catch (IllegalArgumentException ille) {
					value = null;
					Class<?> type = ObjectUtil.getType(key, valueObject);
					String tstr = type.toString();
					if (tstr.equals(Integer.class.toString())) {
						ObjectUtil.setValue(key, valueObject,
								this.getFieldValueAsInteger(key));
					} else if (tstr.equals(Long.class.toString())) {
						ObjectUtil.setValue(key, valueObject,
								this.getFieldValueAsLong(key));
					} else if (tstr.equals(Float.class.toString())) {
						ObjectUtil.setValue(key, valueObject,
								this.getFieldValueAsFloat(key));
					} else if (tstr.equals(Double.class.toString())) {
						ObjectUtil.setValue(key, valueObject,
								this.getFieldValueAsDouble(key));
					} else if (tstr.equals(Short.class.toString())) {
						ObjectUtil.setValue(key, valueObject,
								this.getFieldValueAsShort(key));
					} else if (tstr.equals(Byte.class.toString())) {
						ObjectUtil.setValue(key, valueObject,
								this.getFieldValueAsByte(key));
					}
					tstr = null;
					type = null;
				}
			}
		}
	}

	/*
	 * public void autoFillRow(Object valueObject) { String[] cnames =
	 * this.getColumnNames(); if (cnames != null) { BeanUtilsBean bu =
	 * BeanUtilsBean.getInstance(); for (int i = 0; i < cnames.length; i++) {
	 * String key = cnames[i]; Object value = this.getFieldValue(key); try { if
	 * (value == null) { continue; } bu.copyProperty(valueObject, key, value); }
	 * catch (Exception ex1) { throw new
	 * AppRuntimeException("对此对象装配解析出现问题,请检查对象属性是否和数据库字段一致", ex1); } } } }
	 */
	public RsDataSet(List<Map<String, Object>> sqlResultSet) {
		this.sqlResultSet = sqlResultSet;
		if (sqlResultSet == null) {
			this.rowCount = 0;
			this.colCount = 0;
		} else {
			this.rowCount = sqlResultSet.size();
			if (this.rowCount > 0) {
				// this.colCount = ((Map) sqlResultSet.get(0)).size();
				this.colCount = sqlResultSet.get(0).size();
			}
		}
	}

	public Time getFieldValueAsTime(int i) {
		Object o = getFieldValue(i);
		if (o == null) {
			return null;
		}

		if (o instanceof Time) {
			return (Time) o;
		} else {
			Date d = (Date) o;
			Time t = new Time(d.getTime());
			return t;
		}
	}

	public Time getFieldValueAsTime(String columnName) {
		Object o = getFieldValue(columnName);
		if (o == null) {
			return null;
		}

		if (o instanceof Time) {
			return (Time) o;
		} else {
			Date d = (Date) o;
			Time t = new Time(d.getTime());
			return t;
		}
	}

	public Timestamp getFieldValueAsTimestamp(int i) {
		Object o = getFieldValue(i);
		if (o == null) {
			return null;
		}

		if (o instanceof Timestamp) {
			return (Timestamp) o;
		} else {
			Date d = (Date) o;
			Timestamp t = new Timestamp(d.getTime());
			return t;
		}
	}

	public Timestamp getFieldValueAsTimestamp(String columnName) {
		Object o = getFieldValue(columnName);
		if (o == null) {
			return null;
		}

		if (o instanceof Timestamp) {
			return (Timestamp) o;
		} else {
			Date d = (Date) o;
			Timestamp t = new Timestamp(d.getTime());
			return t;
		}
	}

	public Date getFieldValueAsDate(int i) {
		Object o = getFieldValue(i);
		if (o == null) {
			return null;
		}

		if (o instanceof Date) {
			return (Date) o;
		} else {
			Timestamp t = (Timestamp) o;
			Date d = new Date(t.getTime());
			return d;
		}
	}

	public Date getFieldValueAsDate(String columnName) {
		Object o = getFieldValue(columnName);
		if (o == null) {
			return null;
		}

		if (o instanceof Date) {
			return (Date) o;
		} else {
			Timestamp t = (Timestamp) o;
			Date d = new Date(t.getTime());
			return d;
		}
	}

	public Float getFieldValueAsFloat(int i) {
		Object o = getFieldValue(i);
		if (o == null) {
			return null;
		}

		if (o instanceof Float) {
			return (Float) o;
		} else {
			return new Float(((BigDecimal) o).floatValue());
		}
	}

	public Float getFieldValueAsFloat(String columnName) {
		Object o = getFieldValue(columnName);
		if (o == null) {
			return null;
		}

		if (o instanceof Float) {
			return (Float) o;
		} else {
			return new Float(((BigDecimal) o).floatValue());
		}
	}

	public Double getFieldValueAsDouble(int i) {
		Object o = getFieldValue(i);
		if (o == null) {
			return null;
		}

		if (o instanceof Double) {
			return (Double) o;
		} else {
			return new Double(((BigDecimal) o).doubleValue());
		}
	}

	public Double getFieldValueAsDouble(String columnName) {
		Object o = getFieldValue(columnName);
		if (o == null) {
			return null;
		}

		if (o instanceof Double) {
			return (Double) o;
		} else {
			return new Double(((BigDecimal) o).doubleValue());
		}
	}

	public Byte getFieldValueAsByte(int i) {
		Object o = getFieldValue(i);
		if (o == null) {
			return null;
		}
		if (o instanceof Byte) {
			return (Byte) o;
		} else if (o instanceof Integer) {
			// return new Byte(((Integer) o).byteValue());
			return ((Integer) o).byteValue();
		} else {
			// return new Byte(((BigDecimal) o).byteValue());
			return ((BigDecimal) o).byteValue();
		}
	}

	public Byte getFieldValueAsByte(String columnName) {
		Object o = getFieldValue(columnName);
		if (o == null) {
			return null;
		}
		if (o instanceof Byte) {
			return (Byte) o;
		} else if (o instanceof Integer) {
			// return new Byte(((Integer) o).byteValue());
			return ((Integer) o).byteValue();
		} else {
			// return new Byte(((BigDecimal) o).byteValue());
			return ((BigDecimal) o).byteValue();
		}
	}

	public Short getFieldValueAsShort(int i) {
		Object o = getFieldValue(i);
		if (o == null) {
			return null;
		}
		if (o instanceof Short) {
			return (Short) o;
		} else if (o instanceof Integer) {
			// return new Short(((Integer) o).shortValue());
			return ((Integer) o).shortValue();
		} else {
			// return new Short(((BigDecimal) o).shortValue());
			return ((BigDecimal) o).shortValue();
		}
	}

	public Short getFieldValueAsShort(String columnName) {
		Object o = getFieldValue(columnName);
		if (o == null) {
			return null;
		}
		if (o instanceof Short) {
			return (Short) o;
		} else if (o instanceof Integer) {
			// return new Short(((Integer) o).shortValue());
			return ((Integer) o).shortValue();
		} else {
			// return new Short(((BigDecimal) o).shortValue());
			return ((BigDecimal) o).shortValue();
		}
	}

	public Long getFieldValueAsLong(int i) {
		Object o = getFieldValue(i);
		if (o == null) {
			return null;
		}

		if (o instanceof Long) {
			return (Long) o;
		} else {
			// return new Long(((BigDecimal) o).longValue());
			return ((BigDecimal) o).longValue();
		}
	}

	public BigDecimal getFieldValueAsBigDecimal(int i) {
		Object o = getFieldValue(i);
		if (o == null) {
			return null;
		}
		return (BigDecimal) o;
	}

	public BigDecimal getFieldValueAsBigDecimal(String columnName) {
		Object o = getFieldValue(columnName);
		if (o == null) {
			return null;
		}
		return (BigDecimal) o;
	}

	public Long getFieldValueAsLong(String columnName) {
		Object o = getFieldValue(columnName);
		if (o == null) {
			return null;
		}

		if (o instanceof Long) {
			return (Long) o;
		} else {
			// return new Long(((BigDecimal) o).longValue());
			return ((BigDecimal) o).longValue();
		}
	}

	public Integer getFieldValueAsInteger(int i) {
		Object o = getFieldValue(i);
		if (o == null) {
			return null;
		}

		if (o instanceof Integer) {
			return (Integer) o;
		} else {
			// return new Integer(((BigDecimal) o).intValue());
			return ((BigDecimal) o).intValue();
		}
	}

	public Integer getFieldValueAsInteger(String columnName) {
		Object o = getFieldValue(columnName);
		if (o == null) {
			return null;
		}

		if (o instanceof Integer) {
			return (Integer) o;
		} else {
			// return new Integer(((BigDecimal) o).intValue());
			return ((BigDecimal) o).intValue();
		}
	}

	/**
	 * 获取某一行记录值
	 * 
	 * @param rowNum
	 *            --行号
	 * @return Map(key==字段名称；value=字段值)
	 */
	public Map<String, Object> getRowValues(int rowNum) {
		return sqlResultSet.get(rowNum);
	}

	/**
	 * 按照字段名获取字段值
	 * 
	 * 
	 * @param columnName
	 *            字段名
	 * 
	 * @return Object
	 */
	public Object getFieldValue(String columnName) {
		return getFieldValueImp(columnName);
	}

	private Object getFieldValueImp(String columnName) {
		Object o = null;
		if (!sqlResultSet.isEmpty()) {
			if (pos >= 0 && pos < rowCount) {
				Map<?, ?> map = sqlResultSet.get(pos);
				o = map.get(columnName);
				if (o == null) {
					getColumnNames();
					for (int i = 0; i < columnNames.length; i++) {
						if (columnNames[i].equalsIgnoreCase(columnName)) {
							o = map.get(columnNames[i]);
							break;
						}
					}
				}
			}
		}
		return o;
	}

	/**
	 * 按列名顺序获取字段值
	 * 
	 * 
	 * @param index
	 *            int
	 * @return Object
	 */
	public Object getFieldValue(int index) {
		return getFieldVauleImp(index);
	}

	private Object getFieldVauleImp(int index) {
		Object o = null;
		if (!sqlResultSet.isEmpty()) {
			if (pos >= 0 && pos < rowCount) {
				Map<?, ?> map = sqlResultSet.get(pos);
				if (index >= 0 && index < colCount) {
					Iterator<?> it = map.values().iterator();
					int i = 0;
					while (it.hasNext()) {
						o = it.next();
						if (index == i) {
							break;
						} else {
							o = null;
						}
						i++;
					}
				}
			}
		}
		return o;
	}

	public Character getFieldValueAsChar(String columnName) {
		Object o = getFieldValue(columnName);
		if (o == null) {
			return null;
		}

		if (o instanceof Character) {
			return (Character) o;
		} else {
			return new Character(o.toString().charAt(0));
		}
	}

	public Character getFieldValueAsChar(int index) {
		Object o = getFieldValue(index);
		if (o == null) {
			return null;
		}
		if (o instanceof Character) {
			return (Character) o;
		} else {
			return new Character(o.toString().charAt(0));
		}
	}

	public String getFieldValueAsString(String columnName) {
		Object v = getFieldValue(columnName);
		if (v != null) {
			return v.toString();
		} else {
			return null;
		}
	}

	public String getFieldValueAsString(int index) {
		Object v = getFieldValue(index);
		if (v != null) {
			return v.toString();
		} else {
			return null;
		}
	}

	/**
	 * 获取结果集的列名
	 * 
	 * @return String[]
	 */
	public String[] getColumnNames() {
		if (columnNames != null) {
			return columnNames;
		}
		if (this.rowCount > 0) {
			Map<?, ?> map = sqlResultSet.get(0);
			Iterator<?> it = map.keySet().iterator();
			columnNames = new String[map.keySet().size()];
			int i = 0;
			while (it.hasNext()) {
				columnNames[i] = it.next().toString();
				i++;
			}
			return columnNames;
		} else {
			return null;
		}
	}

	/**
	 * 结果集指针移动到首位
	 */
	public void first() {
		pos = 0;
	}

	/**
	 * 结果集指针移动到最后一位
	 */
	public void last() {
		pos = rowCount - 1;
	}

	/**
	 * 结果集指针移动到下一位
	 */
	public void next() {
		if (pos >= 0 && pos < rowCount - 1) {
			pos = pos + 1;
		}
	}

	/**
	 * 结果集指针移动到前一位
	 */
	public void pre() {
		if (pos > 0 && pos < rowCount) {
			pos--;
		}
	}

	/**
	 * 结果集指针移动到指定的位置
	 * 
	 * 
	 * @param i
	 *            int
	 */
	public void move(int i) {
		if (i >= 0 && i < rowCount) {
			pos = i;
		}
	}

	/**
	 * 清除结果集 本方法为可选方法
	 */
	public void clearAll() {
		if (this.sqlResultSet != null && !this.sqlResultSet.isEmpty()) {
			this.sqlResultSet.clear();
			this.sqlResultSet = null;
		}
		if (this.columnNames != null) {
			com.beetle.framework.util.OtherUtil.clearArray(this.columnNames);
		}
		clearFlag = true;
	}

	protected void finalize() throws Throwable {
		if (!clearFlag) {
			clearAll();
		}
		super.finalize();
	}
}
