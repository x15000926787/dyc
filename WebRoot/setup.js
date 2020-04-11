		var ip_addr = document.location.hostname;
		var port = "8080";
		var path = "dyc"
        var httpIp = "http://"+ip_addr+":"+ port+"/"+path+"/";
        var timeOutNum=100000;
		//httpIp = "http://192.168.101.114:8080";
		//项目号，对应prtu表的domain字段
		var prono = getCookie ('domain');
		var gkey = getCookie ('gkey');
		var leval = getCookie ('leval');
		var uid = getCookie ('uid');
		var uname = getCookie ('uname');
		//初始存档号，data_demo.html页面初始显示的信息点
		var saveno = -1;
		//初始站号，nylb.html一览表初始显示的站点
		var rtuno = 1;

		//0：开机，运行，1：越限，2：关机，告警,3:error,4:越限恢复
		 var color_arr = ['#31708f','#8a6d3b','#a94442','#843534','#006633'];
		var url1 = [
			httpIp + "data_demo.html",
			httpIp + "list.html",
			httpIp + "nupdown_edit.html",
			httpIp + "sjjl.html",
			"",
			httpIp + "authors.html",
			httpIp + "dig.html"
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