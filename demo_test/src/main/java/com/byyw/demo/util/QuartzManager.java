package com.byyw.demo.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.units.qual.s;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.byyw.demo.util.ScheduleManager.TJob;

public class QuartzManager {

    public static void main(String[] args) throws Exception {
        // 1.创建调度器 Scheduler
        SchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();
        scheduler.start();

        JobDataMap jobDataMap = new JobDataMap();
        JobDetail job = JobBuilder.newJob(MyJob.class)
                .withIdentity("1", "1")
                .usingJobData(jobDataMap)
                .build();

        Trigger tr = TriggerBuilder.newTrigger()
                .withIdentity("1", "1")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMilliseconds(1000)
                        .withRepeatCount(0))
                .build();
        scheduler.scheduleJob(job, tr);
    }

    public static class MyJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("任务被执行了。。。" + context.getJobDetail().getKey());
        }
    }
}
