<%@ page language="java" contentType="text/html; charset=UTF-8" import="lite.core.sitemesh.Decorators" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<META name="decorator" content="<%=Decorators.index %>">
<title>LiteAce</title>
<script type="text/javascript">

var $mainPageIframe;

$(document).ready(function(){
	
	$mainPageIframe = $('#main-page-iframe');
	
	registerWindowResize();
	
	getMenu(() => {
		LiteAce.ws.open(); // 整個前端畫面只需要 open 一次
		
		LiteAce.ws.addReceiver("index/notifications", (data) => {
			renderNotifications(data);
		});
		
		LiteAce.ws.addReceiver("index/alert", (data) => {
			console.log(data);
			LiteAce.alert({
				title : data.title,
				text : data.text
			});
		});
		
		LiteAce.ws.send("index/notifications");
		LiteAce.ws.send("index/alert");
	});
});

function getMenu(callback){
	LiteAce.doPost({
		url : "getMenu",
		success : function(data){
			let $target = $('ul[data-sidebar]', '#sidebar');
			renderMenu($target, data);
			callback(); // 避免 renderMenu 開啟第一個功能畫面時 關閉 ws 開啟的 alert
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

function renderNotifications(data){
	let $notifications = $('#notifications');
	
	data.forEach((item) => {
		let $compenent = $('li[data-notification-item]', '.lite-template').clone(false);
		
		$('[data-notification-icon]', $compenent).addClass(item.icon);
		$('[data-notification-msg]', $compenent).html(item.msg);
		
		$compenent.click(() =>{
			readNotification(item.rowid);
			if(item.url){
				LiteAce.dialog({
					url : item.url,
					data : item.data
				});
			}
		});
		
		$notifications.append($compenent);
	});
	
	$('#notifications-size').html(data.length);
	
	if(data.length > 0) {
		$('#notifications-component').show();
	} else {
		$('#notifications-component').hide();
	}
}

// 設定全部訊息為已讀
function readAllNotifications(){
	LiteAce.doPost({
		url : "readAllNotifications",
		success : function(data){
			$('#notifications').empty();
			LiteAce.ws.send("index/notifications");
		}
	});
}

//設定訊息為已讀
function readNotification(rowid){
	LiteAce.doPost({
		url : "readNotification",
		data : {
			rowid : rowid
		},
		success : function(data){
			$('#notifications').empty();
			LiteAce.ws.send("index/notifications");
		}
	});
}

function doLogout(){
	LiteAce.confirm({
		title : "登出確認",
		text : "請問是否要登出?",
		yes : function(){
			LiteAce.doPost({
				url : "doLogout",
				success : function(data){
					LiteAce.ws.close();
					location.reload();
				}
			});
		}	
	});
}

function openLanguage(){
	LiteAce.dialog({
		title : 'Choose Language',
		url : "openLanguage",
		disableSize: true,
		width : '300px',
		height : '200px',
		callback : function(changed){
			if(changed) {
				location.reload();
			}
		}
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
			
			<li data-notification-item>
				<div class="clearfix">
					<span class="pull-left">
						<i data-notification-icon class=""></i>
						<span data-notification-msg></span>
					</span>
				</div>
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
					${translang['application.title']}
				</span>
			</div>
			
			<div class="navbar-buttons navbar-header pull-right" role="navigation">
				<ul class="nav ace-nav">
					
					<%-- 通知 --%>
					<li id="notifications-component" class="green dropdown-modal" style="display:none">
						<a data-toggle="dropdown" class="dropdown-toggle clickable">
							<i class="ace-icon fa fa-bell icon-animated-bell"></i>
							<span id="notifications-size" class="badge badge-success">0</span>
						</a>

						<ul class="dropdown-menu-right dropdown-navbar dropdown-menu blue">
							<li class="dropdown-header">
								<i class="ace-icon fa fa-exclamation-triangle"></i> 通知
							</li>

							<li class="dropdown-content clickable">
							
								<ul id="notifications" class="dropdown-menu dropdown-navbar">
									
								</ul>
								
							</li>

							<li class="dropdown-footer">
								<a onclick="readAllNotifications()">
									已讀全部通知
								</a>
							</li>
						</ul>
					</li>
					
					<li class="blue dropdown-modal">
						<a class="dropdown-toggle none-select" onclick="openLanguage()">
							<i class="ace-icon glyphicon glyphicon-globe"></i>
						</a>
					</li>
					
					<%-- 登入者 --%>
					<li class="light-blue dropdown-modal">
						<a data-toggle="dropdown" class="dropdown-toggle">
							<span class="user-info">
								<small>${translang['index.welcome']},</small>
								${user}
							</span>

							<i class="ace-icon fa fa-caret-down"></i>
						</a>

						<ul class="user-menu dropdown-menu-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close clickable">
							<li>
								<a onclick="doLogout()">
									<i class="ace-icon fa fa-power-off"></i>
									Logout
								</a>
							</li>
						</ul>
					</li>
				
				</ul>
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