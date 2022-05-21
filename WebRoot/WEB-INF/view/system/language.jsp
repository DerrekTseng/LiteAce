<%@ page language="java" contentType="text/html; charset=UTF-8" import="lite.core.sitemesh.Decorators" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<title></title>
<META name="decorator" content="<%=Decorators.page %>">
<script type="text/javascript">

var language = '${language}';

var defaultLanguage = '${defaultLanguage}';

var language_cookie_key = '${LANGUAGE_COOKIES_KEY}';

var selected;

$(document).ready(function(){
	
	selected = language || defaultLanguage;
	
	var $chosenselect = $('#chosen-select');
	
	LiteAce.doPost({
		url : 'getLanguages',
		success : function(data){
			
			Object.keys(data).forEach((key) => {
				let value = data[key];
				let option = [];
				option.push("<option ");
				option.push("value='");
				option.push(key);
				option.push("' ");
				if(selected === key){
					option.push("selected");
				}
				option.push(">");
				option.push(value);
				option.push("</option>");
				$chosenselect.append(option.join(''));
			});
		}
	});
	
	$('#select-btn').click(() => {
		setCookie(language_cookie_key, $chosenselect.val(), 365 * 10);
		LiteAce.closeDialog(true);
	});
	
});

function setCookie(cname, cvalue, exdays) {
	const d = new Date();
	d.setTime(d.getTime() + (exdays*24*60*60*1000));
	let expires = "expires="+ d.toUTCString();
	document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}


</script>
</head>
<content tag="page-body">
	
	<label for="chosen-select">Choose</label>
	<br/>
	<select id="chosen-select" style="width:260px" class="form-control"></select>
	<br/>
	<button id="select-btn" style="float:right" class="btn btn-sm btn-success">
		<i class="ace-icon fa fa-check"></i>
		Confirm
	</button>
	
</content>

</html>