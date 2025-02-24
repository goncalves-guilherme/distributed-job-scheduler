package com.gcg.djs.domain.services.recurringjobs;

import com.gcg.djs.domain.common.Page;
import com.gcg.djs.domain.common.QueryParameters;
import com.gcg.djs.domain.common.QueryParametersBuilder;
import com.gcg.djs.domain.interfaces.external.ICronService;
import com.gcg.djs.domain.interfaces.external.ILog;
import com.gcg.djs.domain.interfaces.repositories.Repository;
import com.gcg.djs.domain.interfaces.services.IRecurringJobService;
import com.gcg.djs.domain.models.errors.ErrorMessages;
import com.gcg.djs.domain.models.errors.ValidationException;
import com.gcg.djs.domain.models.recurringjob.CreateRecurringJob;
import com.gcg.djs.domain.models.recurringjob.RecurringJob;
import com.gcg.djs.domain.services.BaseService;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RecurringJobService extends BaseService implements IRecurringJobService {
    private final Repository<RecurringJob> recurringRepository;
    private final ICronService cronService;

    public RecurringJobService(
            ILog log, Repository<RecurringJob> repository, ICronService cronService) {
        super(log);
        this.recurringRepository = repository;
        this.cronService = cronService;
    }

    @Override
    public RecurringJob createRecurringJob(
            CreateRecurringJob createRecurringJob) throws ValidationException {

        var errors = CreateRecurringJob.validate(createRecurringJob);

        if(!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        Instant nextRun = cronService.calculateNextRun(createRecurringJob.cronExpression());

        RecurringJob recurringJob = new RecurringJob(
                UUID.randomUUID(),
                createRecurringJob.cronExpression(),
                createRecurringJob.name(),
                createRecurringJob.description(),
                Instant.now(),
                Instant.now(),
                true,
                nextRun,
                null
        );

        return executeWithErrorHandler(() -> this.recurringRepository.create(recurringJob));
    }

    @Override
    public Page<RecurringJob> getRecurringJobsPage(
            int page, int pageSize, QueryParameters parameters) throws ValidationException {

        var errors = Page.validate(page, pageSize);

        if(!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return executeWithErrorHandler(
                () -> this.recurringRepository.getPage(page, pageSize, parameters));
    }

    @Override
    public RecurringJob getRecurringJobById(UUID id) throws ValidationException {
        if(Objects.isNull(id)) {
            throw new ValidationException(List.of(ErrorMessages.RECURRING_JOP_ID_NULL));
        }

        return executeWithErrorHandler(() -> {
            var recurring = this.recurringRepository.getById(id);

            if (recurring == null) {
                throw new ValidationException(List.of(String.format(ErrorMessages.RECURRING_JOB_NOT_FOUND, id)));
            }

            return recurring;
        });
    }

    @Override
    public Page<RecurringJob> getNextRun(int page, int pageSize) throws ValidationException {
        var errors = Page.validate(page, pageSize);

        if(!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        var parameters = new QueryParametersBuilder()
                .and()
                    .lessThanOrEqual("nextRun", Instant.now())
                .build();

        return executeWithErrorHandler(
                () -> this.recurringRepository.getPage(page, pageSize, parameters));
    }

    @Override
    public boolean pauseJob(UUID id) throws ValidationException {
        var recurringJob = getRecurringJobById(id);

        var changed = new RecurringJob(
                recurringJob.id(),
                recurringJob.cronExpression(),
                recurringJob.name(),
                recurringJob.description(),
                recurringJob.createdDate(),
                recurringJob.modifiedDate(),
                false,
                recurringJob.nextRun(),
                recurringJob.lastRun()
        );

        return executeWithErrorHandler(() -> {
            this.recurringRepository.update(changed);
            return true;
        });
    }
}
