package com.gcg.djs.domain.models.jobs;

import java.time.Instant;
import java.util.Optional;

public record UpdateJob(
        Optional<String> name,
        Optional<String> description,
        Optional<JobStatus> status,
        Optional<Instant> nextExecution,
        Optional<Integer> retries,
        Optional<JobError> error) {

}