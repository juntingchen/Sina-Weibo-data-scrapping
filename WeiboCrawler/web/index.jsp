<%@ page contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="weibo4j.*"%>
<%@ page language="java" import="weibo4j.http.*"%>
<%@ page language="java" import="java.util.*" isErrorPage="true"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Apps on Weibo</title>
</head>

<%
	if (request.getParameter("signed_request") != null) {
		String sign_request = request.getParameter("signed_request");
		Weibo wb = new Weibo();
		Oauth om = new Oauth();
		Timeline tm = new Timeline();
		String access_token = om.parseSignedRequest(sign_request);
		if ("".equals(access_token) || access_token == null) {
			response.sendRedirect("/t/auth.jsp");
		} else {
			session.setAttribute("access_token", access_token);
			wb.setToken(access_token);
			List<Status> list = tm.getHomeTimeline();
			for (Status s : list) {
%>
<div style="padding: 10px; margin: 5px; border: 1px solid #ccc">
	<%
		out.println(s.getUser().getScreenName() + ":"
							+ s.getText());
	%>
</div>
<%
	}
		}
	}
%>
</body>
</html>
