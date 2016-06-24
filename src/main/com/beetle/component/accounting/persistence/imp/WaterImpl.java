package com.beetle.component.accounting.persistence.imp;

import java.util.List;

import com.beetle.component.accounting.dto.Water;
import com.beetle.component.accounting.persistence.WaterDao;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.TableOperator;

public class WaterImpl implements WaterDao{

    //fieldsNames:[accountId,amount,orderNo,subjectNo,directFlag,createTime,accountNo,foreBalance,aftBalance,waterId"]
    private TableOperator<Water> operator;

    public WaterImpl(){
        operator = new TableOperator<Water>("SYSDATASOURCE_DEFAULT", "water", Water.class);
     ////针对包含自增字段的表，使用下面的构造函数
        //operator = new TableOperator("SYSDATASOURCE_DEFAULT", "water", Water.class,"IDENTITY_FIELD_NAME");
    }

    public Water get(Long id) throws DBOperatorException{
		return operator.selectByPrimaryKey(id);
    }

    public List<Water> getAll()throws DBOperatorException{
		return operator.selectByWhereCondition("", null);
     }

    public int insert(Water water) throws DBOperatorException {
		return operator.insert(water);
    }
    public int update(Water water) throws DBOperatorException{
		return operator.update(water);
    }
    public int delete(Long id) throws DBOperatorException{
		return operator.deleteByPrimaryKey(id);
    }
/*
也可以使用QueryOperator查询器进行，例如根据主键查找此记录，实现如下：
（一般地，我们复杂查询操作使用QueryOperator比较方便）
    public Water getWater(Long id)throws DBOperatorException {
        Water water = null;
        QueryOperator query = new QueryOperator();
        query.setDataSourceName("SYSDATASOURCE_DEFAULT");
        query.setSql("select "+operator.generateFieldsString()+" from "+operator.getTableName());
        query.addParameter(new SqlParameter(SqlType.BIGINT, id));
            query.access();
            RsDataSet rs = new RsDataSet(query.getSqlResultSet());
            if (rs.rowCount > 0) {
                water = new Water();
                water.setAccountId(rs.getFieldValueAsLong("accountId"));
                water.setAmount(rs.getFieldValueAsLong("amount"));
                water.setOrderNo(rs.getFieldValueAsString("orderNo"));
                water.setSubjectNo(rs.getFieldValueAsString("subjectNo"));
                water.setDirectFlag(rs.getFieldValueAsString("directFlag"));
                water.setCreateTime(rs.getFieldValueAsTimestamp("createTime"));
                water.setAccountNo(rs.getFieldValueAsString("accountNo"));
                water.setForeBalance(rs.getFieldValueAsLong("foreBalance"));
                water.setAftBalance(rs.getFieldValueAsLong("aftBalance"));
                water.setWaterId(rs.getFieldValueAsLong("waterId"));
                rs.clearAll();
            }
        return water;
    }

*/
}