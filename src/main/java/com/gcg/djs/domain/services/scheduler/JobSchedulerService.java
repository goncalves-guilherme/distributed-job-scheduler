package com.gcg.djs.domain.services.scheduler;

import com.gcg.djs.domain.interfaces.external.IMessageQueue;
import com.gcg.djs.domain.interfaces.repositories.Repository;
import com.gcg.djs.domain.interfaces.services.IJobSchedulerService;
import com.gcg.djs.domain.models.errors.InvalidJobStateTransitionException;
import com.gcg.djs.domain.models.errors.JobRetriesExceededException;
import com.gcg.djs.domain.models.errors.NotConfiguredJobStateRulesException;
import com.gcg.djs.domain.models.errors.ValidationException;
import com.gcg.djs.domain.models.jobs.Job;
import com.gcg.djs.domain.models.jobs.JobError;
import com.gcg.djs.domain.models.jobs.JobStatus;

import java.time.Instant;
import java.util.*;

public final class JobSchedulerService implements IJobSchedulerService {
    private final Repository<Job> jobRepository;
    private final IMessageQueue<Job> jobMessageQueue;

    private static final Map<JobStatus, Set<JobStatus>> stateTransitionRules =
            new EnumMap<>(JobStatus.class);

    static {

        stateTransitionRules.put(JobStatus.SCHEDULED,
                EnumSet.of(JobStatus.EXECUTING, JobStatus.FAILED));

        stateTransitionRules.put(JobStatus.EXECUTING,
                EnumSet.of(JobStatus.SCHEDULED, JobStatus.CANCELED, JobStatus.FAILED, JobStatus.COMPLETED));

        stateTransitionRules.put(JobStatus.DELETED,
                EnumSet.of(JobStatus.CREATED));

        stateTransitionRules.put(JobStatus.FAILED,
                EnumSet.of(JobStatus.EXECUTING));

        stateTransitionRules.put(JobStatus.CANCELED,
                EnumSet.of(JobStatus.EXECUTING, JobStatus.FAILED, JobStatus.SCHEDULED));

        stateTransitionRules.put(JobStatus.COMPLETED,
                EnumSet.of(JobStatus.EXECUTING));
    }

    public JobSchedulerService(
            Repository<Job> jobsRepository,
            IMessageQueue<Job> jobMessageQueue){

        Objects.requireNonNull(jobsRepository);
        Objects.requireNonNull(jobMessageQueue);

        this.jobRepository = jobsRepository;
        this.jobMessageQueue = jobMessageQueue;
    }

    public void enqueueJobs() throws ValidationException {

    }

    public void enqueueJob(Job job) throws ValidationException {
        JobStatus scheduledStatus = JobStatus.SCHEDULED;
        ensureValidStateTransition(job.status(), scheduledStatus);

        // TODO, set 5 as a configurable parameter
        if(job.retries() > 5) {
            throw new JobRetriesExceededException(job.id(), 5);
        }

        Job updatedJob = new Job.Builder(job)
                .status(scheduledStatus)
                .modifiedDate(Instant.now())
                .build();

        jobMessageQueue.EnqueueMessage(updatedJob);

        jobRepository.update(updatedJob);
    }

    public void markJobAsCompleted(Job job) throws ValidationException {
        JobStatus completedStatus = JobStatus.COMPLETED;
        ensureValidStateTransition(job.status(), completedStatus);

        Job updatedJob = new Job.Builder(job)
                .status(completedStatus)
                .modifiedDate(Instant.now())
                .build();

        // TODO commit message?

        jobRepository.update(updatedJob);
    }

    public void markJobAsFailed(Job job, JobError jobError) throws ValidationException {
        JobStatus failedStatus = JobStatus.FAILED;
        ensureValidStateTransition(job.status(), failedStatus);

        Job updatedJob = new Job.Builder(job)
                .status(failedStatus)
                .retries(job.retries() + 1)
                .error(jobError)
                .modifiedDate(Instant.now())
                .build();

        // TODO commit message? Add to a new queue?

        jobRepository.update(updatedJob);
    }

    public void cancelJob(Job job) throws ValidationException {
        JobStatus canceledStatus = JobStatus.CANCELED;
        ensureValidStateTransition(job.status(), canceledStatus);

        Job updatedJob = new Job.Builder(job)
                .status(canceledStatus)
                .modifiedDate(Instant.now())
                .build();

        jobRepository.update(updatedJob);
    }

    private static void ensureValidStateTransition(
            JobStatus currentJobStatus, JobStatus targetJobStatus){
        if (stateTransitionRules.containsKey(targetJobStatus)) {
            throw new NotConfiguredJobStateRulesException(targetJobStatus);
        }

        if(!stateTransitionRules.get(targetJobStatus).contains(currentJobStatus)) {
            throw new InvalidJobStateTransitionException(
                    currentJobStatus,
                    targetJobStatus,
                    stateTransitionRules.get(targetJobStatus).stream());
        }
    }
}
