package com.gcg.djs.domain.services.jobs;

import com.gcg.djs.domain.common.QueryParameters;
import com.gcg.djs.domain.interfaces.external.ILog;
import com.gcg.djs.domain.interfaces.repositories.Repository;
import com.gcg.djs.domain.interfaces.services.IJobService;
import com.gcg.djs.domain.models.errors.*;
import com.gcg.djs.domain.models.jobs.*;
import com.gcg.djs.domain.common.Page;
import com.gcg.djs.domain.services.BaseService;

import java.time.Instant;
import java.util.*;

public final class JobService extends BaseService implements IJobService {
    private final Repository<Job> jobRepository;

    public JobService(
            Repository<Job> jobRepository, ILog log) {
        super(log);
        this.jobRepository = Objects.requireNonNull(jobRepository);
    }

    @Override
    public Job addJob(CreateJob createJob) throws ValidationException {
        var errors = CreateJob.validate(createJob);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        UUID jobId = UUID.randomUUID();
        Instant now = Instant.now();

        Job newJob = new Job(
                jobId,
                createJob.name(),
                createJob.description(),
                createJob.binLocation(),
                JobStatus.CREATED,
                now,
                now,
                null,
                null,
                createJob.nextExecution(),
                0,
                null
        );

        return executeWithErrorHandler(() -> jobRepository.create(newJob));
    }

    @Override
    public Job modifyJob(UUID id, UpdateJob updateJob) throws ValidationException {
        return executeWithErrorHandler(() -> {
            Job existingJob = fetchJobByIdOrThrow(id);

            Job updatedJob = new Job.Builder(existingJob)
                    .name(updateJob.name().isPresent() ?
                            updateJob.name().get() : existingJob.name())
                    .description(updateJob.description().isPresent() ?
                            updateJob.description().get() : existingJob.description())
                    .status(updateJob.status().isPresent() ?
                            updateJob.status().get() : existingJob.status())
                    .modifiedDate(Instant.now())
                    .nextExecution(updateJob.nextExecution().isPresent() ?
                            updateJob.nextExecution().get() : existingJob.nextExecution())
                    .retries(updateJob.retries().isPresent() ?
                            updateJob.retries().get() : existingJob.retries())
                    .error(updateJob.error().isPresent() ?
                            updateJob.error().get() : existingJob.error())
                    .build();

            return jobRepository.update(updatedJob);
        });
    }

    @Override
    public boolean removeJob(UUID id) throws ValidationException {
        return executeWithErrorHandler(() -> {
            fetchJobByIdOrThrow(id);
            return jobRepository.delete(id);
        });
    }

    @Override
    public Job getJobById(UUID id) throws ValidationException {
        return executeWithErrorHandler(() -> fetchJobByIdOrThrow(id));
    }

    @Override
    public Page<Job> searchJobs(int page, int pageSize, QueryParameters queryParameters) throws ValidationException {
        var errors = Page.validate(page, pageSize);

        if(!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return executeWithErrorHandler(() -> jobRepository.getPage(page, pageSize, queryParameters));
    }

    private Job fetchJobByIdOrThrow(UUID id) throws ValidationException {
        if(id == null) {
            throw new ValidationException(List.of(ErrorMessages.JOP_ID_NULL));
        }

        Job job = jobRepository.getById(id);
        if (job == null) {
            String errorMessage = String.format(ErrorMessages.JOB_NOT_FOUND, id);
            throw new ValidationException(List.of(errorMessage));
        }
        return job;
    }
}