package com.gcg.djs.infrastructure.cron;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.gcg.djs.domain.interfaces.external.ICronService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

public class CronService implements ICronService {
    @Override
    public Instant calculateNextRun(String cronExpression) {
        CronParser parser = new CronParser(
                CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));

        Cron cron = parser.parse(cronExpression);

        ExecutionTime executionTime = ExecutionTime.forCron(cron);

        Optional<ZonedDateTime> nextExecutionTime =
                executionTime.nextExecution(ZonedDateTime.now(ZoneId.systemDefault()));

        return nextExecutionTime.get().toInstant();
    }
}
