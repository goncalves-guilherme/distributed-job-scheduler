package com.gcg.djs.domain.handlers;

import com.gcg.djs.domain.interfaces.external.ICoordinator;
import com.gcg.djs.domain.interfaces.services.IJobSchedulerService;
import com.gcg.djs.domain.interfaces.services.IJobService;
import com.gcg.djs.domain.models.errors.ValidationException;

public class JobSchedulerHandler {
    private final IJobService jobsService;
    private final IJobSchedulerService jobSchedulerService;
    private final ICoordinator coordinator;

    public JobSchedulerHandler(
            IJobService jobsService,
            IJobSchedulerService jobSchedulerService,
            ICoordinator coordinator) {
        this.jobsService = jobsService;
        this.jobSchedulerService = jobSchedulerService;
        this.coordinator = coordinator;
    }

    public void execute() throws ValidationException {
        while (true) {
            if (this.coordinator.tryAcquireLeadership()) {
                processJobs();
            } else {
                sleep();
            }
        }
    }

    private void processJobs() throws ValidationException {
        try {
//            SearchJobFilters searchJobFilters = new SearchJobFilters.JobFiltersBuilder()
//                    .setStatus(JobStatus.CREATED)
//                    .build();
//
//            Page<Job> jobs = jobsService.searchJobs(1, 10, searchJobFilters);
//
//            for (Job job : jobs.items()) {
//                jobSchedulerService.enqueueJob(job);
//            }
        } catch (Exception e) {
            // Consider adding logging here
            handleException(e);
            throw e;
        }
    }

    private void processRetries() {}

    private void sleep() {
        try {
            // TODO Add parameter for 1000
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleException(Exception e) {
        // Handle exception (log or rethrow)
        // TODO: Add logging later
    }
}
