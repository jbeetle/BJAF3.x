package com.beetle.framework.resource.define;

import java.util.ArrayList;
import java.util.List;

public class MasterDetailDTO implements java.io.Serializable {

	private static final long serialVersionUID = -6222298526435643713L;

	public MasterDetailDTO(Object master, List<?> detail) {
		super();
		this.master = master;
		this.detail = new ArrayList<Object>();
		this.detail.addAll(detail);
	}

	@Override
	public String toString() {
		return "MasterDetailDTO [master=" + master + ", detail=" + detail + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((detail == null) ? 0 : detail.hashCode());
		result = prime * result + ((master == null) ? 0 : master.hashCode());
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
		MasterDetailDTO other = (MasterDetailDTO) obj;
		if (detail == null) {
			if (other.detail != null)
				return false;
		} else if (!detail.equals(other.detail))
			return false;
		if (master == null) {
			if (other.master != null)
				return false;
		} else if (!master.equals(other.master))
			return false;
		return true;
	}

	private final Object master;
	private final List<Object> detail;

	public Object getMaster() {
		return master;
	}

	public List<Object> getDetail() {
		return detail;
	}

}
