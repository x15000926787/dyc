package com.dl.tool;

import com.alibaba.fastjson.JSONObject;
import com.bjsxt.thread.ThreadSubscriber;
import com.dl.quartz.LuaJob;
import com.dl.quartz.QuartzJob;
import com.dl.quartz.QuartzManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public final class FirstClass implements ServletContextListener {
    private ThreadSubscriber T4 = null;

    public static String projectId;

    public String url;

    public static ScriptEngineManager manager = new ScriptEngineManager();

    public static ScriptEngine engine = manager.getEngineByName("nashorn");

    public static final Logger logger = LogManager.getLogger(FirstClass.class);

    private WebApplicationContext springContext;

    public static JdbcTemplate jdbcTemplate;

    public static JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public static void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        FirstClass.jdbcTemplate = jdbcTemplate;
    }

    public static ThreadPoolExecutor executor = null;

    public FirstClass() {
        executor = new ThreadPoolExecutor(10, 20, 60000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.allowCoreThreadTimeOut(true);
    }

    public void contextDestroyed(ServletContextEvent arg0) {
        QuartzManager.shutdownJobs();
        try {
            Thread.sleep(30000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
             this.T4.stoplisten();
             this.T4.killThreadByName("lisner_data");
             logger.warn("应用程序关闭!");
    }

    public void contextInitialized(ServletContextEvent arg0) {
             logger.warn("初始化系统服务...");
             projectId = PropertyUtil.getProperty("project_id");
             this.springContext = WebApplicationContextUtils.getWebApplicationContext(arg0.getServletContext());
             if (this.springContext != null) {
                   jdbcTemplate = (JdbcTemplate)this.springContext.getBean("jdbcTemplate");
        } else {
                   logger.warn("获取应用程序上下文失败!");
            return;
        }
             String jsonstr = null;
             JSONObject job = new JSONObject();
             AnaUtil.loadAna_v();
             AnaUtil.loaddev();
             AnaUtil.load_condition();
             AnaUtil.load_msguser();
             AnaUtil.load_fullname();
             AnaUtil.load_onlinewarn();
             AnaUtil.saveno_kkey();
             int i = 0;
             logger.warn("实时数据监听开启");
             this.T4 = new ThreadSubscriber("lisner_data");
             this.T4.start();
             String sql = "select * from timetask ";
             List<Map<String, Object>> tasktype1 = new ArrayList<>();
        try {
                   tasktype1 = getJdbcTemplate().queryForList(sql);
                   for (Map<String, Object> tmap : tasktype1) {
                Map<String, String> maps;
                Map<String, Object> fmap, xmap;
                         i = ((Integer)tmap.get("id")).intValue();
                         switch (((Integer)tmap.get("type")).intValue()) {
                    case 1:
                                     maps = new HashMap<>();
                                     maps.put("vv", "" + i);
                                    QuartzManager.addJob("qjob" + i, QuartzJob.class, tmap.get("cronstr").toString(), maps);
                                     logger.warn("定时ao/do任务:" + tmap.get("cronstr").toString() + maps.toString());
                        break;
                    case 2:
                                     fmap = new HashMap<>();
                                     logger.warn("存储5分钟历史数据任务:" + tmap.get("cronstr").toString());
                                     fmap.put("luaname", tmap.get("luaname").toString());
                                     logger.warn("定时脚本任务:" + tmap.get("cronstr").toString() + fmap.toString());
                                     QuartzManager.addJob("ljob" + i, LuaJob.class, tmap.get("cronstr").toString(), fmap);
                        break;
                    case 3:
                                     if (!PropertyUtil.getProperty("mqtt.clientId").matches("webClientId_1")) {
                                       QuartzManager.addJob("saveHistyc", saveHistyc.class, tmap.get("cronstr").toString());
                                       logger.warn("存储5分钟历史数据任务:" + tmap.get("cronstr").toString());
                    }
                        break;
                    case 12:
                                     if (PropertyUtil.getProperty("project_id").matches("1")) {
                                       QuartzManager.addJob("saveRedis", saveRedis.class, tmap.get("cronstr").toString());
                                       logger.warn("存储5分钟redis数据任务:" + tmap.get("cronstr").toString());
                    }
                        break;
                    case 4:
                                     QuartzManager.addJob("UpdateDataJob", UpdateDataJob.class, tmap.get("cronstr").toString());
                                     logger.warn("请求实时数据任务:" + tmap.get("cronstr").toString());
                        break;
                    case 5:
                                     QuartzManager.addJob("checkonlinetime", checkOnlineTime.class, tmap.get("cronstr").toString());
                                     logger.warn("check在线时长任务:" + tmap.get("cronstr").toString());
                        break;
                    case 6:
                                     QuartzManager.addJob("reportjob", ReportJob.class, tmap.get("cronstr").toString());
                                     logger.warn("yc报表任务:" + tmap.get("cronstr").toString());
                        break;
                    case 8:
                                     QuartzManager.addJob("reportdnjob", ReportdnJob.class, tmap.get("cronstr").toString());
                                     logger.warn("dn报表任务:" + tmap.get("cronstr").toString());
                        break;
                    case 10:
                                     QuartzManager.addJob("reportdnjob", ReportDycJob.class, tmap.get("cronstr").toString());
                                     logger.warn("大悦城报表任务:" + tmap.get("cronstr").toString());
                        break;
                    case 7:
                                     QuartzManager.addJob("maxmindayjob", saveMaxMin.class, tmap.get("cronstr").toString());
                                     logger.warn("daymaxmin任务:" + tmap.get("cronstr").toString());
                        break;
                    case 11:
                                     xmap = new HashMap<>();
                                     xmap.put("txtname", tmap.get("luaname").toString());
                                     QuartzManager.addJob("zjdyjob", zjdyJob.class, tmap.get("cronstr").toString());
                                     logger.warn("电量总加任务:" + tmap.get("cronstr").toString());
                        break;
                    case 13:
                                     if (PropertyUtil.getProperty("project_id").matches("0")) {
                                       QuartzManager.addJob("devZF", devZF.class, tmap.get("cronstr").toString());
                                       logger.warn("机动车转发数据任务:" + tmap.get("cronstr").toString());
                    }
                        break;
                }
                         i++;
            }
                 } catch (Exception e) {
                   logger.error("生成定时任务出错了" + e.toString());
        }
             tasktype1 = null;
    }
}


