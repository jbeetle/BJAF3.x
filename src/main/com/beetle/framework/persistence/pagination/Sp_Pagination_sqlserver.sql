/*@querySql 查询语句, @pageNum 页数, @pageSize 每页记录条数 */ 
CREATE procedure Sp_Pagination 
  @querySql varchar(8000),
  @pageNum int,
  @pageSize int,
  @o_flag int output,
  @o_msg varchar(50) output
 as 
 begin
  set nocount on
  declare @p int,@nRowCount int
  exec sp_cursoropen @p output,@querySql,@scrollopt=2,@ccopt=335873,@rowcount=@nRowCount output
  if(@p!=0)
  begin
    set @pageNum=(@pageNum-1)*@pageSize+1
    exec sp_cursorfetch @p,32,@pageNum,@pageSize 
    exec sp_cursorclose @p
  end
  select @o_flag=1
  select @o_msg='ok'
end
GO