<%@ page language="java" contentType="text/html; charset=UTF-8" import="lite.core.sitemesh.Decorators" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<META name="decorator" content="<%=Decorators.develop %>">
<title>LiteAce</title>
<script type="text/javascript">

var $mainPageIframe;

$(document).ready(function(){
	
	$mainPageIframe = $('#main-page-iframe');
	
	registerWindowResize();
	
	getMenu();

});

function getMenu(){
	LiteAce.doPost({
		url : "developer/getMenu",
		success : function(data){
			let $target = $('ul[data-sidebar]', '#sidebar');
			renderMenu($target, data);
		}
	});
}

function renderMenu($target, data){
	data.forEach(function(item){
		if(item.menuType === 'GROUP'){
			let $compenent = $('li[data-menu-group]', '.lite-template').clone(false);
			$target.append($compenent);	
			$('i', $compenent).attr('class', item.icon);
			$('.menu-text', $compenent).html(item.name);
			$compenent.data("data", item);
			if(!$mainPageIframe.attr('src')){
				$compenent.addClass('open'); // 開啟第一個功能
			}
			renderMenu($('.submenu', $compenent), item.submenu);
		} else if(item.menuType === 'PAGE'){
			let $compenent = $('li[data-menu-item]', '.lite-template').clone(false);
			$target.append($compenent);	
			$('i', $compenent).attr('class', item.icon);
			$('.menu-text', $compenent).html(item.name);
			$compenent.data("data", item);
			$compenent.click(function(){
				setMenuActive($compenent);
				setPage(item.url);
				makeBreadcrumbs();
			});
			if(!$mainPageIframe.attr('src')){
				$compenent.click(); // 開啟第一個功能
			}
		} else if(item.menuType === 'LINK'){
			let $compenent = $('li[data-menu-item]', '.lite-template').clone(false);
			$target.append($compenent);	
			$('i', $compenent).attr('class', item.icon);
			$('.menu-text', $compenent).html(item.name);
			$compenent.data("data", item);
			$compenent.click(function(){
				window.open(item.url, '_blank');
			});
		}
	});
}

function setMenuActive($compenent){
	$('li', '#sidebar').removeClass('active');
	$('li', '#sidebar').removeClass('actived');
	$compenent.addClass('active');
	$compenent.addClass('actived');
	setMenuParentActive($compenent);
}

function setMenuParentActive($compenent){
	let $parent = $compenent.parent();
	if($parent.prop("tagName").toUpperCase() === 'li'.toUpperCase()){
		$parent.addClass('active');
		$parent.addClass('actived');
	}else if($parent.prop("tagName").toUpperCase() === 'body'.toUpperCase()){
		return;
	}
	if($parent.parent().prop("tagName")){
		setMenuParentActive($parent);
	}
}

function setPage(url){		
	LiteAce._closeAllDialog();
	if(url.startsWith("/")){
		url = url.substring(1);
	}	
	$mainPageIframe.attr('src', url);
}

function makeBreadcrumbs(){
	let $actived = $('.actived', '#sidebar');
	let $breadcrumb = $('.breadcrumb', '#breadcrumbs');
	$breadcrumb.empty();
	$actived.each(function(_index, item){
		let data = $(item).data('data');
		$breadcrumb.append('<li>' + data.name + '</li>');
	});
}

function registerWindowResize(){
	
	let $window = $(window);
	
	$mainPageIframe.css('height', ( $window.height() - 92 ) + 'px');
	
	$window.resize(function() {
		
		$mainPageIframe.css('height', ( $window.height() - 92 ) + 'px');
		
		LiteAce.$dialogs.forEach(function($dialog){
		
			let $content = $('[data-lite-ace-dialog-content]', $dialog);
			
			if($content.width() > $window.width()){
				$content.css({
					left : "0px",
					width : $window.width() + "px"
				});	
			}				
			if($content.height() > $window.height()){
				$content.css({
					top : "0px",
					height : $window.height() + "px"
				});	
			}
			
			if(parseInt($content.offset().left) + $content.width() > $window.width()){					
				$content.css({
					left : ( $window.width() - $content.width() ) / 2 + "px"
				});	
			}
			
			if(parseInt($content.offset().top) + $content.height() > $window.height()){				
				$content.css({
					top : ( $window.height() - $content.height() ) / 2 + "px"
				});	
			}
							
		});
	});
}

</script>

</head>

<body>

	<div class="lite-template">
		<ul>
			<!-- menu item -->
			<li data-menu-item class="">
				<a class="none-select clickable">
					<i class=""></i>
					<span class="menu-text"></span>
				</a>
				<b class="arrow"></b>
			</li>
			
			<!-- menu group -->
			<li data-menu-group class="">
				<a class="dropdown-toggle none-select clickable">
					<i class=""></i>
					<span class="menu-text"></span>
					<b class="arrow fa fa-angle-down"></b>
				</a>
				<b class="arrow"></b>
				<ul class="submenu"></ul>
			</li>
		</ul>
	</div>


	<div id="navbar" class="navbar navbar-default navbar-fixed-top none-select">
		<div class="navbar-container " id="navbar-container">
			<button type="button" class="navbar-toggle menu-toggler pull-left" id="menu-toggler" data-target="#sidebar">
				<span class="icon-bar"></span><span class="icon-bar"></span><span class="icon-bar"></span>
			</button>

			<div class="navbar-header pull-left">
				<span class="navbar-brand" style="font-size:22px">
					<i data-index-icon class="fa fa-leaf"></i>
					<i data-index-spinner class="fa fa-spinner fa-spin" style="display:none" ></i>
					Developer
				</span>
			</div>
			
		</div><!-- /.navbar-container -->
		
		<div id="top-objects-container"></div>
	
	</div>

	<div class="main-container " id="main-container">

		<div id="sidebar" class="sidebar sidebar-fixed responsive none-select">
			<ul data-sidebar class="nav nav-list"></ul>
			<div class="sidebar-toggle sidebar-collapse" id="sidebar-collapse">
				<i id="sidebar-toggle-icon" class="ace-icon fa fa-angle-double-left " data-icon1="ace-icon fa fa-angle-double-left" data-icon2="ace-icon fa fa-angle-double-right"></i>
			</div>
		</div>
	
		<div class="main-content">
			<div class="main-content-inner">
				<div class="breadcrumbs none-select" id="breadcrumbs">
					<ul class="breadcrumb"></ul>
				</div>
				<div class="page-content" style="padding:0px">
					<%-- 功能頁面 --%>		
					<iframe id="main-page-iframe" style="border: none; margin: 0px; width:100%;"></iframe>
				</div>
			</div>
		</div>
	
	</div>
	
</body>