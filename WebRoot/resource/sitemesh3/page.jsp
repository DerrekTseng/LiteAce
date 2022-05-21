<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<title>
		<sitemesh:write property="title" />
	</title>
	
	<%@include file="/resource/jsp/PAGE_HEAD.jsp" %>
	<%@include file="/resource/jsp/WebSocket.jsp" %>
	
	<sitemesh:write property="head" />

	<script>
		$(document).ready(function(){
			if( $('.footer-content').html().trim().length == 0 ){
				$('.footer').remove();
			}
			if( $('.page-header').html().trim().length == 0 ){
				$('.page-header').remove();
			}
		});
		
		function getPageTemplate(selector){
			if(selector){
				return $(selector, ".lite-template");
			} else {
				return null;
			}
		}
	</script>
	 
</head>

<body class="no-skin">

	<div class="lite-template">
		<sitemesh:write property="page.page-template" />
	</div>

	<div class="main-container" id="main-container">
	
		<sitemesh:write property="page.pre-main-content" />
		
		<div class="main-content responsive">
			
			<sitemesh:write property="page.pre-page-content" />
			
			<div class="page-content">
				<div class="page-header">
					<sitemesh:write property="page.page-header" />		
				</div>
				
				<sitemesh:write property="page.page-body" />
				
			</div>
		</div>
		
		<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
			<i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
		</a>
		
	</div>
	
	<div class="footer">
		<div class="footer-inner">
			<div class="footer-content" style="border-top: none;">
				<sitemesh:write property="page.page-footer" />
			</div>
		</div>
	</div>

</body>
<%@include file="/resource/jsp/PAGE_TAIL.jsp" %>

</html>