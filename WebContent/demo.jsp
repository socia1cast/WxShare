<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/common/base.jsp"%>
<!DOCTYPE html >
<html>
<script type="text/javascript">
	var contextPath = '${contextPath}';
	var staticPath = '${static1Path}';
</script>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="format-detection" content="telephone=no">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	</head>
	<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
	<body>
		
	</body>
	<script type="text/javascript">
			$.ajax({
				type:"GET",
				data:{"url":window.location.href.split("#")[0]},
				url:"${contextPath}/phone_views/nvhl/share.json",
				success: function(response){
					response = $.parseJSON(response).result;
					wx.config({
						  debug: 	  false,
						  appId:      "wxf7e22861102fe8fd",
						  timestamp:  response.timestamp+"",
						  nonceStr:   response.nonceStr+"",
						  signature:  response.signature+"",
						  jsApiList: [
							'checkJsApi',
						    'onMenuShareTimeline',
						    'onMenuShareAppMessage'
						  ]
					});
				},
				error:function(vdata){
	            	 alert("请重新刷新页面！");
	            }
			});
		</script>
</html>