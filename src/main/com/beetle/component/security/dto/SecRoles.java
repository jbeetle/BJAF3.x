package com.beetle.component.security.dto;

public class SecRoles implements java.io.Serializable {

	public SecRoles(String role, String description, Integer available) {
		super();
		this.role = role;
		this.description = description;
		this.available = available;
	}

	private static final long serialVersionUID = 1L;
	private String role;
	private Long roleid;
	private Integer available;
	private String description;

	public SecRoles() {
	}

	public String getRole() {
		return this.role;
	}

	public Long getRoleId() {
		return this.roleid;
	}

	public Integer getAvailable() {
		return this.available;
	}

	public String getDescription() {
		return this.description;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setRoleId(Long roleid) {
		this.roleid = roleid;
	}

	public void setAvailable(Integer available) {
		this.available = available;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((available == null) ? 0 : available.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
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
		SecRoles other = (SecRoles) obj;
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
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (roleid == null) {
			if (other.roleid != null)
				return false;
		} else if (!roleid.equals(other.roleid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SecRoles [role=" + role + ", roleid=" + roleid + ", available=" + available + ", description="
				+ description + "]";
	}
	
}