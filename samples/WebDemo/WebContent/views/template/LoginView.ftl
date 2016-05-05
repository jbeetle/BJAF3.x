<html>
<head>
  <title>freemarker模板视图</title>
<link href="../views/t-1.css" rel="stylesheet" type="text/css">
</head>
<body>
<CENTER><B>#使用freemarker模板视图</B></CENTER><BR>
<table width="75%" border="1" align="center">
  <tr>
    <td width="20%">登陆用户名：</td>
    <td width="80%">${Login_Info.loginUser}</td>
  </tr>
  <tr>
    <td>密码：</td>
    <td>${Login_Info.password}</td>
  </tr>
  <tr>
    <td>登陆时间：</td>
    <td>${Login_Info.loginTime?string("yyyy-MM-dd HH:mm:ss zzzz")}</td>
  </tr>
</table>
<p align="center"><a href="../index.html">返回</a></p>
</body>
</html>