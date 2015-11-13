package com.chanzor.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yzt.test.Utils;

public class TestSend extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		try {
			String account = request.getParameter("account");
			String password = request.getParameter("password");
			String mobile = request.getParameter("mobile");
			String content = request.getParameter("content");
//			String sendTime = request.getParameter("sendTime");
			String extno = request.getParameter("extno");
			
			String postData = "account="+account+"&password="+password+"&mobile="+mobile+"&content=您的验证码是"+content+"【柚子糖】&extno="+extno;
			String postUrl = "http://sms.chanzor.com:8001/sms.aspx?action=send";
			
			// 发送POST请求
			String result = Utils.post(postData, postUrl);

			System.out.println(result);
			PrintWriter writer = response.getWriter();
			writer.write(result);
			writer.flush();
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace(System.out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
