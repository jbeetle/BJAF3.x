package com.beetle.component.security.dto;

public class SecRolesPermissions implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Long permissionid;
	private Long roleid;
	private java.sql.Timestamp createTime;

	public SecRolesPermissions() {
	}

	public Long getPermissionId() {
		return this.permissionid;
	}

	public Long getRoleId() {
		return this.roleid;
	}

	public void setPermissionId(Long permissionid) {
		this.permissionid = permissionid;
	}

	public void setRoleId(Long roleid) {
		this.roleid = roleid;
	}

	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "SecRolesPermissions [permissionid=" + permissionid + ", roleid=" + roleid + ", createTime=" + createTime
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
		result = prime * result + ((permissionid == null) ? 0 : permissionid.hashCode());
		result = prime * result + ((roleid == null) ? 0 : roleid.hashCode());
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
		SecRolesPermissions other = (SecRolesPermissions) obj;
		if (createTime == null) {
			if (other.createTime != null)
				return false;
		} else if (!createTime.equals(other.createTime))
			return false;
		if (permissionid == null) {
			if (other.permissionid != null)
				return false;
		} else if (!permissionid.equals(other.permissionid))
			return false;
		if (roleid == null) {
			if (other.roleid != null)
				return false;
		} else if (!roleid.equals(other.roleid))
			return false;
		return true;
	}

}