package com.gcg.djs.domain.interfaces.services;

import com.gcg.djs.domain.common.Page;
import com.gcg.djs.domain.models.recurringjob.RecurringJob;

import java.util.UUID;

public interface IRecurringJobService {
    RecurringJob createJob(RecurringJob job);
    RecurringJob updateJob(RecurringJob job);
    boolean deleteJob(UUID id);
    Page<RecurringJob> getRecurringJobsPage();
    RecurringJob getJobById(UUID id);
    Page<RecurringJob> getNextRun();
    boolean pauseJob(UUID id);
}