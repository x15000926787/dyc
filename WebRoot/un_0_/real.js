var url="ws://"+ip_addr+":"+port+"/LF/chatSocket?username=dd&gkey=un_0_&uid=4";
console.log(url);
//var url="ws://192.168.3.203:8080/qs/chatSocket?username="+usr;
window.onload=connect;
function connect(){
    if ('WebSocket' in window) {
        ws = new WebSocket(url);
    } else if ('MozWebSocket' in window) {
        ws = new MozWebSocket(url);
    } else {
        alert('WebSocket is not supported by this browser.');
        return;
    }
    //
    ws.onerror = function () {
        setMessageInnerHTML("WebSocket连接发生错误");
    };
    //连接成功建立的回调方法
    ws.onopen = function () {
        setMessageInnerHTML("WebSocket连接成功");
    }
    //接收到消息的回调方法
    ws.onmessage = function (event) {
        //将接收到的二进制数据转为字符串
        var unit8Arr = new Uint8Array(event.data) ;
        setMessageInnerHTML(byteToString(unit8Arr));
    }
    //连接关闭的回调方法
    ws.onclose = function () {
        setMessageInnerHTML("WebSocket连接关闭");
    }
    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {

        $.post("http://"+ip_addr+":"+port+"/LF/LogoutServlet", function (result) {
            console.log(result);
        });
        closeWebSocket();
    }

    function setMessageInnerHTML(innerHTML) {
        // alert( innerHTML);
    }
    //关闭WebSocket连接
    function closeWebSocket() {
        ws.close();
    }





    ws.onmessage=function(event){
        // console.log(event.data);
        var results=JSON.parse(event.data);
        console.log(results);




        //var yxrd=results.edd;
        //console.log(ycrd);
        //localStorage.clear();
        if (results)
        {
            for (var key in results)
            {

                localStorage.setItem(key, results[key]);
                //console.log(ycrd[i]+"-----"+ycrd[i+1]);
            }


        }else
        {
            //ycrd[0]=results.dev;
            // ycrd[1]=results.data;
            console.log("no data received!");
            //localStorage.setItem(results.dev, results.data);

        }


        //console.log(localStorage);
        /*
        if(result.alert!=undefined){
            $("#content").append(result.alert+"<br/>");
        }

        if(result.names!=undefined){
            $("#userList").html("");
            $(result.names).each(function(){
                $("#userList").append(this+"<br/>");
            });
        }

        if(result.from!=undefined){
            $("#content").append(result.from+" "+result.date+
                    " 说：<br/>"+result.sendMsg+"<br/>");
        }*/
        //for (i=0;i<100;i++)
        //$("#content").append(localStorage.getItem("data"+i)+"<br/>");

    };
}