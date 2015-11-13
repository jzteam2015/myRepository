<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>JSP Page</title>
</head>
<body>
	<%-- <h1>
            <%
            String PostData = "userid=&account=账号&password=密码&mobile=手机号&sendTime=&content="+java.net.URLEncoder.encode("短信内容","utf-8");
            //out.println(PostData);
            String ret = cn.umob.sms.Send.SMS(PostData, "http://sms.chanzor.com:8001/sms.aspx?action=send");
            out.println(ret);
            //请自己反序列化返回的字符串并实现自己的逻辑
        %> </h1> --%>
	<form action="${pageContext.request.contextPath }/TestSend"
		method="post">
		企业id:<input type="text" name="userid" value="【柚子糖】"/><br> 用户账号：<input
			type="text" name="account" value="zcs9680"/><br> 用户密码：<input type="text"
			name="password" value="153563"/><br> 被叫手机号：<input type="text" name="mobile" />（多个用半角逗号隔开）<br>
		发送内容：<input type="text" name="content" /><br> 定时发送：<input
			type="text" name="sendTime" />（为空表示立即发送，定时发送格式2010-10-24 09:08:10）<br>
		扩展号：<input type="text" name="extno" value="50010495"/>(设置为固定的:send)<br> <input
			type="submit" value="发送" />
	</form>


</body>
</html>
