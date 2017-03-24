package com.gmail.vanyadubik.managerplus.service.gps;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;

import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_TASK;

public class TaskSchedure implements SmartScheduler.JobScheduledCallback{

    private int JobType, JobID, JobNetworkType;
    private boolean requiresCharging;
    private long intervalInSeconds;
    private Class<?> serviceClass;
    private Context mContext;
    private SmartScheduler jobScheduler;
    private Job job;

    private TaskSchedure(Context mContext, Class<?> serviceClass, int jobType, int jobID,
                        int jobNetworkType, boolean requiresCharging,
                        long intervalInSeconds) {
        this.JobType = jobType;
        this.JobID = jobID;
        this.JobNetworkType = jobNetworkType;
        this.requiresCharging = requiresCharging;
        this.intervalInSeconds = intervalInSeconds;
        this.serviceClass = serviceClass;
        this.mContext = mContext;
        jobScheduler = SmartScheduler.getInstance(mContext);
        job = new Job.Builder(JobID, this, JobType, TAGLOG_TASK)
                .setRequiredNetworkType(JobNetworkType)
                .setRequiresCharging(requiresCharging)
                .setIntervalMillis(1000 * intervalInSeconds)
                .setPeriodic(1000 * intervalInSeconds)
                .build();

    }

    public static class Builder {

        private int jobType, jobID, jobNetworkType;
        private boolean requiresCharging;
        private long intervalInSeconds;
        private Class<?> serviceClass;
        private Context mContext;

        public Builder(Class<?> serviceClass, Context mContext) {
            this.serviceClass = serviceClass;
            this.mContext = mContext;
        }

        public Builder jobType(int JobType) {
            this.jobType = JobType;
            return this;
        }
        public Builder jobID(int JobID) {
            this.jobID = JobID;
            return this;
        }
        public Builder jobNetworkType(int JobNetworkType) {
            this.jobNetworkType = JobNetworkType;
            return this;
        }
        public Builder requiresCharging(boolean requiresCharging) {
            this.requiresCharging = requiresCharging;
            return this;
        }
        public Builder interval(long intervalInSeconds) {
            this.intervalInSeconds = intervalInSeconds;
            return this;
        }


        public TaskSchedure build() {
            return new TaskSchedure(mContext, serviceClass, jobType, jobID, jobNetworkType,
                    requiresCharging, intervalInSeconds);
        }
    }

    @Override
    public void onJobScheduled(Context context, Job job) {
        if (jobScheduler.contains(JobID)) {
            Intent ishintent = new Intent(mContext, serviceClass);
            mContext.startService(ishintent);
        }
    }

    public void removeTask(){
        if (jobScheduler.contains(job.getJobId())) {
            if (jobScheduler.removeJob(job.getJobId())) {
                Log.d(TAGLOG_TASK, "Job successfully removed!");
            }
        }
    }

    public void startTask(){
        if (!jobScheduler.contains(job.getJobId())) {
            jobScheduler.addJob(job);
            Log.d(TAGLOG_TASK, "Job successfully added!");
        }
    }
}
