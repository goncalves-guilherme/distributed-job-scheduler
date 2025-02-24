package com.gcg.djs.domain.interfaces.services;

import com.gcg.djs.domain.common.Page;
import com.gcg.djs.domain.common.QueryParameters;
import com.gcg.djs.domain.models.errors.ValidationException;
import com.gcg.djs.domain.models.recurringjob.CreateRecurringJob;
import com.gcg.djs.domain.models.recurringjob.RecurringJob;

import java.util.UUID;

public interface IRecurringJobService {
    RecurringJob createRecurringJob(CreateRecurringJob createRecurringJob) throws ValidationException;
    Page<RecurringJob> getRecurringJobsPage(int page, int pageSize, QueryParameters parameters) throws ValidationException;
    RecurringJob getRecurringJobById(UUID id) throws ValidationException;
    Page<RecurringJob> getNextRun(int page, int pageSize) throws ValidationException;
    boolean pauseJob(UUID id) throws ValidationException;
}