package com.gcg.djs.domain.models.errors;

import java.util.UUID;

public class JobRetriesExceededException extends RuntimeException {
    public JobRetriesExceededException(UUID jobId, int maxRetries) {
        super(String.format("Job with Id: %s exceeded the number of retries %s", jobId, maxRetries));
    }
}
