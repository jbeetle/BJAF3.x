package com.beetle.component.security.dto;

public class SecUsersRoles implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Long roleid;
	private Long userid;
	private java.sql.Timestamp createTime;

	public SecUsersRoles() {
	}

	public Long getRoleId() {
		return this.roleid;
	}

	public Long getUserId() {
		return this.userid;
	}

	public void setRoleId(Long roleid) {
		this.roleid = roleid;
	}

	public void setUserId(Long userid) {
		this.userid = userid;
	}

	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "SecUsersRoles [roleid=" + roleid + ", userid=" + userid + ", createTime=" + createTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((roleid == null) ? 0 : roleid.hashCode());
		result = prime * result + ((userid == null) ? 0 : userid.hashCode());
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
		SecUsersRoles other = (SecUsersRoles) obj;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (roleid == null) {
			if (other.roleid != null)
				return false;
		} else if (!roleid.equals(other.roleid))
			return false;
		if (userid == null) {
			if (other.userid != null)
				return false;
		} else if (!userid.equals(other.userid))
			return false;
		return true;
	}

}