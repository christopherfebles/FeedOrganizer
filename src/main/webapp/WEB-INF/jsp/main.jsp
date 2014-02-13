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
		<link rel="stylesheet" type="text/css" href="css/main.css" />
		<title>Feed Organizer for Twitter</title>
	</head>
	<body>
		<form:form id="listForm" method="POST" action="main.do" onsubmit="return mainJS.onSubmit();">
			<select id="listDropdown" name="listDropdown">
				<option selected="selected" value="-1">Choose an Existing List</option>
			</select>
			<input type="submit" id="logoutButton" name="logout" value="Log Out"/>
			<input type="hidden" id="idList" name="idList"/>
			<input type="hidden" id="listId" name="listId" value="${listId}"/>
			<h2>People you follow on Twitter:</h2><br>
			<div id="allCardsDiv">
				<ul id="allFriendsList" class="connectedSortable listClass"></ul>
				<ul id="listMembersList" class="connectedSortable listClass"></ul>
			</div>
			<input type="submit" id="submitButton" name="save" value="Save Changes"/>
		</form:form>
		
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
		<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"></script>
		<script src="js/main.js"></script>
		<script type="text/javascript">
			jQuery(document).ready(function() {
				mainJS.onMainLoad();
			});			
		</script>
	</body>
</html>