		var ip_addr = document.location.hostname;
		var port = "8081";
        var httpIp = "http://"+ip_addr+":"+ port;
        var timeOutNum=100000;
		//httpIp = "http://192.168.101.114:8080";
		//项目号，对应prtu表的domain字段
		var prono = 0;
		var gkey = 'un_0_';
		var leval = 1;
		var uid = 4;
		var uname = 'dd';
		//初始存档号，data_demo.html页面初始显示的信息点
		var saveno = -1;
		//初始站号，nylb.html一览表初始显示的站点
		var rtuno = 1;
		var urlArr = ["http://localhost:8080/Page0.htm",
		              "http://localhost:8080/Page1.htm",
		              "http://localhost:8081/qs/lsqx.html",
		              "http://localhost:8081/qs/lssj.html",
		              "http://localhost:8081/qs/hevt.html",
		              "http://localhost:8081/qs/xbfx.html", 
		              "http://localhost:8081/qs/bbxt.jsp"
		  ];
		 //  
		var url1 = [
			httpIp + "/LF/data_demo.html",
			httpIp + "/LF/list.html",
			httpIp + "/LF/nupdown.html",
			httpIp + "/LF/sjjl.html",
			"",
			httpIp + "/LF/authors.html"
	];
		function isNumber(val){

			if (parseFloat(val).toString() == "NaN") {

				return false;
			} else {
				return true;
			}

		}
		function isEmpty(obj){
			if(typeof obj == "undefined" || obj == null || obj == ""){
				return true;
			}else{
				return false;
			}
		}
		/**
		 * cookie中存值
		 * */
		function setCookie (name, value) {
			if (value) {
				var days = 1; //定义一天

				var exp = new Date();
				exp.setTime(exp.getTime() +  days*24*60 * 60 * 1000);
				//document.cookie = name + "=" + escape(value) + ";expires=" + exp.toUTCString();
				document.cookie = name + "=" + escape(value) + ";expires=" + exp.toUTCString() ;//+ ";path=/LF" + ";domain=localhost";
			}
		}
		/**
		 * cookie中取值
		 * */
		function getCookie (name) {
			var arr,reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)"); //匹配字段
			if (arr = document.cookie.match(reg)) {
				return unescape(arr[2]);
			} else {
				return null;
			}
		};

		/**
		 * 清除指定cookie值
		 * */
		function delCookie (name) {
			var exp = new Date();
			exp.setTime(exp.getTime() - 1);
			var cval = setCookie(name);
			if (cval && cval != null) {
				document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString()
			}
		};

		/**
		 * 清除全部cookie值
		 * */

		function clearCookie() {
			var keys = document.cookie.match(/[^ =;]+(?=\=)/g);
			if (keys) {
				for (var i = keys.length; i--;) {
// document.cookie = keys[i] +'=0;expires=' + new Date( 0).toUTCString()
					document.cookie = keys[i] +'=0;expires=' + new Date( 0).toUTCString() + ";path=/video_learning" + ";domain=localhost";
				}
			}
		};