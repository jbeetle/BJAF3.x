package com.beetle.uidemo.persistence.imp;

import java.util.List;

import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.TableOperator;
import com.beetle.uidemo.persistence.dao.IXxFriendDao;
import com.beetle.uidemo.valueobject.XxFriend;

public class PsXxFriend implements IXxFriendDao{

    //fieldsNames:[friendid,phone,address,email,userid,friendname"]
    private TableOperator<XxFriend> operator;

    public PsXxFriend(){
        operator = new TableOperator<XxFriend>("SYSDATASOURCE_DEFAULT", "xx_friend", XxFriend.class);
     ////针对包含自增字段的表，使用下面的构造函数
        //operator = new TableOperator("SYSDATASOURCE_DEFAULT", "xx_friend", XxFriend.class,"IDENTITY_FIELD_NAME");
    }

    public XxFriend get(Long id) throws DBOperatorException{
		return operator.selectByPrimaryKey(id);
    }

    public List<XxFriend> getAll()throws DBOperatorException{
		return operator.selectByWhereCondition("", null);
     }

    public int insert(XxFriend xxfriend) throws DBOperatorException {
		return operator.insert(xxfriend);
    }
    public int update(XxFriend xxfriend) throws DBOperatorException{
		return operator.update(xxfriend);
    }
    public int delete(Long id) throws DBOperatorException{
		return operator.deleteByPrimaryKey(id);
    }
/*
也可以使用QueryOperator查询器进行，例如根据主键查找此记录，实现如下：
（一般地，我们复杂查询操作使用QueryOperator比较方便）
    public XxFriend getXxFriend(Long id)throws DBOperatorException {
        XxFriend xxfriend = null;
        QueryOperator query = new QueryOperator();
        query.setDataSourceName("SYSDATASOURCE_DEFAULT");
        query.setSql("select "+operator.generateFieldsString()+" from "+operator.getTableName());
        query.addParameter(new SqlParameter(SqlType.BIGINT, id));
            query.access();
            RsDataSet rs = new RsDataSet(query.getSqlResultSet());
            if (rs.rowCount > 0) {
                xxfriend = new XxFriend();
                xxfriend.setFriendid(rs.getFieldValueAsLong("friendid"));
                xxfriend.setPhone(rs.getFieldValueAsString("phone"));
                xxfriend.setAddress(rs.getFieldValueAsString("address"));
                xxfriend.setEmail(rs.getFieldValueAsString("email"));
                xxfriend.setUserid(rs.getFieldValueAsLong("userid"));
                xxfriend.setFriendname(rs.getFieldValueAsString("friendname"));
                rs.clearAll();
            }
        return xxfriend;
    }

*/
}