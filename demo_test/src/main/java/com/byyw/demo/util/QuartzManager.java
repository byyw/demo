package com.byyw.demo.util;

import java.util.concurrent.TimeUnit;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzManager {
    public static void main(String[] args) throws Exception {
        // 1.创建调度器 Scheduler
        SchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();

        // 2.创建JobDetail实例，并与MyJob类绑定(Job执行内容)
        JobDetail job = JobBuilder.newJob(MyJob.class)
                .withIdentity("job1", "group1")
                .build();

        // 3.构建Trigger实例,每隔30s执行一次
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(1)
                        .repeatForever())
                .build();

        scheduler.start();

        scheduler.scheduleJob(job, trigger);
        Thread.sleep(5000);
        
        scheduler.scheduleJob(JobBuilder.newJob(MyJob.class)
                .withIdentity("job2", "group1")
                .build(),
                TriggerBuilder.newTrigger()
                        .withIdentity("trigger2", "group1")
                        .startNow()
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(1)
                                .repeatForever())
                        .build());

        Thread.sleep(60000);

        scheduler.shutdown();
    }

    public static class MyJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("任务被执行了。。。" + context.getJobDetail().getKey());
        }
    }
}
