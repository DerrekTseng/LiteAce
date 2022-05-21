<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<title>
		<sitemesh:write property="title" />
	</title>
	
	<%@include file="/resource/jsp/PAGE_HEAD.jsp" %>
	
	<sitemesh:write property="head" />
	 
</head>

<body class="no-skin">

	<sitemesh:write property="body" />
	
</body>

<%@include file="/resource/jsp/PAGE_TAIL.jsp" %>

</html>