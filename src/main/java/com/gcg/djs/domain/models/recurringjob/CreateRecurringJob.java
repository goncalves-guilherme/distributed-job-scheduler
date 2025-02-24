package com.gcg.djs.domain.models.recurringjob;

import com.gcg.djs.domain.models.errors.ErrorMessages;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

public record CreateRecurringJob(
        String cronExpression,
        String name,
        String description
) {
    public static List<String> validate(CreateRecurringJob recurringJob) {
        List<String> errors = new ArrayList<>();

        if(recurringJob == null) {
            errors.add(ErrorMessages.CREATE_RECURRING_JOB_NULL);
            return errors;
        }

        if (Strings.isBlank(recurringJob.name)) {
            errors.add(ErrorMessages.RECURRING_JOB_NAME_INVALID);
        }

        if (Strings.isBlank(recurringJob.description)) {
            errors.add(ErrorMessages.RECURRING_JOB_DESCRIPTION_INVALID);
        }

        if (Strings.isBlank(recurringJob.cronExpression)) {
            errors.add(ErrorMessages.RECURRING_JOB_CRON_INVALID);
        }

        return errors;
    }
}