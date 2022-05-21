<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta charset="utf-8" />
<meta name="description" content="overview &amp; stats" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />

<link rel="icon" href="<c:url value='/resource/icon/litecloud.ico' />" type="image/x-icon" />
<link rel="shortcut icon" href="<c:url value='/resource/icon/litecloud.ico' />" type="image/x-icon" />

<link rel="stylesheet" href="<c:url value='/resource/lib/merged-resources.min.css' />" />
<link rel="stylesheet" href="<c:url value='/resource/assets/css/ace.min.css' />" class="ace-main-stylesheet" id="main-ace-style" />

<script src="<c:url value='/resource/assets/js/jquery-2.2.4.min.js' />"></script>

<script>

	<%-- 給彈窗使用的，將元素 Append 到 Navbar --%>
	function appendToNavbar($e){	
		$('#top-objects-container').append($e.fadeIn(300));
		if(LiteAce._isFunction(top.hideMenu)){
			top.hideMenu();
		}		
	}
	
	<%-- 給彈窗使用的，讓指定元素過多少毫秒後自動關閉 --%>
	function removeElement($e, timeout = 1300){
		setTimeout(function(){	
			$e.remove();
		}, timeout);
	}
	
	<%-- 顯示左上角 Spinner --%>
	function showSpinner(){
		$('[data-index-icon]').hide();
		$('[data-index-spinner]').show();
	}
	
	<%-- 隱藏左上角 Spinner --%>
	function hideSpinner(){
		$('[data-index-spinner]').hide();
		$('[data-index-icon]').show();
	}
	
</script>

<%-- LiteService 核心，必須要在 LiteAce 前面 --%>
<script src="<c:url value='/resource/script/LiteService.js' />"></script>

<%-- LiteAce 核心 --%>
<script	src="<c:url value='/resource/script/LiteAce.js' />"></script>

<%-- LiteAce 擴充 --%>
<script	src="<c:url value='/resource/script/LiteDialog.js' />"></script>
<script	src="<c:url value='/resource/script/LitePopup.js' />"></script>
<script	src="<c:url value='/resource/script/LiteTable.js' />"></script>
<script	src="<c:url value='/resource/script/template.js' />"></script>
<link rel="stylesheet" href="<c:url value='/resource/style/template.css' />" />
