package demo.XXXApp.persistence.impl;

import com.beetle.framework.persistence.access.operator.UpdateOperator;

import demo.XXXApp.common.Const;
import demo.XXXApp.common.dto.Friend;
import demo.XXXApp.persistence.dao.IFriendDao;

public class PsFriend implements IFriendDao {

	@Override
	public int insert(Friend friend) {
		UpdateOperator update = new UpdateOperator();
		update.setDataSourceName(Const.SYSDATASOURCE_DEFAULT);
		update.setSql("INSERT INTO xx_friend(friendid, userid, friendname, phone, email, address)VALUES (?, ?, ?, ?, ?, ?)");
		update.addParameter(friend.getFriendid());
		update.addParameter(friend.getUserid());
		update.addParameter(friend.getFriendname());
		update.addParameter(friend.getPhone());
		update.addParameter(friend.getEmail());
		update.addParameter(friend.getAddress());
		update.access();
		return update.getEffectCounts();
	}
}
