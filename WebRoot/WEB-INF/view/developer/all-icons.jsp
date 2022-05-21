<%@ page language="java" contentType="text/html; charset=UTF-8" import="lite.core.sitemesh.Decorators" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<title></title>
<META name="decorator" content="<%=Decorators.page %>">

<script src="<c:url value='../resource/script/fa-icons.js' />"></script>
<script src="<c:url value='../resource/script/glyphicon-icons.js' />"></script>

<style type="text/css">
	.my-icons {
		font-size: 24px;
		color: #2c7659
	}
	
	.my-icons > i {
		margin-right: 6px
	}
	
	.my-row {
		margin-bottom: 8px;
		border-bottom: 1px solid #E0E0E0;
	}
</style>

<script type="text/javascript">

var $icons = [];
var $containerBase;
var $icon_container;
var $filter;
var $filterClearButton;

$(document).ready(function(){
	
	$containerBase = $('<div class="row my-row"></div>');
	
	$icon_container = $('#icon-container');
	
	$filter = $('#filter');
	
	$filterClearButton = $('#filter-clear-btn');
	
	fa_icon.forEach((item) => {
		let strs = [];
		let dataIcon = item.split('-').filter((item, index) => index > 0).join('-');
		strs.push("<div data-icon='" + dataIcon + "' class='my-icons col-xs-2'>");
		strs.push("<i class='ace-icon ");
		strs.push(item);
		strs.push("'></i>");
		strs.push(item);
		strs.push("</div>");
		$icons.push($(strs.join('')));
	});
	
	glyphicon_icon.forEach((item) => {
		let strs = [];
		let dataIcon = item.split('-').filter((item, index) => index > 0).join('-');
		strs.push("<div data-icon='" + dataIcon + "' class='my-icons col-xs-2'>");
		strs.push("<i class='ace-icon glyphicon ");
		strs.push(item);
		strs.push("'></i>");
		strs.push("glyphicon ");
		strs.push(item);
		strs.push("</div>");
		$icons.push($(strs.join('')));
	});
	
	$filter.on('input', () => {
		renderIcons();
	});
	
	$filterClearButton.click(() => {
		$filter.val('');
		renderIcons();
	});
	
	renderIcons();
	
});

function renderIcons(){
	$( "[data-icon]" ).detach();
	$icon_container.empty();
	let $container = $containerBase.clone(false);
	let key = $filter.val();
	let count = 0;
	$icons.forEach((item) => {
		if(item[0].dataset.icon.toLowerCase().includes(key.toLowerCase())){
			if(count % 6 === 0){
				$icon_container.append($container);
				$container = $containerBase.clone(false);
			}
			$container.append(item);
			count++;
		}
	});
	if(count % 6 != 0){
		$icon_container.append($container);
	}
}

</script>
</head>
<content tag="page-body">

	<div>
		<input id="filter" type="text">
		<button type="button" id="filter-clear-btn">清除</button>
	</div>
	<br/>
	<div id="icon-container"></div>
	
</content>
</html>