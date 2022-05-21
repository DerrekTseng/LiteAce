<%@ page language="java" contentType="text/html; charset=UTF-8" import="lite.core.sitemesh.Decorators" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<title></title>
<META name="decorator" content="<%=Decorators.page %>">
<script type="text/javascript">

$(document).ready(function(){

});

</script>
</head>
<content tag="page-header">
	<h1>
		Email Templates <small> <i
			class="ace-icon fa fa-angle-double-right"></i> along with an email
			converter tool
		</small>
	</h1>
</content>

<content tag="page-body">

	<div class="row">
		<div class="col-xs-12">
			<!-- PAGE CONTENT BEGINS -->
			<div class="alert alert-block alert-info">
				<button type="button" class="close" data-dismiss="alert">
					<i class="ace-icon fa fa-times"></i>
				</button>
				The following sample email templates are converted using the
				provided email tool which converts normal Bootstrap HTML to email
				friendly table layout with inline CSS!
			</div>

			<div class="space-12"></div>

			<div class="row">
				<div class="col-xs-3">
					<a href="email-confirmation" class="thumbnail" target="_blank">
						<img class="img-responsive"
						src="<c:url value='/resource/assets/images/email/email1.png' />"
						alt="Email Template" />
					</a>
				</div>

				<div class="col-xs-3">
					<a href="email-navbar" class="thumbnail" target="_blank"> <img
						class="img-responsive"
						src="<c:url value='/resource/assets/images/email/email2.png' />"
						alt="Email Template" />
					</a>
				</div>

				<div class="col-xs-3">
					<a href="email-newsletter" class="thumbnail" target="_blank">
						<img class="img-responsive"
						src="<c:url value='/resource/assets/images/email/email3.png' />"
						alt="Email Template" />
					</a>
				</div>

				<div class="col-xs-3">
					<a href="email-contrast" class="thumbnail" target="_blank">
						<img class="img-responsive"
						src="<c:url value='/resource/assets/images/email/email4.png' />"
						alt="Email Template" />
					</a>
				</div>
			</div>

			<!-- PAGE CONTENT ENDS -->
		</div>
		<!-- /.col -->
	</div>
	<!-- /.row -->

</content>
</html>