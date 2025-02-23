package com.gcg.djs.domain.models.jobs;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Job (
        UUID id,
        String name,
        String description,
        String binLocation,
        JobStatus status,
        Instant createdDate,
        Instant modifiedDate,
        Instant executionStart,
        Instant executionEnd,
        Instant nextExecution,
        int retries,
        JobError error) {

    public static class Builder {
        private UUID jobId;
        private String name;
        private String description;
        private String binLocation;
        private JobStatus status;
        private Instant createdDate;
        private Instant modifiedDate;
        private Instant executionStart;
        private Instant executionEnd;
        private Instant nextExecution;
        private int retries;
        private JobError error;

        public Builder() {}

        public Builder(Job job) {
            this.jobId = job.id;
            this.name = job.name;
            this.description = job.description;
            this.binLocation = job.binLocation;
            this.status = job.status;
            this.createdDate = job.createdDate;
            this.modifiedDate = job.modifiedDate;
            this.executionStart = job.executionStart;
            this.executionEnd = job.executionEnd;
            this.nextExecution = job.nextExecution;
            this.retries = job.retries;
            this.error = job.error;
        }

        public Builder jobId(UUID jobId) {
            this.jobId = jobId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder binLocation(String binLocation) {
            this.binLocation = binLocation;
            return this;
        }

        public Builder status(JobStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdDate(Instant createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder modifiedDate(Instant modifiedDate) {
            this.modifiedDate = modifiedDate;
            return this;
        }

        public Builder executionStart(Instant executionStart) {
            this.executionStart = executionStart;
            return this;
        }

        public Builder executionEnd(Instant executionEnd) {
            this.executionEnd = executionEnd;
            return this;
        }

        public Builder nextExecution(Instant nextExecution) {
            this.nextExecution = nextExecution;
            return this;
        }

        public Builder retries(int retries) {
            this.retries = retries;
            return this;
        }

        public Builder error(JobError error) {
            this.error = error;
            return this;
        }

        public Job build() {
            return new Job(
                    jobId,
                    name,
                    description,
                    binLocation,
                    status,
                    createdDate,
                    modifiedDate,
                    executionStart,
                    executionEnd,
                    nextExecution,
                    retries,
                    error
            );
        }
    }
}