package com.dl.tool;

import com.alibaba.fastjson.JSONObject;
import com.bjsxt.server.ChatSocket;
import com.bjsxt.thread.ThreadSubscriber;
import com.dl.quartz.LuaJob;
import com.google.common.io.CharStreams;
import org.apache.logging.log4j.LogManager;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.Jedis;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Desc:系统初始化读取
 * Created by xx on 2019/12/24.
 */
public class AnaUtil  {
    
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(AnaUtil.class);
    public static JSONObject objana_v = new JSONObject();
    //public static JSONObject objana = new JSONObject();
    public static JSONObject msg_user = new JSONObject();
    public static JSONObject msg_author = new JSONObject();
    public static JSONObject objcondition = new JSONObject();
    public static JSONObject online_warn = new JSONObject();
    public static String[] ycEvtInfo ={"越下限","恢复","越上限"};
    public static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    public static ScriptEngine scriptEngine =null;// scriptEngineManager.getEngineByName("nashorn");
    private static final String projectName = "【"+PropertyUtil.getProperty("project_name")+"】";
    private static final int user_divide = Integer.parseInt(PropertyUtil.getProperty("user_divide","0"));           //默认用户不分组
    public static LocalDateTime rightnow = LocalDateTime.now();
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyy-MM-dd");
   /* static{
        loadAna(jdbcTemplate);
    }*/
    public AnaUtil(){
        scriptEngine = scriptEngineManager.getEngineByName("nashorn");
    }
    synchronized static public void loadAna_v(JdbcTemplate jdbcTemplate){
        logger.info("开始读取ana内容.......");

        LocalDateTime tt = null;//LocalDateTime.parse(map.get("checktime").toString(), formatter);

        int i,j,maxsav=0,maxdnsav=0;

        i=0;j=0;
        Map<String,Object> map =null;//new HashMap<>();// (HashMap)userData.get(i);
        String sql="select rtuno,sn,kkey,saveno,upperlimit,lowerlimit,pulsaveno,chgtime,deviceno,ldz,type,author_msg,author_email,author_alert,timevalid,timecondition,warnline,online,timestat,checktime,warntimes,maxv,maxt,minv,mint  from prtuana_v";

        //if(rand.equalsIgnoreCase(imagerand)){
        try{
            List<Map<String, Object>> userData = jdbcTemplate.queryForList(sql);
            int size=userData.size() ;
            if (size> 0)
            {

                for ( i = 0; i<size; i++)
                {

                    map = (HashMap) userData.get(i);

                    maxsav=(Integer.parseInt(map.get("saveno").toString())>maxsav?Integer.parseInt(map.get("saveno").toString()):maxsav);
                    try
                    {
                        maxdnsav=(Integer.parseInt(map.get("pulsaveno").toString())>maxdnsav?Integer.parseInt(map.get("pulsaveno").toString()):maxdnsav);
                    }
                    catch(Exception e)
                    {}
                    try {
                        tt = LocalDateTime.parse(map.get("maxt").toString(), formatter);
                    }catch(Exception e)
                    {
                         tt = LocalDateTime.parse("2020-01-01 00:00:00", formatter); ;
                    }

                    if (!rightnow.format(ymd).matches(tt.format(ymd)))
                    {
                        map.put("maxv","-99999999");
                        map.put("maxt",rightnow.format(formatter));
                    }
                    try {
                        tt = LocalDateTime.parse(map.get("minit").toString(), formatter);
                    }catch(Exception e)
                    {
                        tt = LocalDateTime.parse("2020-01-01 00:00:00", formatter); ;
                    }
                    if (!rightnow.format(ymd).matches(tt.format(ymd)))
                    {
                        map.put("minv","99999999");
                        map.put("mint",rightnow.format(formatter));
                    }
                    objana_v.put(map.get("kkey").toString(),map);

                }
                objana_v.put("maxsav",maxsav);
                objana_v.put("maxdnsav",maxdnsav);
            }

        }catch(Exception e){
            logger.warn("出错了"+e.toString());
            e.printStackTrace();
        }
        /*sql="select rtuno,sn,type ,kkey,chgtime,deviceno,author_msg,author_email,author_alert  from prtudig";
        //if(rand.equalsIgnoreCase(imagerand)){
        try{
            List<Map<String, Object>> userData = jdbcTemplate.queryForList(sql);

            int size=userData.size() ;
            if (size> 0)
            {

                for ( i = 0; i<size; i++)
                {

                    map = (HashMap) userData.get(i);


                    objana_v.put(map.get("kkey").toString(),map);

                }

            }
            // pstmt.close();
            //dbcon.getClose();
        }catch(Exception e){
            logger.error("出错了"+e.toString());
        }*/
        //logger.warn(objana);
        logger.warn(objana_v.toJSONString());
        logger.info("加载ana内容完成.");

    }



    /**
     * 读取yonghubiao
     * @param
     * @throws SQLException

     */

    synchronized static public void load_msguser(JdbcTemplate jdbcTemplate){

        String sql="select *  from msg_user";
        Map<String,Object> map =null;
        int i;
        logger.info("开始读取msg_user内容.......");
        try{
            List<Map<String, Object>> userData = jdbcTemplate.queryForList(sql);

            int size=userData.size() ;
            if (size> 0)
            {

                for ( i = 0; i<size; i++)
                {

                    map = (HashMap) userData.get(i);
                    if (msg_user.containsKey(map.get("gkey")))
                    {
                        ((JSONObject)msg_user.get(map.get("gkey"))).put(map.get("auth").toString(),map);
                    }
                    else
                    {
                        JSONObject user = new JSONObject();
                        user.put(map.get("auth").toString(),map);
                        msg_user.put(map.get("gkey").toString(),user);
                    }


                   // msg_user.put(map.get("id").toString(),map);

                }

            }
            // pstmt.close();
            //dbcon.getClose();
        }catch(Exception e){
            logger.error("出错了"+e.toString());
            e.printStackTrace();
        }
        logger.warn(msg_user);
        logger.info("加载msg_user内容完成.");
        //logger.warn(tobj);


    }
    /**
     * 读取权限表
     * @param
     * @throws SQLException

     */

    synchronized static public void load_author(JdbcTemplate jdbcTemplate){

        String sql="select *  from dev_author";
        Map<String,Object> map =null;
        int i;
        logger.info("开始读取dev_author内容.......");
        try{
            List<Map<String, Object>> userData = jdbcTemplate.queryForList(sql);

            int size=userData.size() ;
            if (size> 0)
            {

                for ( i = 0; i<size; i++)
                {

                    map = (HashMap) userData.get(i);


                    msg_author.put(map.get("deviceno").toString(),map);

                }

            }
            // pstmt.close();
            //dbcon.getClose();
        }catch(Exception e){
            logger.error("出错了"+e.toString());
            e.printStackTrace();
        }
        logger.warn(msg_author);
        logger.info("加载msg_author内容完成.");
    }
    /**
     * 读取online_warn
     * @param
     * @throws SQLException

     */

    synchronized static public void load_onlinewarn(JdbcTemplate jdbcTemplate){

        String sql="select *  from prtuana_v where timevalid=1";
        Map<String,Object> map =null;
        int i;
        logger.info("开始读取online_warn内容.......");
        try{
            List<Map<String, Object>> userData = jdbcTemplate.queryForList(sql);

            int size=userData.size() ;
            if (size> 0)
            {

                for ( i = 0; i<size; i++)
                {

                    map = (HashMap) userData.get(i);


                    online_warn.put(map.get("kkey").toString(),map);

                }

            }
            // pstmt.close();
            //dbcon.getClose();
        }catch(Exception e){
            logger.error("出错了"+e.toString());
            e.printStackTrace();
        }
        logger.warn(online_warn);
        logger.info("加载online_warn内容完成.");
    }
    /**
     * 读取信息表conditionslua
     * @param
     * @throws SQLException

     */
    synchronized static public void load_condition(JdbcTemplate jdbcTemplate){



        logger.info("开始读取conditionlua内容.......");
        String key="";
        String sql="select *  from conditionlua";
        //if(rand.equalsIgnoreCase(imagerand)){
        try{

            List<Map<String, Object>> userData = jdbcTemplate.queryForList(sql);

            int size=userData.size() ;
            if (size> 0)
            {
                for (int i = 0; i<size; i++)
                {
                    Map map = (HashMap) userData.get(i);
                    key = map.get("kkey").toString();
                    if (objcondition.containsKey(key))
                        ((List<Map<String, Object>>)objcondition.get(key)).add(map);
                    else {
                        List<Map<String, Object>> nobj =  new ArrayList<Map<String, Object>>();
                        nobj.add(map);
                        objcondition.put(key, nobj);
                    }

                    //tobj.put(map.get("kkey").toString(),map);
                }

            }
        }catch(Exception e){
            logger.error("condition出错了"+e.toString());
            e.printStackTrace();
        }
        logger.info(objcondition);
        logger.info("加载objcondition内容完成.");

    }
    /**
     * 检查遥测越限
     * @param
     * @throws ScriptException
     */

    synchronized static public int checkmaxmin(String val,String mx,String mn) {
        int st=-2;
        //logger.warn("check updown : " +  val+":"+  up+":"+  down+":"+  stat );


        try
        {

            if ((boolean) scriptEngine.eval(val+"<"+mn) )
                st = -1;
            if ((boolean) scriptEngine.eval(val+">"+mx))
                st = 1;
            if ((boolean) scriptEngine.eval(val+">="+mn)&&(boolean) scriptEngine.eval(val+"<="+mx) )
                st = 0;

        }catch (IllegalStateException ie)
        {
            logger.error("check maxmin err: " +  ie.toString() );
            ie.printStackTrace();
        }
        catch (ScriptException e) {
            logger.error("check maxmin err: " +  e.toString() );
            e.printStackTrace();
        }
        //logger.warn("st : " +  st );
        return st;

    }
    /**
     * 检查遥测越限
     * @param
     * @throws ScriptException
     */

    synchronized static public int checkupdown(String val,String down,String up,int stat) {
        int st=-2;
        //logger.warn("check updown : " +  val+":"+  up+":"+  down+":"+  stat );


        try
        {

            if ((boolean) scriptEngine.eval(val+"<"+down) && stat>-1)
                st = -1;
            if ((boolean) scriptEngine.eval(val+">"+up) && stat<1)
                st = 1;
            if ((boolean) scriptEngine.eval(val+">="+down)&&(boolean) scriptEngine.eval(val+"<="+up) && stat!=0)
                st = 0;

        }catch (IllegalStateException ie)
        {
            logger.error("check updown err: " +  ie.toString() );
            ie.printStackTrace();
        }
        catch (ScriptException e) {
            logger.error("check updown err: " +  e.toString() );
            e.printStackTrace();
        }
        //logger.warn("st : " +  st );
        return st;

    }
    /**
     * 计算condition
     * @param
     * @throws ScriptException
     */

    synchronized static public String calccondition(String val,String condition) {
        String st="";
        try
        {
            st = ""+scriptEngine.eval(val+condition) ;
        }catch (ScriptException e) {
            logger.error("check updown err: " +  e.toString() );
            e.printStackTrace();
        }

        return st;

    }
    /**
     * 检查condition
     * @param
     * @throws ScriptException
     */

    synchronized static public boolean checkcondition(String val,String condition) {
        boolean st=false;

        //logger.warn(val+condition);
        if (!condition.isEmpty()) {
            try {
                if ((boolean) scriptEngine.eval(val + condition))
                    st = true;
            } catch (ScriptException e) {
                logger.error("check updown err: " + e.toString());
                // e.printStackTrace();
            }
        }

        return st;

    }
    /**
     * 检查遥信变位
     * @param
     * @throws ScriptException
     */

    synchronized static public int checkevt(String val,int stat) {
        int st = 0;
        if (stat!=-1)
        {

            try
            {

                if ((boolean) scriptEngine.eval(val+"!="+stat) )
                {
                    st = 1;
                    //logger.info("checkevt: " +  val+"!="+stat );
                }


            }catch (IllegalStateException ie)
            {
                logger.error("check updown err: " +  ie.toString() );
                ie.printStackTrace();
            }
            catch (ScriptException e) {
                logger.error("check updown err: " +  e.toString() );
                e.printStackTrace();
            }
        }
        return st;

    }

    /**
     * checkevt

     */
    synchronized static public void handleCondition(String val, String channel, Jedis tjedis) {
        boolean calcEvt = false;
        String luaStr="",nval="";
        int tp=0;
        if (objcondition.containsKey(channel)){
            List<Map<String, Object>> dobj =  new ArrayList<Map<String, Object>>();
            try{
                dobj = (List<Map<String, Object>>)objcondition.get(channel);
                //logger.warn(dobj.toString());

                for (int i=0;i<dobj.size();i++){
                    tp = (int)dobj.get(i).get("type");
                    if (tp!=4) {
                        calcEvt = checkcondition(val, dobj.get(i).get("condition").toString());
                        switch (tp) {
                            case 0:
                                //;
                                if (calcEvt) {
                                    Reader r = new InputStreamReader(ThreadSubscriber.class.getResourceAsStream(dobj.get(i).get("luaname").toString()));
                                    luaStr = CharStreams.toString(r);
                                    tjedis.eval(luaStr);
                                    logger.warn("condition lua handled :" + channel + dobj.get(i).get("condition").toString());
                                }
                                break;
                            case 1:
                                luaStr = dobj.get(i).get("luaname").toString() + "_.value";//...;
                                if (calcEvt) {
                                    tjedis.set(luaStr, "" + 1);
                                    //logger.warn("condition lua handled :"+channel+dobj.get(i).get("condition").toString()+" set "+luaStr+"=1");
                                } else {
                                    tjedis.set(luaStr, "" + 0);
                                }
                                break;
                            case 2:
                                luaStr = dobj.get(i).get("luaname").toString() + "_.value";//...;
                                if (calcEvt) {
                                    tjedis.set(luaStr, dobj.get(i).get("delay").toString());
                                }
                                break;
                            case 3:
                                luaStr = dobj.get(i).get("luaname").toString() + "_.delay";//...;
                                if (tjedis.exists(luaStr))
                                    nval = tjedis.get(luaStr);
                                else
                                    nval = "0";
                                if (calcEvt && (!nval.matches("1"))) {
                                    tjedis.setex(luaStr, (int) dobj.get(i).get("delay"), "1");
                                    tjedis.set(luaStr.replace("delay", "value"), "0");
                                }
                                if (!calcEvt && (nval.matches("1"))) {
                                    tjedis.set(luaStr, "0");
                                    tjedis.set(luaStr.replace("delay", "value"), "0");
                                }
                                break;
                            default:

                                break;
                        }
                    }
                    if (tp==4)
                    {
                        luaStr = dobj.get(i).get("luaname").toString() + "_.value";//...;
                        //logger.warn(luaStr);
                        tjedis.set(luaStr, calccondition(val, dobj.get(i).get("condition").toString()));

                    }

                }              //for



            }catch (Exception e) {
                logger.error("condition lua err:"+e.toString());
                e.printStackTrace();
            }
        }

    }
    /*
     * 执行sql
     * getPrepatedResultSet
     */

    synchronized static public void add_red(JdbcTemplate jdbcTemplate,String sql)
    {

        try{

            int t= jdbcTemplate.update(sql);
            //dbcon.getClose();
        }catch(Exception e){
            logger.error("出错了"+e.toString()+":"+sql);
        }

    }

    /*
     * 查询信息
     * getPrepatedResultSet
     */
    synchronized static public String query_red(JdbcTemplate jdbcTemplate,String sql)
    {
        String msg="信息表错误";


        //logger.warn(sql);
        try{
            List userData = jdbcTemplate.queryForList(sql);
            if(!userData.isEmpty())
            msg= ((HashMap)userData.get(0)).get("msg").toString();
        }catch(Exception e){
            logger.error("出错了:"+sql+":"+e.toString());
            e.printStackTrace();
        }

        return msg;
    }
    synchronized static public void handleMessage(String channel, String message, JdbcTemplate jdbcTemplate, Jedis tjedis,Jedis mjedis, ChatSocket skt) {
        String val=null,pmessage=null;


        if (message.endsWith("_.value") )
        {
            try {

                    val = tjedis.get(message);

                //logger.warn(pattern+" - "+channel+" - "+message+" - "+val);
            } catch (Exception  e) {
                val = "0";
                logger.warn(message+":"+e);
                e.printStackTrace();
            }
            try {

                if (!skt.getSockets().isEmpty())
                    skt.broadcast(skt.getSockets(), "{\""+message.split("\\.")[0]+"\":{\""+message+"\":"+val+"}}",-1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pmessage = message.replace("_.value","");

            if (message.contains("ai")) handleMaxMin(pmessage,val,mjedis);

            handleEvt(pmessage,val,jdbcTemplate,skt);

            handleTime(pmessage,val,jdbcTemplate,skt);

            handleCondition(val,pmessage,tjedis);

        }else if (message.matches("anaupdate")) {
            try {
                loadAna_v(jdbcTemplate);
                logger.warn("the anatable has been reloaded.");
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        else if (message.matches("authorupdate")) {
            try {
                load_msguser(jdbcTemplate);
                load_author(jdbcTemplate);
                logger.warn("the anatable has been reloaded.");
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        /*else if (message.matches("onlinewarnupdate")) {
            try {

                load_onlinewarn(jdbcTemplate);
                logger.warn("the anatable has been reloaded.");
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }*/
        else if (message.matches("conditionupdate")) {
            try {
                load_condition(jdbcTemplate);
                logger.warn("the conditiontable has been reloaded.");
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        else if (message.contains("active"))
        {
            //
            //logger.info("延时虚拟遥信心跳：" );
            //tjedis.set(message.replace("delay","value"),"1");
        }
    }
    synchronized static public void handleExpired(String channel, String message, JdbcTemplate jdbcTemplate, Jedis tjedis) {
        String val=null,luaStr=null,pmessage=null;
        if (channel.contains("expired")) {
            try {

                if (message.contains("_.status"))   //do或者ao设置指令超时
                {
                    pmessage = message.replace("_.status","");
                    val = tjedis.get(pmessage+"_.value");
                    add_red(jdbcTemplate,"insert into setfail (kkey,nval,chgtime) values ('"+pmessage+"','"+val+"',sysdate())");
                    logger.info("设置指令超时：" + pmessage);
                }
                else if (message.contains("_.delay"))
                {
                    //
                    logger.info("延时虚拟遥信执行完成：" + message);
                    tjedis.set(message.replace("delay","value"),"1");
                }
                /*else if (message.matches("anaupdate")) {
                    try {
                        loadAna_v(jdbcTemplate);
                        logger.warn("the anatable has been reloaded.");
                    } catch (Exception e1) {

                        e1.printStackTrace();
                    }
                }
                else if (message.matches("authorupdate")) {
                    try {
                        load_msguser(jdbcTemplate);
                        load_author(jdbcTemplate);
                        logger.warn("the anatable has been reloaded.");
                    } catch (Exception e1) {

                        e1.printStackTrace();
                    }
                }
                else if (message.matches("conditionupdate")) {
                    try {
                        load_condition(jdbcTemplate);
                        logger.warn("the conditiontable has been reloaded.");
                    } catch (Exception e1) {

                        e1.printStackTrace();
                    }
                }*/

                else         //处理延时任务
                {
                    Reader r = new InputStreamReader(LuaJob.class.getResourceAsStream(message));
                    luaStr = CharStreams.toString(r);
                    tjedis.eval(luaStr);

                    logger.info("延时任务脚本执行完成：" + message);
                }
            }
            catch (Exception  e)
            {
                logger.error("delay lua err："+e.toString()+" : "+message);
                e.printStackTrace();
            }


        }

    }
    synchronized static public void handleMaxMin(String key,String vals,Jedis mjedis) {
        String maxv,minv;


        //String luaStr = null;
        //SimpleDateFormat df,df2,df3;
        //JSONObject  map = null;
        //HashMap<String,Object>  authormap = new HashMap<String,Object>();

        HashMap<String,Object>  map = new HashMap<String,Object>();

        //logger.error(key);
        //map = (HashMap<String,Object>)objana_v.get(key);
        //logger.warn(key+ "：" +vals);
        //logger.warn(map.toString());
        //df = new SimpleDateFormat("YYMMdd");//设置日期格式
        //df2 = new SimpleDateFormat("HHmmss");//设置日期格式
        // df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        // savet = (df.format(new Date()));// new Date()为获取当前系统时间
        LocalDateTime rightnow = LocalDateTime.now();
        int vv;

        if (objana_v.containsKey(key))
        {

            map = (HashMap<String,Object>)objana_v.get(key);
            // logger.warn(map.toString());
            maxv = map.get("maxv").toString();
            minv = map.get("minv").toString();


                vv =  checkmaxmin(vals,maxv,minv);
                //logger.warn(vv);//, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);//
                if (vv!=-2)
                {
                    //logger.warn(key+ "：" +vals);
                    //logger.warn(msg_author);

                    try {

                        //logger.warn(msg_author.toString());
                        if (vv==1)
                        {
                           // add_red(jdbcTemplate, "UPDATE prtuana SET maxv= " + vals + ",maxt='"+rightnow.format(formatter)+"' where kkey='"+key+"'");
                            ((HashMap<String,String>) objana_v.get(key)).put("maxv", vals);
                            ((HashMap<String,String>) objana_v.get(key)).put("maxt", rightnow.format(formatter));
                            mjedis.set(key+"_.maxv",vals);
                            mjedis.set(key+"_.maxt",rightnow.format(formatter));
                        }
                        if(vv==-1)
                        {
                           // add_red(jdbcTemplate, "UPDATE prtuana SET minv= " + vals + ",mint='"+rightnow.format(formatter)+"' where kkey='"+key+"'");
                            ((HashMap<String,String>) objana_v.get(key)).put("minv", vals);
                            ((HashMap<String,String>) objana_v.get(key)).put("mint", rightnow.format(formatter));
                            mjedis.set(key+"_.minv",vals);
                            mjedis.set(key+"_.mint",rightnow.format(formatter));

                        }



                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }






            //map.clear();
            //map=null;
        } else
        {
            //logger.error("redis_key do not match mysql_kkey:"+key+",请确认！！！");
        }

    }
    synchronized static public void handleEvt(String key,String vals,JdbcTemplate jdbcTemplate,ChatSocket skt) {
        String down,up,limit=" ",rtuno,sn,dbname,dbname2,msgah,mailah,altah,mobs = "",gkey="";
        Calendar c;
        String msg="";
        //String luaStr = null;
        //SimpleDateFormat df,df2,df3;
        //JSONObject  map = null;
        //HashMap<String,Object>  authormap = new HashMap<String,Object>();
        JSONObject  usermap = new JSONObject();
        HashMap<String,Object>  map = new HashMap<String,Object>();
        HashMap<String,Object>  tuser = new HashMap<String,Object>();
        //logger.error(key);
        //map = (HashMap<String,Object>)objana_v.get(key);
        //logger.warn(key+ "：" +vals);
        //logger.warn(map.toString());
        //df = new SimpleDateFormat("YYMMdd");//设置日期格式
        //df2 = new SimpleDateFormat("HHmmss");//设置日期格式
        // df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        // savet = (df.format(new Date()));// new Date()为获取当前系统时间
        LocalDateTime rightnow = LocalDateTime.now();

        dbname = "hevtyc"+(rightnow.getYear()%10);
        dbname2 = "hevt"+(rightnow.getYear()%10);
        int min= rightnow.getHour()*60+rightnow.getMinute();
        //logger.warn(msg_author.toString());
        int vv = -2;int ttp=0;
        if (objana_v.containsKey(key))
        {

            map = (HashMap<String,Object>)objana_v.get(key);
            // logger.warn(map.toString());
            try {
                msgah = map.get("author_msg").toString();
            }catch (Exception e)
            {
                msgah = "0";
            }

            try {
                mailah = map.get("author_email").toString();
            }catch (Exception e)
            {
                mailah = "0";
            }
            try {
                altah = map.get("author_alert").toString();
            }catch (Exception e)
            {
                altah = "0";
            }
            rtuno = map.get("rtuno").toString();
            sn = map.get("sn").toString();
            gkey = key.split("\\.")[0];
            if(user_divide==0) gkey = "un_all_";

            if (key.contains("ai")){
                //logger.warn(map.toString());
                try {
                    down = map.get("lowerlimit").toString();
                }catch (Exception e)
                {
                    down = "0";
                }
                try {
                    up = map.get("upperlimit").toString();
                }catch (Exception e)
                {
                    up = "999999";
                }


                //logger.warn(up);


                //logger.error(s);
                //logger.error(luaStr);
                //logger.error(map.get("lowerlimit").toString());
                //logger.warn(map);//, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);//
                // logger.warn(key+","+vals+","+down+","+up+","+Integer.parseInt(map.get("chgtime").toString()));
                vv =  checkupdown(vals,down,up,Integer.parseInt(map.get("chgtime").toString()));
                //logger.warn(vv);//, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);//
                if (vv!=-2)
                {
                    //logger.warn(key+ "：" +vals);
                    //logger.warn(msg_author);

                    try {

                        //logger.warn(msg_author.toString());
                        if (vv==1) limit=up;
                        if(vv==-1) limit=down;
                        if (vv==0) {
                            add_red(jdbcTemplate, "INSERT INTO " + dbname + " (ymd,hms,ch,xh,zt,val,readstatus) values (" + rightnow.format(DateTimeFormatter.ofPattern("YYMMdd")) + "," + rightnow.format(DateTimeFormatter.ofPattern("HHmmss")) + "," + rtuno + "," + sn + "," + vv + "," + vals + ",0)");
                            add_red(jdbcTemplate, "UPDATE prtu SET status=0 WHERE RTUNO=" + rtuno );
                        }else {
                            add_red(jdbcTemplate, "INSERT INTO " + dbname + " (ymd,hms,ch,xh,zt,val,tlimit,readstatus) values (" + rightnow.format(DateTimeFormatter.ofPattern("YYMMdd")) + "," + rightnow.format(DateTimeFormatter.ofPattern("HHmmss")) + "," + rtuno + "," + sn + "," + vv + "," + vals + "," + limit + ",0)");
                            add_red(jdbcTemplate, "UPDATE prtu SET status=1 WHERE RTUNO=" + rtuno );
                        }
                        //chgtime 字段用来存储当前遥测状态
                        add_red(jdbcTemplate, "UPDATE prtuana SET chgtime=" + vv + " WHERE RTUNO=" + rtuno + " AND SN=" + sn);
                        ((HashMap<String,String>) objana_v.get(key)).put("chgtime", ""+vv);

                        //authormap = (HashMap<String,Object>)msg_author.get(map.get("deviceno").toString());


                        //logger.warn(authormap.toString());

                        msg = query_red(jdbcTemplate, "select concat(a.pro_name,b.name,c.roomname,d.devicenm,e.name) msg from project_list a,prtu b,room c,dev_author d,prtuana e where a.pro_id=b.domain and b.rtuno=c.rtuno and c.roomno = d.roomno and d.deviceno=e.deviceno  and e.rtuno=" + rtuno + " and e.sn=" + sn);
                        if (!msg.isEmpty())
                        {
                            msg = rightnow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " " + msg;
                            switch (vv) {
                                case -1:
                                    msg = msg + " 越下限。备注：" + vals + "（" + down + "~" + up + "）";
                                    ttp = 1;
                                    break;
                                case 0:
                                    msg = msg + " 恢复正常。备注：" + vals + "（" + down + "~" + up + "）";
                                    ttp = 0;
                                    break;
                                case 1:
                                    msg = msg + " 越上限。备注：" + vals + "（" + down + "~" + up + "）";
                                    ttp = 1;
                                    break;
                                default:
                                    break;
                            }
                            if (Integer.parseInt(altah) > 0) {
                                //logger.info( "send alt_yc_msg :" + msg);
                                if (!skt.getSockets().isEmpty()) {
                                    skt.broadcast(skt.getSockets(), "{\"auth\":\""+altah+"\",\"gkey\":\""+gkey+"\",\"msg\":{\"message\":\"" + msg + "\",\"title\":\"遥测越限\",\"type\":" + ttp + ",\"stay\":5000}}", Integer.parseInt(altah));
                                    logger.warn( "send alt_msg :" + "{\"auth\":\""+altah+"\",\"gkey\":\""+gkey+"\",\"msg\":{\"message\":\"" + msg + "\",\"title\":\"遥测越限\",\"type\":" + ttp + ",\"stay\":5000}}", Integer.parseInt(altah));

                                }
                                // logger.warn( "send alt_msg :" + "{\"msg\":{\"message\":\""+msg+"\",\"title\":\"遥测越\",\"type\":"+ttp+",\"stay\":5000}}");
                                //sendmsg.sendmsg(mobs.substring(1),msg);\"auth\":\""+altah+"\",
                            }



                        }
                        msg=projectName +msg;
                        if (Integer.parseInt(msgah) > 1) {
                            try {
                                String s = Integer.toBinaryString(Integer.parseInt(msgah));
                                //logger.warn(s);
                                s = (new StringBuffer(s).reverse()).toString();
                                //logger.warn(s);
                                char[] ss=new char[s.length()];
                                ss=s.toCharArray();


                                for (int i = 1; i < s.length(); i++) {
                                    //logger.warn("ss"+i+":"+ss[i]);
                                    if (ss[i] == '1' && msg_user.containsKey(gkey)) {
                                        usermap = (JSONObject) msg_user.get(gkey);
                                        if (usermap.containsKey(String.valueOf(i+1))) {
                                            tuser = (HashMap<String, Object>) usermap.get(""+(i+1));


                                            if ((Boolean) tuser.get("phonevalid")==true && (min > Integer.parseInt(tuser.get("msg_st").toString())) && (min < Integer.parseInt(tuser.get("msg_et").toString())))
                                                mobs = mobs + "," + tuser.get("phone").toString();
                                        }
                                    }
                                }
                                if (!mobs.isEmpty()) {
                                    logger.warn(mobs.substring(1) + "：" + msg);
                                    //sendmsg.sendmsg(mobs.substring(1),msg);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        if (Integer.parseInt(mailah) > 1) {
                            HashMap<String, String> umap = new HashMap<String, String>();
                            try {
                                String s = Integer.toBinaryString(Integer.parseInt(mailah));
                                char ss[] =(new StringBuffer(s).reverse()).toString().toCharArray();
                                for (int i = 1; i < ss.length; i++) {
                                    if (ss[i] == '1' && msg_user.containsKey(gkey)) {
                                        usermap = (JSONObject) msg_user.get(gkey);
                                        if (usermap.containsKey(String.valueOf(i+1))) {
                                            tuser = (HashMap<String, Object>) usermap.get(""+(i+1));

                                            if ((Boolean) tuser.get("emailvalid")==true && (tuser.get("email").toString() != null))
                                                umap.put(tuser.get("name").toString(), tuser.get("email").toString());
                                        }

                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            emailUtil sendmail = new emailUtil();
                            // logger.warn(sendmail.emailSend("系统信息", msg, umap) + ":" + umap.toString());

                        }
                        // logger.warn("save hevtyc:"+df.format(new Date())+","+df2.format(new Date())+","+rtuno+","+sn+","+vv+","+vals);
                        //updownstat.put(s, ""+vv);


                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            if (key.contains("di")){

                //map =(Map) objdig.get(s);
                //logger.warn(map.toString());
                //logger.warn(vals);
                mobs="";
                //rtuno = map.get("rtuno").toString();
                //sn = map.get("sn").toString();
                up = map.get("type").toString();
                // logger.warn(key+","+vals+","+map.get("chgtime").toString());
                vv =  checkevt(vals,Integer.parseInt(map.get("chgtime").toString()));

                //logger.warn(map);
                // logger.warn(vals);
                //logger.warn(vv);
                if (map.get("chgtime").toString()!="-1" && vv!=0 && Integer.parseInt(up)!=0)
                {
                    //logger.warn(key+ "：" +vals);
                    try
                    {
                        //logger.warn("INSERT INTO "+dbname2+" (ymd,hms,ch,xh,event,zt,readstatus) values ("+rightnow.format(DateTimeFormatter.ofPattern("YYMMdd"))+","+rightnow.format(DateTimeFormatter.ofPattern("HHmmss"))+","+rtuno+","+sn+","+up+","+vals+",0)");

                        add_red(jdbcTemplate,"INSERT INTO "+dbname2+" (ymd,hms,ch,xh,event,zt,readstatus) values ("+rightnow.format(DateTimeFormatter.ofPattern("YYMMdd"))+","+rightnow.format(DateTimeFormatter.ofPattern("HHmmss"))+","+rtuno+","+sn+","+up+","+vals+",0)");
                        //logger.warn("INSERT INTO "+dbname2+" (ymd,hms,ch,xh,event,zt,readstatus) values ("+rightnow.format(DateTimeFormatter.ofPattern("YYMMdd"))+","+rightnow.format(DateTimeFormatter.ofPattern("HHmmss"))+","+rtuno+","+sn+","+up+","+vals+",0)");
                        add_red(jdbcTemplate,"UPDATE prtudig SET chgtime="+vals+" where RTUNO="+rtuno+" AND SN="+sn);
                        ((HashMap<String,String>) objana_v.get(key)).put("chgtime", ""+vals);
                        // logger.warn("save hevt:"+df.format(new Date())+","+df2.format(new Date())+","+rtuno+","+sn+","+vv+","+vals);

                    } catch (Exception e) {

                        e.printStackTrace();
                    }







                    // authormap = (HashMap<String,Object>)msg_author.get(map.get("deviceno").toString());
                    //logger.warn(authormap);


                    msg=query_red(jdbcTemplate,"select concat(a.pro_name,b.name,c.roomname,d.devicenm,e.name,f.e_info,',',f.e_color,',',f.e_stay) msg from project_list a,prtu b,room c,dev_author d,prtudig e,etype_info f where a.pro_id=b.domain and b.rtuno=c.rtuno and c.roomno = d.roomno and c.rtuno=d.domain and d.deviceno=e.deviceno and c.rtuno=e.rtuno and e.type=f.e_type  and e.rtuno="+rtuno+" and e.sn="+sn+" and f.e_zt="+vals);
                    String[] msgs = msg.split(",");
                    msg=projectName+rightnow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+" "+msgs[0];
                    if (Integer.parseInt(altah) > 0) {

                        if (!skt.getSockets().isEmpty())
                            //skt.broadcast(skt.getSockets(), "{\"gkey\":\""+key.split("\\.")[0]+"\",\"msg\":{\"message\":\"" + msg + "\",\"title\":\"遥测越限\",\"type\":" + ttp + ",\"stay\":5000}}", Integer.parseInt(altah));

                            skt.broadcast(skt.getSockets(), "{\"auth\":\""+altah+"\",\"gkey\":\""+gkey+"\",\"msg\":{\"message\":\""+rightnow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+" "+msgs[0]+"\",\"title\":\"开关变位\",\"type\":\""+msgs[1]+"\",\"stay\":\""+msgs[2]+"\"}}",Integer.parseInt(altah));

                        logger.info( "send alt_yx_msg :" + msgs[0]+"   {\"auth\":\""+altah+"\",\"gkey\":\""+gkey+"\"}");
                        //sendmsg.sendmsg(mobs.substring(1),msg);
                    }

                    if (Integer.parseInt(msgah) > 1) {
                        try {
                            String s = Integer.toBinaryString(Integer.parseInt(msgah));
                            //logger.warn(s);
                            s = (new StringBuffer(s).reverse()).toString();
                            //logger.warn(s);
                            char[] ss=new char[s.length()];
                            ss=s.toCharArray();


                            for (int i = 1; i < s.length(); i++) {
                                //logger.warn("ss"+i+":"+ss[i]);
                                if (ss[i] == '1' && msg_user.containsKey(gkey)) {
                                    usermap = (JSONObject) msg_user.get(gkey);
                                    // logger.warn(i);
                                    // logger.warn(usermap);
                                    if (usermap.containsKey(String.valueOf(i+1)))
                                    {
                                        tuser = (HashMap<String, Object>) usermap.get(""+(i+1));

                                        //   logger.warn(tuser);
                                        if ((Boolean) tuser.get("phonevalid")==true && (min > Integer.parseInt(tuser.get("msg_st").toString())) && (min < Integer.parseInt(tuser.get("msg_et").toString())))
                                            mobs = mobs + "," + tuser.get("phone").toString();
                                    }
                                }
                            }
                            if (!mobs.isEmpty()) {
                                logger.warn(mobs.substring(1) + "：" + msg);
                                //sendmsg.sendmsg(mobs.substring(1),msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    if (Integer.parseInt(mailah) > 1) {
                        HashMap<String, String> umap = new HashMap<String, String>();
                        try {
                            String s = Integer.toBinaryString(Integer.parseInt(mailah));
                            char ss[] =(new StringBuffer(s).reverse()).toString().toCharArray();
                            for (int i = 1; i < ss.length; i++) {
                                if (ss[i] == '1' && msg_user.containsKey(gkey)) {
                                    usermap = (JSONObject) msg_user.get(gkey);
                                    if (usermap.containsKey(String.valueOf(i+1))) {
                                        tuser = (HashMap<String, Object>) usermap.get(""+(i+1));

                                        if ((Boolean) tuser.get("emailvalid")==true && (tuser.get("email").toString() != null))
                                            umap.put(tuser.get("name").toString(), tuser.get("email").toString());
                                    }

                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        emailUtil sendmail = new emailUtil();
                        // logger.warn(sendmail.emailSend("系统信息", msg, umap) + ":" + umap.toString());

                    }



                }


                //map.clear();
                //map=null;
            }



            //map.clear();
            //map=null;
        } else
        {
            //logger.error("redis_key do not match mysql_kkey:"+key+",请确认！！！");
        }

    }


    synchronized static public void handleTime(String key,String vals,JdbcTemplate jdbcTemplate,ChatSocket skt)  {
        String down,up,limit=" ",dbname,rtuno,sn,msgah,mailah,altah,mobs = "",gkey="";
       // Calendar c;
        String msg="";

        //SimpleDateFormat df,df2,df3;
        LocalDateTime rightnow = LocalDateTime.now();


        String rnow = rightnow.format(formatter);
        JSONObject  usermap = new JSONObject();
        HashMap<String,Object>  map = new HashMap<String,Object>();
        HashMap<String,Object>  tuser = new HashMap<String,Object>();
       // df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式


        int vv = -2;
        int timevalid=0;
        //logger.error(key);



        if (objana_v.containsKey(key))
           {

            map = (HashMap<String,Object>)objana_v.get(key);


            try {
                timevalid = Integer.parseInt(map.get("timevalid").toString());
            }catch (Exception e)
            {
                timevalid = 0;
            }

            if (timevalid==1)
            {
                if (key.contains("ai"))
                    dbname = "prtuana";
                else
                    dbname = "prtudig";
                //logger.warn(map.get("kkey").toString()+","+timevalid+","+vals+","+map.get("timecondition").toString());
                if (checkcondition(vals,map.get("timecondition").toString()) && Integer.parseInt(map.get("timestat").toString())==0)
                {
                    logger.warn(key+" 开始计时 ");
                    //开始计时
                    ((HashMap<String,String>) objana_v.get(key)).put("timestat","1");

                    ((HashMap<String,String>) objana_v.get(key)).put("checktime",rnow);
                    add_red(jdbcTemplate,"UPDATE "+dbname+" SET timestat=1 ,checktime='"+rnow+"' where kkey='"+key+"'");
                    logger.warn("UPDATE "+dbname+" SET timestat=1 ,checktime='"+rnow+"' where kkey='"+key+"'");
                }
                else if (!checkcondition(vals,map.get("timecondition").toString()) && Integer.parseInt(map.get("timestat").toString())==1)
                {
                    //停止计时

                    logger.warn(key+" 停止计时 ");
                    //LocalDateTime Date2 = LocalDateTime.now();
                    LocalDateTime to2 = LocalDateTime.parse(map.get("checktime").toString(), formatter);
                    Duration duration = Duration.between(to2,rightnow);

                    long minutes = duration.toMinutes();//相差的分钟数
                    //float hours =  ((rightnow.get - to2) / (1000.00f * 60 * 60));
                    float tot = minutes/60.00f + Float.parseFloat(map.get("online").toString());
                    //logger.warn(df3.format(Date2)+","+df3.format(toDate2)+","+hours);
                    ((HashMap<String,String>) objana_v.get(key)).put("timestat","0");
                    ((HashMap<String,String>) objana_v.get(key)).put("online",""+tot);
                    ((HashMap<String,String>) objana_v.get(key)).put("checktime",rnow);
                    /**
                     * 设备在线运行时长不发送实时数据，只在网页对应的表格上显示
                     */
                   /* try {

                        if (!skt.getSockets().isEmpty())
                            skt.broadcast(skt.getSockets(), "{\""+key.split("\\.")[0]+"\":{\""+key+"_t"+"\":"+tot+"}}",-1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    add_red(jdbcTemplate,"UPDATE "+dbname+" SET timestat=0 ,checktime='"+rnow+"',online="+tot+" where kkey='"+key+"'");
                    if (tot>(Float.parseFloat(map.get("warnline").toString()))*(Integer.parseInt(map.get("warntimes").toString())+1))
                    {
                        /////////////////////////////////
                        //达到报警线
                        // logger.warn(map.toString());
                       // ((HashMap<String,String>) objana_v.get(key)).put("timestat","2");
                        add_red(jdbcTemplate,"UPDATE "+dbname+" SET warntimes=warnimes+1 where kkey='"+key+"'");
                        msgah = map.get("author_msg").toString();
                        mailah = map.get("author_email").toString();
                        altah = map.get("author_alert").toString();

                        if (key.contains("ai")){
                            //logger.warn(map.toString());

                            rtuno = map.get("rtuno").toString();
                            sn = map.get("sn").toString();
                            gkey = key.split("\\.")[0];


                                //logger.warn(key+ "：" +vals);
                                //logger.warn(msg_author);

                                try {

                                    //logger.warn(msg_author.toString());


                                    msg = query_red(jdbcTemplate, "select concat(a.pro_name,b.name,c.roomname,d.devicenm) msg from project_list a,prtu b,room c,dev_author d,prtuana e where a.pro_id=b.domain and b.rtuno=c.rtuno and c.roomno = d.roomno and d.deviceno=e.deviceno  and e.rtuno=" + rtuno + " and e.sn=" + sn);
                                    if (!msg.isEmpty())
                                    {
                                        msg = msg + " 在线运行距上次保养已超("+map.get("warnline")+"小时)";
                                        if (Integer.parseInt(altah) > 0) {
                                            //logger.info( "send alt_yc_msg :" + msg);
                                            if (!skt.getSockets().isEmpty()) {
                                                skt.broadcast(skt.getSockets(), "{\"auth\":\""+altah+"\",\"gkey\":\""+gkey+"\",\"msg\":{\"message\":\"" + msg + "\",\"title\":\"运行时长超限\",\"type\":2,\"stay\":5000}}", Integer.parseInt(altah));
                                                logger.warn( "send alt_msg :" + "{\"auth\":\""+altah+"\",\"gkey\":\""+gkey+"\",\"msg\":{\"message\":\"" + msg + "\",\"title\":\"运行时长超限\",\"type\":2,\"stay\":5000}}", Integer.parseInt(altah));

                                            }
                                            // logger.warn( "send alt_msg :" + "{\"msg\":{\"message\":\""+msg+"\",\"title\":\"遥测越\",\"type\":"+ttp+",\"stay\":5000}}");
                                            //sendmsg.sendmsg(mobs.substring(1),msg);\"auth\":\""+altah+"\",
                                        }



                                    }
                                    msg=projectName +msg;
                                    if (Integer.parseInt(msgah) > 1) {
                                        try {
                                            String s = Integer.toBinaryString(Integer.parseInt(msgah));
                                            //logger.warn(s);
                                            s = (new StringBuffer(s).reverse()).toString();
                                            //logger.warn(s);
                                            char[] ss=new char[s.length()];
                                            ss=s.toCharArray();


                                            for (int i = 1; i < s.length(); i++) {
                                                //logger.warn("ss"+i+":"+ss[i]);
                                                if (ss[i] == '1') {
                                                    usermap = (JSONObject) msg_user.get(gkey);
                                                    tuser = (HashMap<String, Object>) usermap.get(""+(i+1));
                                                    if (tuser != null) {

                                                        if ((Boolean) tuser.get("phonevalid")==true && (tuser.get("phone").toString() != null))
                                                            mobs = mobs + "," + tuser.get("phone").toString();
                                                    }
                                                }
                                            }
                                            if (!mobs.isEmpty()) {
                                                logger.warn(mobs.substring(1) + "：" + msg);
                                                //sendmsg.sendmsg(mobs.substring(1),msg);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                    if (Integer.parseInt(mailah) > 1) {
                                        HashMap<String, String> umap = new HashMap<String, String>();
                                        try {
                                            String s = Integer.toBinaryString(Integer.parseInt(mailah));
                                            char ss[] =(new StringBuffer(s).reverse()).toString().toCharArray();
                                            for (int i = 1; i < ss.length; i++) {
                                                if (ss[i] == '1') {
                                                    usermap = (JSONObject) msg_user.get(gkey);
                                                    tuser = (HashMap<String, Object>) usermap.get(""+(i+1));
                                                    if (tuser != null) {

                                                        if ((Boolean) tuser.get("emailvalid")==true && (tuser.get("email").toString() != null))
                                                            umap.put(tuser.get("name").toString(), tuser.get("email").toString());
                                                    }

                                                }
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        emailUtil sendmail = new emailUtil();
                                        // logger.warn(sendmail.emailSend("系统信息", msg, umap) + ":" + umap.toString());

                                    }


                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }


                        }
                        if (key.contains("di")){


                            mobs="";
                            rtuno = map.get("rtuno").toString();
                            sn = map.get("sn").toString();

                            gkey = key.split("\\.")[0];

                                msg=query_red(jdbcTemplate,"select concat(a.pro_name,b.name,c.roomname,d.devicenm) msg from project_list a,prtu b,room c,dev_author d,prtudig e where a.pro_id=b.domain and b.rtuno=c.rtuno and c.roomno = d.roomno and d.deviceno=e.deviceno   and e.rtuno="+rtuno+" and e.sn="+sn);


                                if (Integer.parseInt(altah) > 0) {

                                    if (!skt.getSockets().isEmpty())
                                        //skt.broadcast(skt.getSockets(), "{\"gkey\":\""+key.split("\\.")[0]+"\",\"msg\":{\"message\":\"" + msg + "\",\"title\":\"遥测越限\",\"type\":" + ttp + ",\"stay\":5000}}", Integer.parseInt(altah));

                                        skt.broadcast(skt.getSockets(), "{\"auth\":\""+altah+"\",\"gkey\":\""+gkey+"\",\"msg\":{\"message\":\""+msg+"\",\"title\":\"运行时长超限\",\"type\":2,\"stay\":0}}",Integer.parseInt(altah));

                                    logger.info( "send alt_yx_msg :" + msg);
                                    //sendmsg.sendmsg(mobs.substring(1),msg);
                                }
                                 msg=projectName +msg;
                                if (Integer.parseInt(msgah) > 1) {
                                    try {
                                        String s = Integer.toBinaryString(Integer.parseInt(msgah));
                                        //logger.warn(s);
                                        s = (new StringBuffer(s).reverse()).toString();
                                        //logger.warn(s);
                                        char[] ss=new char[s.length()];
                                        ss=s.toCharArray();


                                        for (int i = 1; i < s.length(); i++) {
                                            //logger.warn("ss"+i+":"+ss[i]);
                                            if (ss[i] == '1') {
                                                usermap = (JSONObject) msg_user.get(gkey);
                                                tuser = (HashMap<String, Object>) usermap.get(""+(i+1));
                                                if (tuser != null) {

                                                    if ((Boolean) tuser.get("phonevalid")==true && (tuser.get("phone").toString() != null))
                                                        mobs = mobs + "," + tuser.get("phone").toString();
                                                }
                                            }
                                        }
                                        if (!mobs.isEmpty()) {
                                            logger.warn(mobs.substring(1) + "：" + msg);
                                            //sendmsg.sendmsg(mobs.substring(1),msg);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                if (Integer.parseInt(mailah) > 1) {
                                    HashMap<String, String> umap = new HashMap<String, String>();
                                    try {
                                        String s = Integer.toBinaryString(Integer.parseInt(mailah));
                                        char ss[] =(new StringBuffer(s).reverse()).toString().toCharArray();
                                        for (int i = 1; i < ss.length; i++) {
                                            if (ss[i] == '1') {
                                                usermap = (JSONObject) msg_user.get(gkey);
                                                tuser = (HashMap<String, Object>) usermap.get(""+(i+1));
                                                if (tuser != null) {

                                                    if ((Boolean) tuser.get("emailvalid")==true && (tuser.get("email").toString() != null))
                                                        umap.put(tuser.get("name").toString(), tuser.get("email").toString());
                                                }

                                            }
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    emailUtil sendmail = new emailUtil();
                                    // logger.warn(sendmail.emailSend("系统信息", msg, umap) + ":" + umap.toString());

                                }


                        }


                        /////////////////////////////////
                    }


                }
            }

        } else
        {
            //logger.error("redis_key do not match mysql_kkey:"+key+",请确认！！！");
        }

    }



    synchronized static public void checkTime(String key,String vals,JdbcTemplate jdbcTemplate,ChatSocket skt) throws ParseException {
        String down,up,limit=" ",dbname,rtuno,sn,msgah,mailah,altah,mobs = "",gkey="";
        LocalDateTime rightnow = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String rnow = rightnow.format(formatter);
        String msg="";
        //String luaStr = null;


        JSONObject  usermap = new JSONObject();
        HashMap<String,Object>  map = new HashMap<String,Object>();
        HashMap<String,Object>  tuser = new HashMap<String,Object>();
        //logger.error(key);
        map = (HashMap<String,Object>)objana_v.get(key);
        //logger.warn(key+ "：" +vals);
        //logger.warn(map.toString());

       // df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式

        //logger.warn(msg_author.toString());

        int timevalid=0;


        if (map!=null)
        {
            if (key.contains("ai"))
                dbname = "prtuana";
            else
                dbname = "prtudig";
            try {
                timevalid = Integer.parseInt(map.get("timevalid").toString());
            }catch (Exception e)
            {
                timevalid = 0;
            }
            if (timevalid==1)
            {
                 if (Integer.parseInt(map.get("timestat").toString())==1)
                {


                    LocalDateTime to2 = LocalDateTime.parse(map.get("checktime").toString(), formatter);
                    Duration duration = Duration.between(to2,rightnow);

                    long minutes = duration.toMinutes();//相差的分钟数
                    //float hours =  ((rightnow.get - to2) / (1000.00f * 60 * 60));
                    float tot = minutes/60.00f + Float.parseFloat(map.get("online").toString());

                    ((HashMap<String,String>) objana_v.get(key)).put("checktime",rnow);
                    ((HashMap<String,String>) objana_v.get(key)).put("online",""+tot);
                    /**
                     * 设备在线运行时长不发送实时数据，只在网页对应的表格上显示
                     */
                    /*try {

                        if (!skt.getSockets().isEmpty())
                            skt.broadcast(skt.getSockets(), "{\""+key.split("\\.")[0]+"\":{\""+key+"_t"+"\":"+tot+"}}",-1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    add_red(jdbcTemplate,"UPDATE "+dbname+" SET online="+tot+",checktime='"+rnow+"' where kkey='"+key+"'");
                    logger.warn("UPDATE "+dbname+" SET online="+tot+",checktime='"+rnow+"' where kkey='"+key+"'");
                    if (tot>(Float.parseFloat(map.get("warnline").toString()))*(Integer.parseInt(map.get("warntimes").toString())+1))
                    {
                        /////////////////////////////////
                        //达到报警线
                        // logger.warn(map.toString());
                        //((HashMap<String,String>) objana_v.get(key)).put("timestat","2");
                        add_red(jdbcTemplate,"UPDATE "+dbname+" SET warntimes=warntimes+1  where kkey='"+key+"'");
                        msgah = map.get("author_msg").toString();
                        mailah = map.get("author_email").toString();
                        altah = map.get("author_alert").toString();

                        if (key.contains("ai")){
                            //logger.warn(map.toString());

                            rtuno = map.get("rtuno").toString();
                            sn = map.get("sn").toString();
                            gkey = key.split("\\.")[0];


                            //logger.warn(key+ "：" +vals);
                            //logger.warn(msg_author);

                            try {

                                //logger.warn(msg_author.toString());


                                msg = query_red(jdbcTemplate, "select concat(a.pro_name,b.name,c.roomname,d.devicenm) msg from project_list a,prtu b,room c,dev_author d,prtuana e where a.pro_id=b.domain and b.rtuno=c.rtuno and c.roomno = d.roomno and d.deviceno=e.deviceno  and e.rtuno=" + rtuno + " and e.sn=" + sn);
                                if (!msg.isEmpty())
                                {
                                    msg = msg + " 在线运行距上次保养已超("+map.get("warnline")+"小时)";
                                    if (Integer.parseInt(altah) > 0) {
                                        //logger.info( "send alt_yc_msg :" + msg);
                                        if (!skt.getSockets().isEmpty()) {
                                            skt.broadcast(skt.getSockets(), "{\"auth\":\""+altah+"\",\"gkey\":\""+gkey+"\",\"msg\":{\"message\":\"" + msg + "\",\"title\":\"运行时长超限\",\"type\":2,\"stay\":5000}}", Integer.parseInt(altah));
                                            logger.warn( "send alt_msg :" + "{\"auth\":\""+altah+"\",\"gkey\":\""+gkey+"\",\"msg\":{\"message\":\"" + msg + "\",\"title\":\"运行时长超限\",\"type\":2,\"stay\":5000}}", Integer.parseInt(altah));

                                        }
                                        // logger.warn( "send alt_msg :" + "{\"msg\":{\"message\":\""+msg+"\",\"title\":\"遥测越\",\"type\":"+ttp+",\"stay\":5000}}");
                                        //sendmsg.sendmsg(mobs.substring(1),msg);\"auth\":\""+altah+"\",
                                    }



                                }
                                msg=projectName +msg;
                                if (Integer.parseInt(msgah) > 1) {
                                    try {
                                        String s = Integer.toBinaryString(Integer.parseInt(msgah));
                                        //logger.warn(s);
                                        s = (new StringBuffer(s).reverse()).toString();
                                        //logger.warn(s);
                                        char[] ss=new char[s.length()];
                                        ss=s.toCharArray();


                                        for (int i = 1; i < s.length(); i++) {
                                            //logger.warn("ss"+i+":"+ss[i]);
                                            if (ss[i] == '1') {
                                                usermap = (JSONObject) msg_user.get(gkey);
                                                tuser = (HashMap<String, Object>) usermap.get(""+(i+1));
                                                if (tuser != null) {

                                                    if ((Boolean) tuser.get("phonevalid")==true && (tuser.get("phone").toString() != null))
                                                        mobs = mobs + "," + tuser.get("phone").toString();
                                                }
                                            }
                                        }
                                        if (!mobs.isEmpty()) {
                                            logger.warn(mobs.substring(1) + "：" + msg);
                                            //sendmsg.sendmsg(mobs.substring(1),msg);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                if (Integer.parseInt(mailah) > 1) {
                                    HashMap<String, String> umap = new HashMap<String, String>();
                                    try {
                                        String s = Integer.toBinaryString(Integer.parseInt(mailah));
                                        char ss[] =(new StringBuffer(s).reverse()).toString().toCharArray();
                                        for (int i = 1; i < ss.length; i++) {
                                            if (ss[i] == '1') {
                                                usermap = (JSONObject) msg_user.get(gkey);
                                                tuser = (HashMap<String, Object>) usermap.get(""+(i+1));
                                                if (tuser != null) {

                                                    if ((Boolean) tuser.get("emailvalid")==true && (tuser.get("email").toString() != null))
                                                        umap.put(tuser.get("name").toString(), tuser.get("email").toString());
                                                }

                                            }
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    emailUtil sendmail = new emailUtil();
                                    // logger.warn(sendmail.emailSend("系统信息", msg, umap) + ":" + umap.toString());

                                }


                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                        if (key.contains("di")){


                            mobs="";
                            rtuno = map.get("rtuno").toString();
                            sn = map.get("sn").toString();

                            gkey = key.split("\\.")[0];

                            msg=query_red(jdbcTemplate,"select concat(a.pro_name,b.name,c.roomname,d.devicenm) msg from project_list a,prtu b,room c,dev_author d,prtudig e where a.pro_id=b.domain and b.rtuno=c.rtuno and c.roomno = d.roomno and d.deviceno=e.deviceno   and e.rtuno="+rtuno+" and e.sn="+sn);


                            if (Integer.parseInt(altah) > 0) {

                                if (!skt.getSockets().isEmpty())
                                    //skt.broadcast(skt.getSockets(), "{\"gkey\":\""+key.split("\\.")[0]+"\",\"msg\":{\"message\":\"" + msg + "\",\"title\":\"遥测越限\",\"type\":" + ttp + ",\"stay\":5000}}", Integer.parseInt(altah));

                                    skt.broadcast(skt.getSockets(), "{\"auth\":\""+altah+"\",\"gkey\":\""+gkey+"\",\"msg\":{\"message\":\""+msg+"\",\"title\":\"运行时长超限\",\"type\":2,\"stay\":0}}",Integer.parseInt(altah));

                                logger.info( "send alt_yx_msg :" + msg);
                                //sendmsg.sendmsg(mobs.substring(1),msg);
                            }
                            msg=projectName +msg;
                            if (Integer.parseInt(msgah) > 1) {
                                try {
                                    String s = Integer.toBinaryString(Integer.parseInt(msgah));
                                    //logger.warn(s);
                                    s = (new StringBuffer(s).reverse()).toString();
                                    //logger.warn(s);
                                    char[] ss=new char[s.length()];
                                    ss=s.toCharArray();


                                    for (int i = 1; i < s.length(); i++) {
                                        //logger.warn("ss"+i+":"+ss[i]);
                                        if (ss[i] == '1') {
                                            usermap = (JSONObject) msg_user.get(gkey);
                                            tuser = (HashMap<String, Object>) usermap.get(""+(i+1));
                                            if (tuser != null) {

                                                if ((Boolean) tuser.get("phonevalid")==true && (tuser.get("phone").toString() != null))
                                                    mobs = mobs + "," + tuser.get("phone").toString();
                                            }
                                        }
                                    }
                                    if (!mobs.isEmpty()) {
                                        logger.warn(mobs.substring(1) + "：" + msg);
                                        //sendmsg.sendmsg(mobs.substring(1),msg);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            if (Integer.parseInt(mailah) > 1) {
                                HashMap<String, String> umap = new HashMap<String, String>();
                                try {
                                    String s = Integer.toBinaryString(Integer.parseInt(mailah));
                                    char ss[] =(new StringBuffer(s).reverse()).toString().toCharArray();
                                    for (int i = 1; i < ss.length; i++) {
                                        if (ss[i] == '1') {
                                            usermap = (JSONObject) msg_user.get(gkey);
                                            tuser = (HashMap<String, Object>) usermap.get(""+(i+1));
                                            if (tuser != null) {

                                                if ((Boolean) tuser.get("emailvalid")==true && (tuser.get("email").toString() != null))
                                                    umap.put(tuser.get("name").toString(), tuser.get("email").toString());
                                            }

                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                emailUtil sendmail = new emailUtil();
                                // logger.warn(sendmail.emailSend("系统信息", msg, umap) + ":" + umap.toString());

                            }


                        }


                        /////////////////////////////////
                    }


                }
            }

        } else
        {
            //logger.error("redis_key do not match mysql_kkey:"+key+",请确认！！！");
        }

    }

}