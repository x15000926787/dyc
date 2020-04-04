package com.dl.tool;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * [说明/描述]
 * @author Edon-Du
 * @date 2018-6-25 
 * @version 1.0
 * @copyright copyright (c) 2018
 */
 class SchedulerUtil {
	 private static final Logger logger = LogManager.getLogger(SchedulerUtil.class);
    /**
     * [简单任务调度:每次执行间隔为多少毫秒，执行多少次] <br>
     * @author Edon-Du <br>
     * @date 2018-6-25 <br>
     * @param jobName 任务名字
     * @param jobGroupName 任务组名字
     * @param triggerName触发器名字
     * @param triggerGroupName触发器组名字
     * @param jobClass任务类
     * @param intevalTime时间间隔
     * @param count执行几次<br>
     */
    public static void handleSimpleTrigger(String jobName, String jobGroupName,
            String triggerName, String triggerGroupName, Class jobClass,
            int time, int count) {
        // 通过schedulerFactory获取一个调度器
        SchedulerFactory schedulerfactory = new StdSchedulerFactory();
        Scheduler scheduler = null;
        try {
            // 通过schedulerFactory获取一个调度器
            scheduler = schedulerfactory.getScheduler();
            // 创建jobDetail实例，绑定Job实现类
            // 指明job的名称，所在组的名称，以及绑定job类
            JobDetail job = JobBuilder.newJob(jobClass)
                    .withIdentity(jobName, jobGroupName).build();
            // 定义调度触发规则
             //使用simpleTrigger规则
             Trigger trigger=TriggerBuilder.newTrigger().withIdentity(triggerName,
                     triggerGroupName)
             .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(time).withRepeatCount(count))
             .startNow().build();
            // 把作业和触发器注册到任务调度中
            scheduler.scheduleJob(job, trigger);
            // 启动调度
            scheduler.start();
        } catch (Exception e) {
            logger.warn("执行"+jobName+"组"+jobName+"任务出现异常E:["+ e.getMessage() + "]");
        }
    }
    /**
     * [复杂任务调度：每天几点几分几秒定时执行任务] <br>
     * @author xx <br>
     * @date 2018-6-25 <br>
     * @param jobName 任务名字
     * @param jobGroupName 任务组名字
     * @param triggerName 触发器名字
     * @param triggerGroupName 触发器组名字
     * @param jobClass 任务类
     * @param cron 触发规则<br>
     */
    static void hadleCronTrigger(String jobName, String jobGroupName,
            String triggerName, String triggerGroupName, Class jobClass,String cron, List<Map<String, Object>> taskstr) {
        // 通过schedulerFactory获取一个调度器
        SchedulerFactory schedulerfactory = new StdSchedulerFactory();
        Scheduler scheduler = null;
        try {
            // 通过schedulerFactory获取一个调度器
            scheduler = schedulerfactory.getScheduler();
            // 创建jobDetail实例，绑定Job实现类
            // 指明job的名称，所在组的名称，以及绑定job类
            JobDetail job = JobBuilder.newJob(jobClass)
                    .withIdentity(jobName, jobGroupName).build();
            // 定义调度触发规则
            //使用cornTrigger规则  每天18点30分  
          //放入菜单Id
			JobDataMap jobDataMap = job.getJobDataMap();
			jobDataMap.put("taskdetial", taskstr);
            Trigger trigger=TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName)  
            .withSchedule(CronScheduleBuilder.cronSchedule(cron))  
            .startNow().build();    
            // 把作业和触发器注册到任务调度中
            CronExpression cronExpression = new CronExpression(cron);
            logger.warn(jobName+","+jobGroupName+","+cronExpression.getNextValidTimeAfter(new Date()));
            scheduler.scheduleJob(job, trigger);
            // 启动调度
            scheduler.start();
        } catch (Exception e) {
            logger.warn("执行"+jobName+"组"+jobName+"任务出现异常E:["+ e.getMessage() + "]");
        }
    }
    /**
     * [复杂任务调度：每天几点几分几秒定时执行任务] <br>
     * @author xx <br>
     * @date 2018-6-25 <br>
     * @param jobName 任务名字
     * @param jobGroupName 任务组名字
     * @param triggerName 触发器名字
     * @param triggerGroupName 触发器组名字
     * @param jobClass 任务类
     * @param cron 触发规则<br>
     */
    static void hadleCronTrigger_obj(String jobName, String jobGroupName,
            String triggerName, String triggerGroupName, Class jobClass,String cron, JSONObject taskstr) {
        // 通过schedulerFactory获取一个调度器
        SchedulerFactory schedulerfactory = new StdSchedulerFactory();
        Scheduler scheduler = null;
        try {
            // 通过schedulerFactory获取一个调度器
            scheduler = schedulerfactory.getScheduler();
            // 创建jobDetail实例，绑定Job实现类
            // 指明job的名称，所在组的名称，以及绑定job类
            JobDetail job = JobBuilder.newJob(jobClass)
                    .withIdentity(jobName, jobGroupName).build();
            // 定义调度触发规则
            //使用cornTrigger规则  每天18点30分  
          //放入菜单Id
			JobDataMap jobDataMap = job.getJobDataMap();
			jobDataMap.put("taskdetial", taskstr);
            Trigger trigger=TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName)  
            .withSchedule(CronScheduleBuilder.cronSchedule(cron))  
            .startNow().build();    
            // 把作业和触发器注册到任务调度中
            CronExpression cronExpression = new CronExpression(cron);
            logger.warn(jobName+","+jobGroupName+","+cronExpression.getNextValidTimeAfter(new Date()));
            scheduler.scheduleJob(job, trigger);
            // 启动调度
            scheduler.start();
        } catch (Exception e) {
            logger.warn("执行"+jobName+"组"+jobName+"任务出现异常E:["+ e.getMessage() + "]");
        }
    }
 }
