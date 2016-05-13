package com.beetle.component.security.dto;

public class SecPermissions implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Long permissionid;
	private Integer available;
	private String description;
	private String permission;

	public SecPermissions() {
	}

	public SecPermissions(String permission, String description, Integer available) {
		super();
		this.permission = permission;
		this.description = description;
		this.available = available;
	}

	public Long getPermissionId() {
		return this.permissionid;
	}

	public Integer getAvailable() {
		return this.available;
	}

	public String getDescription() {
		return this.description;
	}

	public String getPermission() {
		return this.permission;
	}

	public void setPermissionId(Long permissionid) {
		this.permissionid = permissionid;
	}

	public void setAvailable(Integer available) {
		this.available = available;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@Override
	public String toString() {
		return "SecPermissions [permissionid=" + permissionid + ", available=" + available + ", description="
				+ description + ", permission=" + permission + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((available == null) ? 0 : available.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((permission == null) ? 0 : permission.hashCode());
		result = prime * result + ((permissionid == null) ? 0 : permissionid.hashCode());
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
		SecPermissions other = (SecPermissions) obj;
		if (available == null) {
			if (other.available != null)
				return false;
		} else if (!available.equals(other.available))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (permission == null) {
			if (other.permission != null)
				return false;
		} else if (!permission.equals(other.permission))
			return false;
		if (permissionid == null) {
			if (other.permissionid != null)
				return false;
		} else if (!permissionid.equals(other.permissionid))
			return false;
		return true;
	}
	
}