package com.gcg.djs.domain.models.recurringjob;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public record RecurringJob(
        UUID id,
        String cronExpression,
        String name,
        String description,
        Instant createdDate,
        Instant modifiedDate,
        Boolean active,
        Instant nextRun,
        Instant lastRun
) {
}