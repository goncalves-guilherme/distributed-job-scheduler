package com.gcg.djs.domain.interfaces.services;

import com.gcg.djs.domain.models.errors.ValidationException;
import com.gcg.djs.domain.models.jobs.Job;
import com.gcg.djs.domain.models.jobs.JobError;

public interface IJobSchedulerService {
    void enqueueJob(Job job) throws ValidationException;
    void markJobAsCompleted(Job job) throws ValidationException;
    void markJobAsFailed(Job job, JobError jobError) throws ValidationException;
    void cancelJob(Job job) throws ValidationException;
}
