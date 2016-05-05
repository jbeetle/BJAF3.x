package com.beetle.framework.resource.define;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeDTO implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1268031237073784439L;
	private final Map<String, List<?>> datas;
	private int record;

	public CompositeDTO(List<?>... datalist) {
		this.datas = new HashMap<String, List<?>>();
		for (List<?> mylist : datalist) {
			String key = mylist.get(0).getClass().getName();
			datas.put(key, mylist);
			record = mylist.size();
		}
	}

	@Override
	public String toString() {
		return "CompositeDTO [datas=" + datas + ", record=" + record + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((datas == null) ? 0 : datas.hashCode());
		result = prime * result + record;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompositeDTO other = (CompositeDTO) obj;
		if (datas == null) {
			if (other.datas != null)
				return false;
		} else if (!datas.equals(other.datas))
			return false;
		if (record != other.record)
			return false;
		return true;
	}

	/**
	 * 结果集记录总数
	 * 
	 * @return
	 */
	public int recordAmount() {
		return this.record;
	}

	/**
	 * 结果数据列表个数
	 * 
	 * @return
	 */
	public int objListAmount() {
		return this.datas.size();
	}

	/**
	 * 根据对象定义的类获取其对应结果数据列表
	 * 
	 * @param objClass
	 * @return
	 */
	public <T> List<T> getObjList(Class<T> objClass) {
		String key = objClass.getName();
		if (datas.containsKey(key)) {
			@SuppressWarnings("unchecked")
			List<T> ll = (List<T>) datas.get(key);
			return ll;
		} else {
			return new ArrayList<T>();
		}
	}

	/**
	 * 释放资源
	 */
	public void clear() {
		this.datas.clear();
	}
}
