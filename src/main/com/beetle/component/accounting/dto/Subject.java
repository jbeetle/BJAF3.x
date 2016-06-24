package com.beetle.component.accounting.dto;

public class Subject implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String subjectno;
	private String subjectdirect;
	private String remark;
	private Integer subjecttype;
	private String subjectname;

	public Subject() {
	}

	public String getSubjectNo() {
		return this.subjectno;
	}

	public String getSubjectDirect() {
		return this.subjectdirect;
	}

	public String getRemark() {
		return this.remark;
	}

	public Integer getSubjectType() {
		return this.subjecttype;
	}

	public String getSubjectName() {
		return this.subjectname;
	}

	public void setSubjectNo(String subjectno) {
		this.subjectno = subjectno;
	}

	public void setSubjectDirect(String subjectdirect) {
		this.subjectdirect = subjectdirect;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setSubjectType(Integer subjecttype) {
		this.subjecttype = subjecttype;
	}

	public void setSubjectName(String subjectname) {
		this.subjectname = subjectname;
	}

	@Override
	public String toString() {
		return "Subject [subjectno=" + subjectno + ", subjectdirect=" + subjectdirect + ", remark=" + remark
				+ ", subjecttype=" + subjecttype + ", subjectname=" + subjectname + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((remark == null) ? 0 : remark.hashCode());
		result = prime * result + ((subjectdirect == null) ? 0 : subjectdirect.hashCode());
		result = prime * result + ((subjectname == null) ? 0 : subjectname.hashCode());
		result = prime * result + ((subjectno == null) ? 0 : subjectno.hashCode());
		result = prime * result + ((subjecttype == null) ? 0 : subjecttype.hashCode());
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
		Subject other = (Subject) obj;
		if (remark == null) {
			if (other.remark != null)
				return false;
		} else if (!remark.equals(other.remark))
			return false;
		if (subjectdirect == null) {
			if (other.subjectdirect != null)
				return false;
		} else if (!subjectdirect.equals(other.subjectdirect))
			return false;
		if (subjectname == null) {
			if (other.subjectname != null)
				return false;
		} else if (!subjectname.equals(other.subjectname))
			return false;
		if (subjectno == null) {
			if (other.subjectno != null)
				return false;
		} else if (!subjectno.equals(other.subjectno))
			return false;
		if (subjecttype == null) {
			if (other.subjecttype != null)
				return false;
		} else if (!subjecttype.equals(other.subjecttype))
			return false;
		return true;
	}

}