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
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

public class QuartzManager {

    public static void main(String[] args) throws Exception {
        // 1.创建调度器 Scheduler
        SchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();
        scheduler.start();

        // JobDataMap jobDataMap = new JobDataMap();
        // JobDetail job = JobBuilder.newJob(MyJob.class)
        // .withIdentity("1", "1")
        // .usingJobData(jobDataMap)
        // .build();

        // Trigger tr = TriggerBuilder.newTrigger()
        // .withIdentity("0", "1")
        // .withSchedule(SimpleScheduleBuilder.simpleSchedule()
        // .withIntervalInMilliseconds(1000)
        // .withRepeatCount(1))
        // .build();

        // scheduler.scheduleJob(job, tr);
        int i = 1;
        while (true) {
            JobDataMap jobDataMap = new JobDataMap();
            JobDetail job = JobBuilder.newJob(MyJob.class)
                    .withIdentity("" + i, "1")
                    .usingJobData(jobDataMap)
                    .build();

            Trigger tr = TriggerBuilder.newTrigger()
                    .withIdentity("" + i, "1")
                    .startNow()
                    .build();

            scheduler.scheduleJob(job, tr);

            i++;
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class MyJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("任务被执行了。。。" + context.getJobDetail().getKey());
        }
    }
}
