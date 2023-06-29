package com.byyw.demo.util;

import java.util.Date;
import java.util.function.Function;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import lombok.Data;

public class ScheduleManager {
    private static Scheduler scheduler;
    static {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    




    @Data
    public static class TObject {
        private String key;

        private Long createOutTime;
        private Long writeOutTime;
        private Long readOutTime;
        private Function<TObject, Object> createOutTimeEvent;
        private Function<TObject, Object> writeOutTimeEvent;
        private Function<TObject, Object> readOutTimeEvent;
        private JobDetail createOutTimeJob;
        private JobDetail writeOutTimeJob;
        private JobDetail readOutTimeJob;

        public TObject(String key) {
            this.key = key;
        }

        public TObject createOutTimeEvent(Function<TObject, Object> createOutTimeEvent, Long createOutTime) {
            this.createOutTimeEvent = createOutTimeEvent;
            this.createOutTime = createOutTime;
            return this;
        }

        public TObject writeOutTimeEvent(Function<TObject, Object> writeOutTimeEvent, Long writeOutTime) {
            this.writeOutTimeEvent = writeOutTimeEvent;
            this.writeOutTime = writeOutTime;
            return this;
        }

        public TObject readOutTimeEvent(Function<TObject, Object> readOutTimeEvent, Long readOutTime) {
            this.readOutTimeEvent = readOutTimeEvent;
            this.readOutTime = readOutTime;
            return this;
        }
    }

    public static void addJob(TObject o) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("obj", o);
        if (o.getCreateOutTime() != null && o.getCreateOutTimeEvent() != null) {
            JobDetail job = add(o, "createOutTimeEvent", o.getCreateOutTime());
            o.setCreateOutTimeJob(job);
        } else if (o.getWriteOutTime() != null && o.getWriteOutTimeEvent() != null) {
            JobDetail job = add(o, "writeOutTimeEvent", o.getWriteOutTime());
            o.setWriteOutTimeJob(job);
        } else if (o.getReadOutTime() != null && o.getReadOutTimeEvent() != null) {
            JobDetail job = add(o, "readOutTimeEvent", o.getReadOutTime());
            o.setReadOutTimeJob(job);
        }
    }

    public static void removeJob(JobDetail job) throws SchedulerException {
        scheduler.deleteJob(job.getKey());
    }

    public static void resetJob(JobDetail job) throws SchedulerException {
        scheduler.deleteJob(job.getKey());
        add(job, job.getJobDataMap().getString("event"), job.getJobDataMap().getLong("time"));
    }

    private static JobDetail add(TObject o, String event, Long time) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("obj", o);
        JobDetail job = JobBuilder.newJob(TJob.class)
                .withIdentity(event + "_" + o.getKey(), "group")
                .usingJobData(jobDataMap)
                .usingJobData("event", event)
                .usingJobData("time", time)
                .build();
        add(job, event, time);
        return job;
    }

    private static void add(JobDetail job, String event, Long time) throws SchedulerException {
        scheduler.scheduleJob(job,
                TriggerBuilder.newTrigger()
                        .withIdentity(job.getKey().getName(), "group")
                        .startAt(new Date(System.currentTimeMillis() + time))
                        .build());
    }

    public static class TJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            TObject obj = (TObject) jobDataMap.get("obj");
            String event = jobDataMap.getString("event");
            switch (event) {
                case "createOutTimeEvent":
                    obj.getCreateOutTimeEvent().apply(obj);
                    break;
                case "writeOutTimeEvent":
                    obj.getWriteOutTimeEvent().apply(obj);
                    break;
                case "readOutTimeEvent":
                    obj.getReadOutTimeEvent().apply(obj);
                    break;
            }
        }
    }

}