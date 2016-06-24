package com.beetle.component.accounting.persistence.imp;

import java.util.List;

import com.beetle.component.accounting.dto.Subject;
import com.beetle.component.accounting.persistence.SubjectDao;
import com.beetle.framework.persistence.access.operator.DBOperatorException;
import com.beetle.framework.persistence.access.operator.TableOperator;

public class SubjectImpl implements SubjectDao{

    //fieldsNames:[subjectNo,subjectDirect,remark,subjectType,subjectName"]
    private TableOperator<Subject> operator;

    public SubjectImpl(){
        operator = new TableOperator<Subject>("SYSDATASOURCE_DEFAULT", "subject", Subject.class);
     ////针对包含自增字段的表，使用下面的构造函数
        //operator = new TableOperator("SYSDATASOURCE_DEFAULT", "subject", Subject.class,"IDENTITY_FIELD_NAME");
    }

    public Subject get(String id) throws DBOperatorException{
		return operator.selectByPrimaryKey(id);
    }

    public List<Subject> getAll()throws DBOperatorException{
		return operator.selectByWhereCondition("", null);
     }

    public int insert(Subject subject) throws DBOperatorException {
		return operator.insert(subject);
    }
    public int update(Subject subject) throws DBOperatorException{
		return operator.update(subject);
    }
    public int delete(String id) throws DBOperatorException{
		return operator.deleteByPrimaryKey(id);
    }
/*
也可以使用QueryOperator查询器进行，例如根据主键查找此记录，实现如下：
（一般地，我们复杂查询操作使用QueryOperator比较方便）
    public Subject getSubject(String id)throws DBOperatorException {
        Subject subject = null;
        QueryOperator query = new QueryOperator();
        query.setDataSourceName("SYSDATASOURCE_DEFAULT");
        query.setSql("select "+operator.generateFieldsString()+" from "+operator.getTableName());
        query.addParameter(new SqlParameter(SqlType.VARCHAR, id));
            query.access();
            RsDataSet rs = new RsDataSet(query.getSqlResultSet());
            if (rs.rowCount > 0) {
                subject = new Subject();
                subject.setSubjectNo(rs.getFieldValueAsString("subjectNo"));
                subject.setSubjectDirect(rs.getFieldValueAsString("subjectDirect"));
                subject.setRemark(rs.getFieldValueAsString("remark"));
                subject.setSubjectType(rs.getFieldValueAsInteger("subjectType"));
                subject.setSubjectName(rs.getFieldValueAsString("subjectName"));
                rs.clearAll();
            }
        return subject;
    }

*/
}