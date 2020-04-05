<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>login4app</title>
    <script src="js/jquery.js"></script>
    <script type="text/javascript"  src="un_0_/setup.js"></script>
<script>
	var username = '<%=request.getParameter("username")%>';
	var password = '<%=request.getParameter("password")%>';

    console.log(username);
	//var url="ws://192.168.3.203:8080/qs/chatSocket?username="+usr;
	window.onload=connect;
	function connect(){
		$.ajax({
			url : httpIp + '/LF/LoginServlet',
			data :{"password":password,"username":username},
			type : "post",
			dataType: "json",
			success : function(data) {
				console.log(data);
				if (data){
					if (data.result == "1") { //判断返回值，
						setTimeout(function () {//做延时以便显示登录状态值
                            console.log("正在登录中...");

							window.location.href = "index.html";//指向登录的页面地址
						},100)
					} else {
                        console.log("登录失败");//显示登录失败
						return false;
					}
				}
			},
			error : function(data){
				showMsg(data.message);
			}
		});
	}
</script>
</head>
<body>
	


</body>
</html>