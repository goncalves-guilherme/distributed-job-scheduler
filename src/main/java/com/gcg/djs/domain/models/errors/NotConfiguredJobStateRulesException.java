package com.gcg.djs.domain.models.errors;

import com.gcg.djs.domain.models.jobs.JobStatus;

public class NotConfiguredJobStateRulesException extends RuntimeException {
    public NotConfiguredJobStateRulesException(JobStatus jobStatus) {
        super(String.format(
                "Error: The status '%s' is not configured in the state transition rules.",
                jobStatus));
    }
}
