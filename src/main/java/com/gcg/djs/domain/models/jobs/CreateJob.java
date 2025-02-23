package com.gcg.djs.domain.models.jobs;

import com.gcg.djs.domain.models.errors.ErrorMessages;
import org.apache.logging.log4j.util.Strings;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record CreateJob (
        String name,
        String description,
        String binLocation,
        Instant nextExecution) {

    public static List<String> validate(CreateJob job) {
        List<String> errors = new ArrayList<>();

        if(job == null) {
            errors.add(ErrorMessages.CREATE_JOB_NULL);
            return errors;
        }

        if (Strings.isBlank(job.name())) {
            errors.add(ErrorMessages.JOB_NAME_INVALID);
        }

        if (Strings.isBlank(job.description())) {
            errors.add(ErrorMessages.JOB_DESCRIPTION_INVALID);
        }

        if (Strings.isBlank(job.binLocation())) {
            errors.add(ErrorMessages.JOB_BIN_LOCATION_INVALID);
        }

        if(job.nextExecution() == null) {
            errors.add(ErrorMessages.JOB_NEXT_EXECUTION_INVALID);
        }

        return errors;
    }
}