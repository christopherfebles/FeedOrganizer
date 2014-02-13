<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Feed Organizer for Twitter</title>
	</head>
	<body>
		<form:form modelAttribute="loginUser" name="loginForm" action="login.do" method="POST">
				<table width="100%" height="99%"  border="0" cellpadding="0" cellspacing="0" align="center">
					<tr>
						<td colspan="2" align="center">
							<%-- Image Could go here --%>
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<div class="loginTitle">FeedOrganizer</div> <br><br>
						</td>
					</tr>
					<tr>
						<td align="right" width="50%">
							<%-- <div class="loginText">Twitter Screen Name:</div> --%>
							<%-- <div class="loginText">Password:</div> --%>
						</td>
						<td align="left" width="50%">
							<%-- <form:input path="screenName" size="30"/> 
							<form:errors path="screenName" cssClass="error"/> <br>
						 	<form:password path="password" size="30"/> 
							<form:errors path="password" cssClass="error"/> <br> --%>
						</td>
					</tr>
					<tr>
						<td colspan="2" align="center">
							<br><br>
							<input type="submit" value="Login via Twitter"/> <br>
						</td>
					</tr>
			
				</table>
			</form:form>
	</body>
</html>