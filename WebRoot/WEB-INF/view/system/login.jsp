<%@ page language="java" contentType="text/html; charset=UTF-8" import="lite.core.sitemesh.Decorators" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<title>LiteAce</title>
<META name="decorator" content="<%=Decorators.login %>">
<script type="text/javascript">

$(document).ready(function(){

	$(document).keydown(function(event) {
		
		if(event.which == 27){
			$('#name').val('');
			$('#pwd').val('');
		}
		
		if (event.which == 13) {
			doLogin();
		}
	});

});

function doLogin(){
	
	if(!$('#name').val() || !$('#pwd').val()){
		LiteAce.warning("帳號或密碼不允許空白！");
		return;
	}
	
	LiteAce.doPost({
		url : "doLogin",
		data : {
			name : $('#name').val().trim(),
			pwd : $('#pwd').val().trim()
		},
		success : function(data){
			if(data){
				location.reload();
			} else {
				LiteAce.warning("登入失敗");
			}			
		}
	});
}

</script>
</head>
<body>
			
	<div id="navbar" class="navbar navbar-default navbar-fixed-top">
		<div class="navbar-container " id="navbar-container">
			<div class="navbar-header pull-left">
				<a href="" class="navbar-brand">
					<small>
						<i class="fa fa-cloud"></i>
						LiteAce
					</small>
				</a>
			</div>
			<div id="top-objects-container"></div>
		</div>
	</div>
	
	<div class="main-container">
		<div class="main-content">
			<div class="row">
				<div class="col-sm-10 col-sm-offset-1">
					<div class="login-container">
						
						<div class="space-6"></div>
						<div class="space-6"></div>
			
						<div class="position-relative">
							<div id="login-box" class="login-box visible widget-box no-border" style="border-radius: 20px;">
								<div class="widget-body" style="border-radius: 20px;">
									<div class="widget-main" style="border-radius: 20px;">
										<h4 class="header blue lighter bigger">
											<i class="ace-icon fa fa-lightbulb-o"></i>
											歡迎使用 LiteAce 伺服器
										</h4>
										<div class="space-6"></div>
										<form>
											<fieldset>
												<label class="block clearfix">
													<span class="block input-icon input-icon-right">
														<input autocomplete="off" id="name" type="search" class="form-control" placeholder="Username" />
														<i class="ace-icon fa fa-user"></i>
													</span>
												</label>
			
												<label class="block clearfix">
													<span class="block input-icon input-icon-right">
														<input autocomplete="off" id="pwd" type="password" class="form-control" placeholder="Password" />
														<i class="ace-icon fa fa-lock"></i>
													</span>
												</label>
			
												<div class="space"></div>
			
												<div class="clearfix">
													<button onclick="doLogin()" type="button" class="width-35 pull-right btn btn-sm btn-primary">
														<i class="ace-icon fa fa-key"></i>
														<span class="bigger-110">Login</span>
													</button>
												</div>
			
												<div class="space-4"></div>
											</fieldset>
										</form>											
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
</body>

</html>