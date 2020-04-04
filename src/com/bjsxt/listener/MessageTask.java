package com.bjsxt.listener;



import javax.servlet.ServletContext;
import javax.websocket.Session;

import com.google.gson.Gson;

import java.util.Random;
import java.util.TimerTask;

public class MessageTask extends TimerTask {
    private static boolean isRunning=false;
    private ServletContext context=null;

    public MessageTask(ServletContext context){
        this.context=context;
       
    }
    @Override
    public void run() {
        if (!isRunning)
        {
            isRunning=true;
            //任务执行中
          //  context.log("hello world");
           
            isRunning=false;
        }else {
            context.log("任务执行中");
        }

    }
}
