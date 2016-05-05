package demo.valueobject;

import java.io.Serializable;

public class ExpUfRelation implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long friendid;
	private Long userid;

	public ExpUfRelation() {
	}

	public Long getFriendid() {
		return friendid;
	}

	public void setFriendid(Long friendid) {
		this.friendid = friendid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	@Override
	public String toString() {
		return "TestUfRelation [friendid=" + friendid + ", userid=" + userid
				+ "]";
	}

}