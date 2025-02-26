package com.gcg.djs.infrastructure.kafka;

import com.gcg.djs.domain.models.jobs.Job;

import java.util.UUID;

public final class ProtobufConverter {
    public static JobOuterClass.Job toProtobufJob(Job domainJob) {
        return JobOuterClass.Job.newBuilder()
                .setId(domainJob.id().toString())
                .setBinLocation(domainJob.binLocation())
                .build();
    }

    public static Job protobufToDomain(JobOuterClass.Job messageJob) {
        return new Job.Builder()
                .jobId(UUID.fromString(messageJob.getId()))
                .binLocation(messageJob.getBinLocation())
                .build();
    }
}
