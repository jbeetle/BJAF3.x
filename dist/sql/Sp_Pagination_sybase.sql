 /*@querySql 查询语句, @pageNum 页数, @pageSize 每页记录条数,@tmpName 临时表名称 */
create procedure Sp_Pagination
       @querySql varchar(16384),
       @pageNum int,
       @pageSize int,
       @tmpName varchar(50),
       @o_flag int output,
       @o_msg varchar(50) output
      as
     begin
       declare @rcount int
       declare @execsql varchar(16384)
       declare @disflag int
       select @rcount=@pageNum*@pageSize
       set rowcount @rcount
       select @disflag=charindex('distinct',@querySql)
       if(@disflag=0)
       begin
         select @execsql = stuff(@querySql,charindex('select',@querySql),6,'select sybid=identity(12),')
       end
       else
       begin
     select @querySql = stuff(@querySql,charindex('distinct',@querySql),8,'')
        select @execsql = stuff(@querySql,charindex('select',@querySql),6,'select distinct sybid=identity(12),')
       end
       select @execsql = stuff(@execsql, charindex('from',@execsql),4,'into '||@tmpName||' from')
       select @execsql = @execsql || ' select * from '||@tmpName||'  where sybid>' || convert(varchar,(@pageNum-1)*@pageSize) || ' and sybid <= ' || convert(varchar,@pageNum*@pageSize)
       execute (@execsql)
       set rowcount 0
       select @o_flag=1
       select @o_msg='ok'
     end
