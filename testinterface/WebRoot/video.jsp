<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'vedio.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  
  <body>
    <h1>视频上传第一步</h1>
    <form action="${pageContext.request.contextPath }/Create"
		method="post">
		视频标题：<input type="text" name="title" value=""/><br> 
		视频标签：<input type="text" name="tag" value="153563"/><br> 
		视频描述：<input type="text" name="description" /><br>
		二级分类id：<input type="text" name="categoryid" /><br> 
		<input type="submit" value="发送" />
	</form>
  </body>
</html>
