package com.gcg.djs.domain.models.jobs;

import java.time.Instant;

public record JobError(String errorMessage, String errorType, Instant errorTimestamp) {
}
